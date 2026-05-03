package com.lokodom.alquilatucoche.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.lokodom.alquilatucoche.ui.screens.*

sealed class Screen(val route: String) {

    object Login : Screen("login")

    object Ofertas : Screen("ofertas/{token}") {
        fun createRoute(token: String) = "ofertas/$token"
        const val ARG_TOKEN = "token"
    }

    object OfertaDetail : Screen("oferta/{token}/{ofertaId}") {
        fun createRoute(token: String, ofertaId: Long) = "oferta/$token/$ofertaId"
        const val ARG_TOKEN = "token"
        const val ARG_OFERTA_ID = "ofertaId"
    }

    object VehiculoDetail : Screen("vehiculo/{token}/{vehiculoId}/{ofertaId}") {
        fun createRoute(token: String, vehiculoId: Long, ofertaId: Long) =
            "vehiculo/$token/$vehiculoId/$ofertaId"
        const val ARG_TOKEN = "token"
        const val ARG_VEHICULO_ID = "vehiculoId"
        const val ARG_OFERTA_ID = "ofertaId"
    }

    object PropietarioProfile : Screen("propietario/{token}/{propietarioId}") {
        fun createRoute(token: String, propietarioId: Long) = "propietario/$token/$propietarioId"
        const val ARG_TOKEN = "token"
        const val ARG_PROPIETARIO_ID = "propietarioId"
    }

    object StripePayment : Screen("pago/{token}/{ofertaId}/{usuarioId}/{dias}") {
        fun createRoute(token: String, ofertaId: Long, usuarioId: Long, dias: Int) =
            "pago/$token/$ofertaId/$usuarioId/$dias"
        const val ARG_TOKEN = "token"
        const val ARG_OFERTA_ID = "ofertaId"
        const val ARG_USUARIO_ID = "usuarioId"
        const val ARG_DIAS = "dias"
    }

    object ReservaSuccess : Screen("reserva-success/{token}/{reservaId}") {
        fun createRoute(token: String, reservaId: Long) = "reserva-success/$token/$reservaId"
        const val ARG_TOKEN = "token"
        const val ARG_RESERVA_ID = "reservaId"
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { token ->
                    navController.navigate(Screen.Ofertas.createRoute(token)) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.Ofertas.route,
            arguments = listOf(navArgument(Screen.Ofertas.ARG_TOKEN) { type = NavType.StringType })
        ) { backStack ->
            val token = backStack.arguments?.getString(Screen.Ofertas.ARG_TOKEN) ?: ""
            OfertasScreen(
                token = token,
                onOfertaClick = { ofertaId ->
                    navController.navigate(Screen.OfertaDetail.createRoute(token, ofertaId))
                }
            )
        }

        composable(
            route = Screen.OfertaDetail.route,
            arguments = listOf(
                navArgument(Screen.OfertaDetail.ARG_TOKEN) { type = NavType.StringType },
                navArgument(Screen.OfertaDetail.ARG_OFERTA_ID) { type = NavType.LongType }
            )
        ) { backStack ->
            val token = backStack.arguments?.getString(Screen.OfertaDetail.ARG_TOKEN) ?: ""
            val ofertaId = backStack.arguments?.getLong(Screen.OfertaDetail.ARG_OFERTA_ID) ?: 0L
            OfertaDetailScreen(
                token = token,
                ofertaId = ofertaId,
                onVerVehiculo = { vehiculoId ->
                    navController.navigate(
                        Screen.VehiculoDetail.createRoute(token, vehiculoId, ofertaId)
                    )
                },
                onReservar = { usuarioId, dias ->
                    navController.navigate(
                        Screen.StripePayment.createRoute(token, ofertaId, usuarioId, dias)
                    )
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.VehiculoDetail.route,
            arguments = listOf(
                navArgument(Screen.VehiculoDetail.ARG_TOKEN) { type = NavType.StringType },
                navArgument(Screen.VehiculoDetail.ARG_VEHICULO_ID) { type = NavType.LongType },
                navArgument(Screen.VehiculoDetail.ARG_OFERTA_ID) { type = NavType.LongType }
            )
        ) { backStack ->
            val token = backStack.arguments?.getString(Screen.VehiculoDetail.ARG_TOKEN) ?: ""
            val vehiculoId = backStack.arguments?.getLong(Screen.VehiculoDetail.ARG_VEHICULO_ID) ?: 0L
            VehiculoDetailScreen(
                token = token,
                vehiculoId = vehiculoId,
                onVerPerfil = { propietarioId ->
                    navController.navigate(
                        Screen.PropietarioProfile.createRoute(token, propietarioId)
                    )
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.PropietarioProfile.route,
            arguments = listOf(
                navArgument(Screen.PropietarioProfile.ARG_TOKEN) { type = NavType.StringType },
                navArgument(Screen.PropietarioProfile.ARG_PROPIETARIO_ID) { type = NavType.LongType }
            )
        ) { backStack ->
            val token = backStack.arguments?.getString(Screen.PropietarioProfile.ARG_TOKEN) ?: ""
            val propietarioId = backStack.arguments?.getLong(Screen.PropietarioProfile.ARG_PROPIETARIO_ID) ?: 0L
            PropietarioScreen(
                token = token,
                propietarioId = propietarioId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.StripePayment.route,
            arguments = listOf(
                navArgument(Screen.StripePayment.ARG_TOKEN) { type = NavType.StringType },
                navArgument(Screen.StripePayment.ARG_OFERTA_ID) { type = NavType.LongType },
                navArgument(Screen.StripePayment.ARG_USUARIO_ID) { type = NavType.LongType },
                navArgument(Screen.StripePayment.ARG_DIAS) { type = NavType.IntType }
            )
        ) { backStack ->
            val token = backStack.arguments?.getString(Screen.StripePayment.ARG_TOKEN) ?: ""
            val ofertaId = backStack.arguments?.getLong(Screen.StripePayment.ARG_OFERTA_ID) ?: 0L
            val usuarioId = backStack.arguments?.getLong(Screen.StripePayment.ARG_USUARIO_ID) ?: 0L
            val dias = backStack.arguments?.getInt(Screen.StripePayment.ARG_DIAS) ?: 1
            StripePaymentScreen(
                token = token,
                ofertaId = ofertaId,
                usuarioId = usuarioId,
                diasContratados = dias,
                onPagoExitoso = { reservaId ->
                    navController.navigate(Screen.ReservaSuccess.createRoute(token, reservaId)) {
                        popUpTo(Screen.Ofertas.route)
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.ReservaSuccess.route,
            arguments = listOf(
                navArgument(Screen.ReservaSuccess.ARG_TOKEN) { type = NavType.StringType },
                navArgument(Screen.ReservaSuccess.ARG_RESERVA_ID) { type = NavType.LongType }
            )
        ) { backStack ->
            val token = backStack.arguments?.getString(Screen.ReservaSuccess.ARG_TOKEN) ?: ""
            val reservaId = backStack.arguments?.getLong(Screen.ReservaSuccess.ARG_RESERVA_ID) ?: 0L
            ReservaSuccessScreen(
                reservaId = reservaId,
                onVolverAOfertas = {
                    navController.navigate(Screen.Ofertas.createRoute(token)) {
                        popUpTo(Screen.Ofertas.route) { inclusive = true }
                    }
                }
            )
        }
    }
}