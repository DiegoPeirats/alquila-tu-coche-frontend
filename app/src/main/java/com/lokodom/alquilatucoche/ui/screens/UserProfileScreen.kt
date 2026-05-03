package com.lokodom.alquilatucoche.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lokodom.alquilatucoche.ui.components.*
import com.lokodom.alquilatucoche.utils.UiState
import com.lokodom.alquilatucoche.viewmodels.UserProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    token: String,
    usuarioId: Long,
    onBack: () -> Unit,
    vm: UserProfileViewModel = viewModel()
) {
    val state by vm.state.collectAsState()

    LaunchedEffect(usuarioId) { vm.load(token, usuarioId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil de usuario", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { padding ->
        when (val s = state) {
            is UiState.Loading -> LoadingScreen()
            is UiState.Error -> ErrorScreen(s.message, onRetry = { vm.load(token, usuarioId) })
            is UiState.Success -> {
                val usuario = s.data
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // — Avatar + nombre ——————————————
                    item {
                        Column(
                            Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(96.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = usuario.nombre?.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                                        style = MaterialTheme.typography.displaySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                            Text(
                                text = usuario.nombre ?: "Usuario",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = usuario.email,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // — Información básica ———————————
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    "Información",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                                HorizontalDivider()
                                InfoRow("Dirección", usuario.direccion)
                                InfoRow("Reservas", "${usuario.reservas.size}")
                                InfoRow("Valoraciones", "${usuario.valoraciones.size}")
                            }
                        }
                    }

                    // — Reservas del usuario ————————
                    if (usuario.reservas.isNotEmpty()) {
                        item {
                            Text(
                                "Reservas",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        items(usuario.reservas) { reserva ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    InfoRow("Estado", reserva.estado)
                                    InfoRow("Inicio", reserva.fechaInicio)
                                    InfoRow("Fin", reserva.fechaFin)
                                }
                            }
                        }
                    }

                    // — Valoraciones recibidas ——————
                    if (usuario.valoraciones.isNotEmpty()) {
                        item {
                            Text(
                                "Valoraciones recibidas",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        items(usuario.valoraciones) { valoracion ->
                            ValoracionCard(valoracion)
                        }
                    }
                }
            }
            else -> Unit
        }
    }
}