package com.lokodom.alquilatucoche.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = Color(0xFF1A3A7A),
    onPrimaryContainer = Color(0xFFBDD4FF),

    secondary = Color(0xFF22C55E),
    onSecondary = Color(0xFF000000),
    secondaryContainer = Color(0xFF0D3320),
    onSecondaryContainer = Color(0xFF86EFAC),

    tertiary = Color(0xFFF59E0B),
    onTertiary = Color(0xFF000000),
    tertiaryContainer = Color(0xFF3D2900),
    onTertiaryContainer = Color(0xFFFDE68A),

    background = Background,
    onBackground = OnBackground,

    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,

    error = AccentRed,
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFF4A0D0D),
    onErrorContainer = Color(0xFFFCA5A5),

    outline = Border,
    outlineVariant = BorderSubtle,

    inverseSurface = OnBackground,
    inverseOnSurface = Background,
    inversePrimary = PrimaryVariant
)

@Composable
fun AlquilaTuCocheTheme(content: @Composable () -> Unit) {
    val systemUiController = rememberSystemUiController()

    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Background,
            darkIcons = false
        )
    }

    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = AppTypography,
        content = content
    )
}