package com.lokodom.alquilatucoche.ui.components

// ─────────────────────────────────────────────
// WithDrawer.kt
// Helper composable para envolver pantallas con drawer.
// Simplifica la repetición en cada pantalla.
// ─────────────────────────────────────────────


/**
 * Envuelve el contenido de una pantalla con el drawer lateral.
 *
 * Uso:
 * WithDrawer(
 *     onNavigateToOfertas = { ... },
 *     onNavigateToMiPerfil = { ... }
 * ) { drawerState, scope ->
 *     Scaffold(
 *         topBar = {
 *             TopAppBar(
 *                 navigationIcon = {
 *                     IconButton(onClick = { scope.launch { drawerState.open() } }) {
 *                         Icon(Icons.Filled.Menu, "Menú")
 *                     }
 *                 }
 *             )
 *         }
 *     ) { ... }
 * }
 */
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lokodom.alquilatucoche.ui.theme.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun WithDrawer(
    onNavigateToOfertas: () -> Unit,
    onNavigateToMiPerfil: () -> Unit,
    onLogout: () -> Unit,
    esPropietario: Boolean = false,
    onNavigateToMisReservas: (() -> Unit)? = null,
    onNavigateToMisOfertas: (() -> Unit)? = null,
    content: @Composable (drawerState: DrawerState, scope: CoroutineScope) -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    fun closeAndNavigate(action: () -> Unit) {
        scope.launch { drawerState.close() }
        action()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Surface,
                drawerContentColor = OnSurface
            ) {
                Spacer(Modifier.height(40.dp))

                // Logo / Brand
                Column(Modifier.padding(horizontal = 24.dp)) {
                    Text(
                        "AlquilaTuCoche",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = OnBackground
                    )
                    Text(
                        "Tu marketplace de alquiler",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant
                    )
                }

                Spacer(Modifier.height(24.dp))
                HorizontalDivider(color = Border, thickness = 0.5.dp)
                Spacer(Modifier.height(8.dp))

                DrawerNavItem(Icons.Filled.Home, "Ofertas") { closeAndNavigate(onNavigateToOfertas) }
                DrawerNavItem(Icons.Filled.Person, "Mi perfil") { closeAndNavigate(onNavigateToMiPerfil) }
                onNavigateToMisReservas?.let { nav ->
                    DrawerNavItem(Icons.Filled.BookOnline, "Mis reservas") { closeAndNavigate(nav) }
                }
                if (esPropietario) {
                    onNavigateToMisOfertas?.let { nav ->
                        DrawerNavItem(Icons.Filled.ListAlt, "Mis ofertas") { closeAndNavigate(nav) }
                    }
                }

                Spacer(Modifier.weight(1f))
                HorizontalDivider(color = Border, thickness = 0.5.dp)
                Spacer(Modifier.height(8.dp))

                DrawerNavItem(
                    icon = Icons.Filled.Logout,
                    label = "Cerrar sesión",
                    tint = AccentRed
                ) { closeAndNavigate(onLogout) }

                Spacer(Modifier.height(24.dp))
            }
        }
    ) {
        content(drawerState, scope)
    }
}

@Composable
private fun DrawerNavItem(
    icon: ImageVector,
    label: String,
    tint: androidx.compose.ui.graphics.Color = Primary,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(icon, label, tint = tint, modifier = Modifier.size(20.dp))
        Text(label, style = MaterialTheme.typography.bodyLarge, color = OnSurface)
    }
}
