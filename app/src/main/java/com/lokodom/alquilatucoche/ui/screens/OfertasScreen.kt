package com.lokodom.alquilatucoche.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lokodom.alquilatucoche.model.entidad.Oferta
import com.lokodom.alquilatucoche.ui.components.ErrorScreen
import com.lokodom.alquilatucoche.ui.components.EstadoChip
import com.lokodom.alquilatucoche.ui.components.LoadingScreen
import com.lokodom.alquilatucoche.utils.UiState
import com.lokodom.alquilatucoche.viewmodels.OfertasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfertasScreen(
    token: String,
    onOfertaClick: (Long) -> Unit,
    vm: OfertasViewModel = viewModel()
) {
    val state by vm.state.collectAsState()

    LaunchedEffect(token) { vm.load(token) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ofertas disponibles", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        when (val s = state) {
            is UiState.Loading -> LoadingScreen()
            is UiState.Error -> ErrorScreen(s.message, onRetry = { vm.load(token) })
            is UiState.Success -> {
                val ofertas = s.data
                if (ofertas.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay ofertas disponibles", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(ofertas, key = { it.id }) { oferta ->
                            OfertaCard(oferta = oferta, onClick = { onOfertaClick(oferta.id) })
                        }
                    }
                }
            }
            else -> Unit
        }
    }
}

@Composable
fun OfertaCard(oferta: Oferta, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Oferta #${oferta.id}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                EstadoChip(estado = oferta.estado)
            }

            Text(
                text = "Vehículo ID: ${oferta.idVehiculo}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (oferta.valoraciones.isNotEmpty()) {
                val media = oferta.valoraciones.map { it.valoracion }.average()
                Text(
                    text = "⭐ ${String.format("%.1f", media)} (${oferta.valoraciones.size} valoraciones)",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (oferta.reservas.isNotEmpty()) {
                Text(
                    text = "${oferta.reservas.size} reserva${if (oferta.reservas.size != 1) "s" else ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
