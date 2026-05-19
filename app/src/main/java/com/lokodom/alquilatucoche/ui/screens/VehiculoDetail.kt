package com.lokodom.alquilatucoche.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lokodom.alquilatucoche.ui.components.*
import com.lokodom.alquilatucoche.utils.UiState
import com.lokodom.alquilatucoche.viewmodels.VehiculoViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehiculoDetailScreen(
    token: String,
    vehiculoId: Long,
    onVerPerfil: (Long) -> Unit,
    onBack: () -> Unit,
    onNavigateToOfertas: () -> Unit,
    onNavigateToMiPerfil: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToMisReservas: () -> Unit,
    onNavigateToMisOfertas: () -> Unit,
    vm: VehiculoViewModel = viewModel()
) {
    val state by vm.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(vehiculoId) { vm.load(token, vehiculoId) }

    WithDrawer(
        onNavigateToOfertas = onNavigateToOfertas,
        onNavigateToMiPerfil = onNavigateToMiPerfil,
        onLogout = onLogout,
        onNavigateToMisReservas = onNavigateToMisReservas,
        onNavigateToMisOfertas = onNavigateToMisOfertas
    ) { drawerState, scope ->

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Detalle del vehículo", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menú")
                        }
                    },
                    actions = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                        }
                    }
                )
            }
        ) { padding ->

            when (val s = state) {
                is UiState.Loading -> LoadingScreen()

                is UiState.Error -> ErrorScreen(
                    s.message,
                    onRetry = { vm.load(token, vehiculoId) }
                )

                is UiState.Success -> {
                    val vehiculo = s.data

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        item {
                            Box(
                                Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier.size(100.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            "🚗",
                                            style = MaterialTheme.typography.displayMedium
                                        )
                                    }
                                }
                            }
                        }

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
                                        "Información del vehículo",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    HorizontalDivider()
                                    InfoRow("Tipo", vehiculo.tipo)
                                    InfoRow("Provincia", vehiculo.provincia)
                                    InfoRow("ID", "${vehiculo.id}")
                                    InfoRow("Propietario ID", "${vehiculo.propietarioId}")
                                    InfoRow("Ofertas activas", "${vehiculo.ofertas.size}")
                                }
                            }
                        }

                        item {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {

                                Button(
                                    onClick = { onVerPerfil(vehiculo.propietarioId) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Filled.Person, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Ver perfil del propietario")
                                }

                                OutlinedButton(
                                    onClick = {
                                        val intent = Intent(
                                            Intent.ACTION_DIAL,
                                            Uri.parse("tel:+34600000000")
                                        )
                                        context.startActivity(intent)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Filled.Call, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Contactar con el propietario")
                                }
                            }
                        }
                    }
                }

                else -> Unit
            }
        }
    }
}