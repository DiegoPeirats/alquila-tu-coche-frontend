package com.lokodom.alquilatucoche.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.lokodom.alquilatucoche.session.SessionManager
import com.lokodom.alquilatucoche.ui.screens.*
import java.net.URLDecoder
import java.net.URLEncoder

sealed class Screen(val route: String) {

    object Login : Screen("login")
    object Registro : Screen("registro")

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

    object MiPerfil : Screen("mi-perfil/{token}") {
        fun createRoute(token: String) = "mi-perfil/$token"
        const val ARG_TOKEN = "token"
    }

    object MisReservas : Screen("mis-reservas/{token}") {
        fun createRoute(token: String) = "mis-reservas/$token"
        const val ARG_TOKEN = "token"
    }

    object MisOfertas : Screen("mis-ofertas/{token}") {
        fun createRoute(token: String) = "mis-ofertas/$token"
        const val ARG_TOKEN = "token"
    }

    /**
     * Pago:
     * - precio se pasa como Int (céntimos × 100) para evitar pérdida de precisión Float
     * - fechas van URLEncoded para evitar problemas con "/" en la ruta
     */
    object StripePayment : Screen(
        "pago/{token}/{ofertaId}/{usuarioId}/{dias}/{precioCentimos}/{fechaInicio}/{fechaFin}"
    ) {
        fun createRoute(
            token: String,
            ofertaId: Long,
            usuarioId: Long,
            dias: Int,
            precioPorDia: Double,
            fechaInicio: String,
            fechaFin: String
        ): String {
            val precioCentimos = (precioPorDia * 100).toInt()
            val fi = URLEncoder.encode(fechaInicio, "UTF-8")
            val ff = URLEncoder.encode(fechaFin, "UTF-8")
            return "pago/$token/$ofertaId/$usuarioId/$dias/$precioCentimos/$fi/$ff"
        }

        const val ARG_TOKEN = "token"
        const val ARG_OFERTA_ID = "ofertaId"
        const val ARG_USUARIO_ID = "usuarioId"
        const val ARG_DIAS = "dias"
        const val ARG_PRECIO_CENTIMOS = "precioCentimos"
        const val ARG_FECHA_INICIO = "fechaInicio"
        const val ARG_FECHA_FIN = "fechaFin"
    }

    /**
     * Éxito de reserva — todos los datos como argumentos para mostrarse
     * en ReservaSuccessScreen sin necesidad de ViewModel adicional.
     * totalCentimos = total en centésimas de euro (evita Float)
     */
    object ReservaSuccess : Screen(
        "reserva-success/{token}/{reservaId}/{totalCentimos}/{dias}/{fechaInicio}/{fechaFin}"
    ) {
        fun createRoute(
            token: String,
            reservaId: Long,
            totalPagado: Double,
            dias: Int,
            fechaInicio: String,
            fechaFin: String
        ): String {
            val totalCentimos = (totalPagado * 100).toInt()
            val fi = URLEncoder.encode(fechaInicio, "UTF-8")
            val ff = URLEncoder.encode(fechaFin, "UTF-8")
            return "reserva-success/$token/$reservaId/$totalCentimos/$dias/$fi/$ff"
        }

        const val ARG_TOKEN = "token"
        const val ARG_RESERVA_ID = "reservaId"
        const val ARG_TOTAL_CENTIMOS = "totalCentimos"
        const val ARG_DIAS = "dias"
        const val ARG_FECHA_INICIO = "fechaInicio"
        const val ARG_FECHA_FIN = "fechaFin"
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    fun logout() {
        SessionManager.cerrarSesion()
        navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } }
    }

    fun goToOfertas(token: String) {
        navController.navigate(Screen.Ofertas.createRoute(token)) {
            popUpTo(Screen.Ofertas.route) { inclusive = true }
            launchSingleTop = true
        }
    }

    NavHost(navController = navController, startDestination = Screen.Login.route) {

        // ── LOGIN ────────────────────────────────────────────────────────
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { token ->
                    navController.navigate(Screen.Ofertas.createRoute(token)) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegistro = { navController.navigate(Screen.Registro.route) }
            )
        }

        // ── REGISTRO ─────────────────────────────────────────────────────
        composable(Screen.Registro.route) {
            RegistroScreen(
                onRegistroExitoso = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        // ── OFERTAS ──────────────────────────────────────────────────────
        composable(
            Screen.Ofertas.route,
            arguments = listOf(navArgument(Screen.Ofertas.ARG_TOKEN) { type = NavType.StringType })
        ) { bs ->
            val token = bs.arguments?.getString(Screen.Ofertas.ARG_TOKEN) ?: ""
            OfertasScreen(
                token = token,
                onOfertaClick = { navController.navigate(Screen.OfertaDetail.createRoute(token, it)) },
                onNavigateToOfertas = { goToOfertas(token) },
                onNavigateToMiPerfil = { navController.navigate(Screen.MiPerfil.createRoute(token)) },
                onNavigateToMisReservas = { navController.navigate(Screen.MisReservas.createRoute(token)) },
                onNavigateToMisOfertas = { navController.navigate(Screen.MisOfertas.createRoute(token)) },
                onLogout = { logout() }
            )
        }

        // ── DETALLE OFERTA ───────────────────────────────────────────────
        composable(
            Screen.OfertaDetail.route,
            arguments = listOf(
                navArgument(Screen.OfertaDetail.ARG_TOKEN) { type = NavType.StringType },
                navArgument(Screen.OfertaDetail.ARG_OFERTA_ID) { type = NavType.LongType }
            )
        ) { bs ->
            val token = bs.arguments?.getString(Screen.OfertaDetail.ARG_TOKEN) ?: ""
            val ofertaId = bs.arguments?.getLong(Screen.OfertaDetail.ARG_OFERTA_ID) ?: 0L
            OfertaDetailScreen(
                token = token,
                ofertaId = ofertaId,
                onVerVehiculo = { vehiculoId ->
                    navController.navigate(Screen.VehiculoDetail.createRoute(token, vehiculoId, ofertaId))
                },
                onReservar = { usuarioId, fechaInicio, fechaFin, dias, precioPorDia ->
                    navController.navigate(
                        Screen.StripePayment.createRoute(
                            token, ofertaId, usuarioId, dias, precioPorDia, fechaInicio, fechaFin
                        )
                    )
                },
                onBack = { navController.popBackStack() },
                onNavigateToOfertas = { goToOfertas(token) },
                onNavigateToMiPerfil = { navController.navigate(Screen.MiPerfil.createRoute(token)) },
                onNavigateToMisReservas = { navController.navigate(Screen.MisReservas.createRoute(token)) },
                onNavigateToMisOfertas = { navController.navigate(Screen.MisOfertas.createRoute(token)) },
                onLogout = { logout() }
            )
        }

        // ── DETALLE VEHÍCULO ─────────────────────────────────────────────
        composable(
            Screen.VehiculoDetail.route,
            arguments = listOf(
                navArgument(Screen.VehiculoDetail.ARG_TOKEN) { type = NavType.StringType },
                navArgument(Screen.VehiculoDetail.ARG_VEHICULO_ID) { type = NavType.LongType },
                navArgument(Screen.VehiculoDetail.ARG_OFERTA_ID) { type = NavType.LongType }
            )
        ) { bs ->
            val token = bs.arguments?.getString(Screen.VehiculoDetail.ARG_TOKEN) ?: ""
            val vehiculoId = bs.arguments?.getLong(Screen.VehiculoDetail.ARG_VEHICULO_ID) ?: 0L
            VehiculoDetailScreen(
                token = token, vehiculoId = vehiculoId,
                onVerPerfil = { navController.navigate(Screen.PropietarioProfile.createRoute(token, it)) },
                onBack = { navController.popBackStack() },
                onNavigateToOfertas = { goToOfertas(token) },
                onNavigateToMiPerfil = { navController.navigate(Screen.MiPerfil.createRoute(token)) },
                onNavigateToMisReservas = { navController.navigate(Screen.MisReservas.createRoute(token)) },
                onNavigateToMisOfertas = { navController.navigate(Screen.MisOfertas.createRoute(token)) },
                onLogout = { logout() }
            )
        }

        // ── PROPIETARIO ──────────────────────────────────────────────────
        composable(
            Screen.PropietarioProfile.route,
            arguments = listOf(
                navArgument(Screen.PropietarioProfile.ARG_TOKEN) { type = NavType.StringType },
                navArgument(Screen.PropietarioProfile.ARG_PROPIETARIO_ID) { type = NavType.LongType }
            )
        ) { bs ->
            val token = bs.arguments?.getString(Screen.PropietarioProfile.ARG_TOKEN) ?: ""
            val propietarioId = bs.arguments?.getLong(Screen.PropietarioProfile.ARG_PROPIETARIO_ID) ?: 0L
            PropietarioScreen(
                token = token, propietarioId = propietarioId,
                onBack = { navController.popBackStack() },
                onNavigateToOfertas = { goToOfertas(token) },
                onNavigateToMiPerfil = { navController.navigate(Screen.MiPerfil.createRoute(token)) },
                onNavigateToMisReservas = { navController.navigate(Screen.MisReservas.createRoute(token)) },
                onNavigateToMisOfertas = { navController.navigate(Screen.MisOfertas.createRoute(token)) },
                onLogout = { logout() }
            )
        }

        // ── MI PERFIL ────────────────────────────────────────────────────
        composable(
            Screen.MiPerfil.route,
            arguments = listOf(navArgument(Screen.MiPerfil.ARG_TOKEN) { type = NavType.StringType })
        ) { bs ->
            val token = bs.arguments?.getString(Screen.MiPerfil.ARG_TOKEN) ?: ""
            MiPerfilScreen(
                token = token,
                onBack = { navController.popBackStack() },
                onNavigateToOfertas = { goToOfertas(token) },
                onNavigateToMiPerfil = { navController.navigate(Screen.MiPerfil.createRoute(token)) },
                onNavigateToMisReservas = { navController.navigate(Screen.MisReservas.createRoute(token)) },
                onNavigateToMisOfertas = { navController.navigate(Screen.MisOfertas.createRoute(token)) },
                onLogout = { logout() }
            )
        }

        // ── MIS RESERVAS ─────────────────────────────────────────────────
        composable(
            Screen.MisReservas.route,
            arguments = listOf(navArgument(Screen.MisReservas.ARG_TOKEN) { type = NavType.StringType })
        ) { bs ->
            val token = bs.arguments?.getString(Screen.MisReservas.ARG_TOKEN) ?: ""
            MisReservasScreen(
                token = token,
                onNavigateToOfertas = { goToOfertas(token) },
                onNavigateToMiPerfil = { navController.navigate(Screen.MiPerfil.createRoute(token)) },
                onNavigateToMisReservas = { navController.navigate(Screen.MisReservas.createRoute(token)) },
                onNavigateToMisOfertas = { navController.navigate(Screen.MisOfertas.createRoute(token)) },
                onLogout = { logout() },
                esPropietario = SessionManager.esPropietario
            )
        }

        // ── MIS OFERTAS ──────────────────────────────────────────────────
        composable(
            Screen.MisOfertas.route,
            arguments = listOf(navArgument(Screen.MisOfertas.ARG_TOKEN) { type = NavType.StringType })
        ) { bs ->
            val token = bs.arguments?.getString(Screen.MisOfertas.ARG_TOKEN) ?: ""
            MisOfertasScreen(
                token = token,
                onNavigateToOfertas = { goToOfertas(token) },
                onNavigateToMiPerfil = { navController.navigate(Screen.MiPerfil.createRoute(token)) },
                onNavigateToMisReservas = { navController.navigate(Screen.MisReservas.createRoute(token)) },
                onNavigateToMisOfertas = { navController.navigate(Screen.MisOfertas.createRoute(token)) },
                onLogout = { logout() }
            )
        }

        // ── PAGO STRIPE ──────────────────────────────────────────────────
        composable(
            Screen.StripePayment.route,
            arguments = listOf(
                navArgument(Screen.StripePayment.ARG_TOKEN) { type = NavType.StringType },
                navArgument(Screen.StripePayment.ARG_OFERTA_ID) { type = NavType.LongType },
                navArgument(Screen.StripePayment.ARG_USUARIO_ID) { type = NavType.LongType },
                navArgument(Screen.StripePayment.ARG_DIAS) { type = NavType.IntType },
                navArgument(Screen.StripePayment.ARG_PRECIO_CENTIMOS) { type = NavType.IntType },
                navArgument(Screen.StripePayment.ARG_FECHA_INICIO) { type = NavType.StringType },
                navArgument(Screen.StripePayment.ARG_FECHA_FIN) { type = NavType.StringType }
            )
        ) { bs ->
            val token = bs.arguments?.getString(Screen.StripePayment.ARG_TOKEN) ?: ""
            val ofertaId = bs.arguments?.getLong(Screen.StripePayment.ARG_OFERTA_ID) ?: 0L
            val usuarioId = bs.arguments?.getLong(Screen.StripePayment.ARG_USUARIO_ID) ?: 0L
            val dias = bs.arguments?.getInt(Screen.StripePayment.ARG_DIAS) ?: 1
            // Convertir céntimos → euros con precisión correcta
            val precioPorDia = (bs.arguments?.getInt(Screen.StripePayment.ARG_PRECIO_CENTIMOS) ?: 0) / 100.0
            val fechaInicio = URLDecoder.decode(bs.arguments?.getString(Screen.StripePayment.ARG_FECHA_INICIO) ?: "", "UTF-8")
            val fechaFin = URLDecoder.decode(bs.arguments?.getString(Screen.StripePayment.ARG_FECHA_FIN) ?: "", "UTF-8")

            StripePaymentScreen(
                token = token,
                ofertaId = ofertaId,
                usuarioId = usuarioId,
                diasContratados = dias,
                precioPorDia = precioPorDia,
                fechaInicio = fechaInicio,
                fechaFin = fechaFin,
                onPagoExitoso = { reservaId, totalPagado, fi, ff, d ->
                    navController.navigate(
                        Screen.ReservaSuccess.createRoute(token, reservaId, totalPagado, d, fi, ff)
                    ) {
                        popUpTo(Screen.Ofertas.route)
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        // ── RESERVA EXITOSA ──────────────────────────────────────────────
        composable(
            Screen.ReservaSuccess.route,
            arguments = listOf(
                navArgument(Screen.ReservaSuccess.ARG_TOKEN) { type = NavType.StringType },
                navArgument(Screen.ReservaSuccess.ARG_RESERVA_ID) { type = NavType.LongType },
                navArgument(Screen.ReservaSuccess.ARG_TOTAL_CENTIMOS) { type = NavType.IntType },
                navArgument(Screen.ReservaSuccess.ARG_DIAS) { type = NavType.IntType },
                navArgument(Screen.ReservaSuccess.ARG_FECHA_INICIO) { type = NavType.StringType },
                navArgument(Screen.ReservaSuccess.ARG_FECHA_FIN) { type = NavType.StringType }
            )
        ) { bs ->
            val token = bs.arguments?.getString(Screen.ReservaSuccess.ARG_TOKEN) ?: ""
            val reservaId = bs.arguments?.getLong(Screen.ReservaSuccess.ARG_RESERVA_ID) ?: 0L
            val totalPagado = (bs.arguments?.getInt(Screen.ReservaSuccess.ARG_TOTAL_CENTIMOS) ?: 0) / 100.0
            val dias = bs.arguments?.getInt(Screen.ReservaSuccess.ARG_DIAS) ?: 0
            val fechaInicio = URLDecoder.decode(bs.arguments?.getString(Screen.ReservaSuccess.ARG_FECHA_INICIO) ?: "", "UTF-8")
            val fechaFin = URLDecoder.decode(bs.arguments?.getString(Screen.ReservaSuccess.ARG_FECHA_FIN) ?: "", "UTF-8")

            ReservaSuccessScreen(
                reservaId = reservaId,
                totalPagado = totalPagado,
                fechaInicio = fechaInicio,
                fechaFin = fechaFin,
                dias = dias,
                onVolverAOfertas = {
                    navController.navigate(Screen.Ofertas.createRoute(token)) {
                        popUpTo(Screen.Ofertas.route) { inclusive = true }
                    }
                }
            )
        }
    }
}