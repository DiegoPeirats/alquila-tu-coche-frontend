package com.lokodom.alquilatucoche

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.lokodom.alquilatucoche.ui.theme.AlquilaTuCocheTheme
import com.lokodom.alquilatucoche.navigation.AppNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AlquilaTuCocheTheme {
                AppNavigation()
            }
        }
    }
}