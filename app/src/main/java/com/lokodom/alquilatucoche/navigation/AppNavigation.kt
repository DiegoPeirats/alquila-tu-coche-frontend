package com.lokodom.alquilatucoche.navigation

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lokodom.alquilatucoche.ui.screens.LoginScreen
import com.lokodom.alquilatucoche.ui.screens.OfertasScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Ofertas : Screen("ofertas/{token}") {
        fun createRoute(token: String) = "ofertas/$token"
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

        composable(Screen.Ofertas.route) { backStackEntry ->
            val token = backStackEntry.arguments?.getString("token") ?: ""
            OfertasScreen(token = token)
        }
    }
}