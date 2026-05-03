package com.lokodom.alquilatucoche.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lokodom.alquilatucoche.model.entidad.Propietario
import com.lokodom.alquilatucoche.ui.components.*
import com.lokodom.alquilatucoche.utils.UiState
import com.lokodom.alquilatucoche.viewmodels.PropietarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropietarioScreen(
    token: String,
    propietarioId: Long,
    onBack: () -> Unit,
    vm: PropietarioViewModel = viewModel()
) {
    val state by vm.state.collectAsState()

    LaunchedEffect(propietarioId) { vm.load(token, propietarioId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil del propietario", fontWeight = FontWeight.Bold) },
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
            is UiState.Error -> ErrorScreen(s.message, onRetry = { vm.load(token, propietarioId) })
            is UiState.Success<Propietario> -> PropietarioContent(
                propietario = s.data,
                modifier = Modifier.padding(padding)
            )
            else -> Unit
        }
    }
}

@Composable
private fun PropietarioContent(
    propietario: Propietario,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // — Avatar + nombre ——————————————————————
        item {
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(96.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = propietario.nombre?.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                Text(
                    text = "${propietario.nombre ?: ""} ${propietario.apellidos ?: ""}".trim(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = propietario.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // — Información de contacto ——————————————
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Información", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    HorizontalDivider()
                    InfoRow("Dirección", propietario.direccion)
                    propietario.provincia?.let { InfoRow("Provincia", it) }
                    propietario.numeroTelefono?.let { InfoRow("Teléfono", it) }
                    propietario.genero?.let { InfoRow("Género", it) }
                    InfoRow("Vehículos", "${propietario.vehiculos.size}")
                }
            }
        }

        // — Vehículos ————————————————————————————
        if (propietario.vehiculos.isNotEmpty()) {
            item {
                Text("Vehículos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            items(propietario.vehiculos) { vehiculo ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = vehiculo.tipo,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            // Chip de estado de la primera oferta si existe
                            vehiculo.ofertas.firstOrNull()?.let { EstadoChip(it.estado) }
                        }
                        InfoRow("Provincia", vehiculo.provincia)
                        InfoRow("Ofertas", "${vehiculo.ofertas.size}")
                        if (vehiculo.ofertas.isNotEmpty()) {
                            val precio = vehiculo.ofertas.first().precioPorDia
                            if (precio != null) InfoRow("Precio/día", "${precio}€")
                        }
                    }
                }
            }
        }

        // — Valoraciones recibidas ———————————————
        if (propietario.valoracionesRecibidas.isNotEmpty()) {
            item {
                Text("Valoraciones recibidas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            items(propietario.valoracionesRecibidas) { valoracion ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row {
                            repeat(5) { idx ->
                                Icon(
                                    imageVector = if (idx < valoracion.valoracion) Icons.Filled.Star else Icons.Outlined.StarOutline,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Text(valoracion.mensaje, style = MaterialTheme.typography.bodyMedium)
                        valoracion.createdAt?.take(10)?.let {
                            Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}