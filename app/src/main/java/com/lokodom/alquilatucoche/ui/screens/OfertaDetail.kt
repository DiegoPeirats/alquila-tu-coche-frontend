package com.lokodom.alquilatucoche.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lokodom.alquilatucoche.model.entidad.Oferta
import com.lokodom.alquilatucoche.ui.components.*
import com.lokodom.alquilatucoche.utils.UiState
import com.lokodom.alquilatucoche.viewmodels.OfertaDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfertaDetailScreen(
    token: String,
    ofertaId: Long,
    onVerVehiculo: (Long) -> Unit,
    onReservar: (usuarioId: Long, dias: Int) -> Unit,
    onBack: () -> Unit,
    vm: OfertaDetailViewModel = viewModel()
) {
    val state by vm.state.collectAsState()
    val dias by vm.diasSeleccionados.collectAsState()

    LaunchedEffect(ofertaId) { vm.load(token, ofertaId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de oferta", fontWeight = FontWeight.Bold) },
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
            is UiState.Error -> ErrorScreen(s.message, onRetry = { vm.load(token, ofertaId) })
            is UiState.Success<Oferta> -> OfertaDetailContent(
                oferta = s.data,
                dias = dias,
                onDiasChange = { vm.setDias(it) },
                onVerVehiculo = { onVerVehiculo(s.data.idVehiculo) },
                onReservar = {
                    // TODO: reemplazar 1L con el usuarioId real del usuario autenticado
                    onReservar(1L, dias)
                },
                modifier = Modifier.padding(padding)
            )
            else -> Unit
        }
    }
}

@Composable
private fun OfertaDetailContent(
    oferta: Oferta,
    dias: Int,
    onDiasChange: (Int) -> Unit,
    onVerVehiculo: () -> Unit,
    onReservar: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // — Info principal ——————————————————
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Oferta #${oferta.id}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        EstadoChip(oferta.estado)
                    }
                    HorizontalDivider()
                    InfoRow("ID Vehículo", "${oferta.idVehiculo}")
                    InfoRow("Reservas activas", "${oferta.reservas.size}")
                    InfoRow("Valoraciones", "${oferta.valoraciones.size}")
                }
            }
        }

        // — Selector de días y precio ———————
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "¿Cuántos días quieres alquilarlo?",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    DiasSelectorRow(dias = dias, onDiasChange = onDiasChange)
                }
            }
        }

        // — Acciones ————————————————————————
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PrimaryButton(
                    text = "🚗  Ver vehículo",
                    onClick = onVerVehiculo
                )
                PrimaryButton(
                    text = "💳  Reservar ($dias día${if (dias != 1) "s" else ""})",
                    onClick = onReservar,
                    enabled = oferta.estado.uppercase() == "ACTIVA"
                )
            }
        }

        // — Valoraciones ————————————————————
        if (oferta.valoraciones.isNotEmpty()) {
            item {
                Text(
                    "Valoraciones",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            items(oferta.valoraciones) { valoracion ->
                ValoracionCard(valoracion)
            }
        }
    }
}