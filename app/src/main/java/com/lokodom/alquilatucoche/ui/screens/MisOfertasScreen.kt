package com.lokodom.alquilatucoche.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lokodom.alquilatucoche.model.entidad.Oferta
import com.lokodom.alquilatucoche.ui.components.*
import com.lokodom.alquilatucoche.utils.UiState
import com.lokodom.alquilatucoche.viewmodels.MisOfertasViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisOfertasScreen(
    token: String,
    onNavigateToOfertas: () -> Unit,
    onNavigateToMiPerfil: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToMisReservas: () -> Unit,
    onNavigateToMisOfertas: () -> Unit,
    vm: MisOfertasViewModel = viewModel()
) {
    val state by vm.state.collectAsState()
    val accionState by vm.accionState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(token) { vm.load(token) }

    LaunchedEffect(accionState) {
        when (accionState) {
            is UiState.Success -> { snackbarHostState.showSnackbar("Operación realizada"); vm.resetAccionState() }
            is UiState.Error -> { snackbarHostState.showSnackbar((accionState as UiState.Error).message); vm.resetAccionState() }
            else -> Unit
        }
    }

    WithDrawer(
        onNavigateToOfertas = onNavigateToOfertas,
        onNavigateToMiPerfil = onNavigateToMiPerfil,
        onLogout = onLogout,
        esPropietario = true
    ) { drawerState, scope ->
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Mis ofertas", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, "Menú")
                        }
                    }
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { padding ->
            when (val s = state) {
                is UiState.Loading -> LoadingScreen()
                is UiState.Error -> ErrorScreen(s.message) { vm.load(token) }
                is UiState.Success<List<Oferta>> -> {
                    val ofertas = s.data
                    if (ofertas.isEmpty()) {
                        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                            Text("No tienes ofertas creadas", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(padding),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(ofertas, key = { it.id }) { oferta ->
                                MiOfertaCard(
                                    oferta = oferta,
                                    accionLoading = accionState is UiState.Loading,
                                    onModificar = { precio, estado ->
                                        vm.modificarOferta(token, oferta.id, precio, estado)
                                    },
                                    onEliminar = { vm.eliminarOferta(token, oferta.id) }
                                )
                            }
                        }
                    }
                }
                else -> Unit
            }
        }
    }
}

@Composable
private fun MiOfertaCard(
    oferta: Oferta,
    accionLoading: Boolean,
    onModificar: (Double, String) -> Unit,
    onEliminar: () -> Unit
) {
    var mostrarDialogoModificar by remember { mutableStateOf(false) }
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Oferta #${oferta.id}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                EstadoChip(oferta.estado)
            }
            HorizontalDivider()
            InfoRow("Vehículo ID", "${oferta.idVehiculo}")
            oferta.precioPorDia?.let { InfoRow("Precio/día", "${it}€") }
            InfoRow("Reservas", "${oferta.reservas.size}")

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { mostrarDialogoModificar = true },
                    modifier = Modifier.weight(1f),
                    enabled = !accionLoading,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Filled.Edit, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Modificar")
                }
                Button(
                    onClick = { mostrarDialogoEliminar = true },
                    modifier = Modifier.weight(1f),
                    enabled = !accionLoading,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Filled.Delete, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Eliminar")
                }
            }
        }
    }

    if (mostrarDialogoModificar) {
        ModificarOfertaDialog(
            ofertaActual = oferta,
            onConfirm = { precio, estado ->
                onModificar(precio, estado)
                mostrarDialogoModificar = false
            },
            onDismiss = { mostrarDialogoModificar = false }
        )
    }

    if (mostrarDialogoEliminar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoEliminar = false },
            title = { Text("Eliminar oferta") },
            text = { Text("¿Estás seguro de que quieres eliminar la oferta #${oferta.id}? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = { onEliminar(); mostrarDialogoEliminar = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Eliminar") }
            },
            dismissButton = { TextButton(onClick = { mostrarDialogoEliminar = false }) { Text("Cancelar") } }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModificarOfertaDialog(
    ofertaActual: Oferta,
    onConfirm: (Double, String) -> Unit,
    onDismiss: () -> Unit
) {
    var precioText by remember { mutableStateOf(ofertaActual.precioPorDia?.toString() ?: "") }
    var estadoExpanded by remember { mutableStateOf(false) }
    val estadosDisponibles = listOf("ACTIVA", "INACTIVA", "PENDIENTE")
    var estadoSeleccionado by remember { mutableStateOf(ofertaActual.estado) }

    val valido = precioText.toDoubleOrNull() != null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Modificar oferta #${ofertaActual.id}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = precioText,
                    onValueChange = { precioText = it },
                    label = { Text("Precio por día (€)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = precioText.isNotBlank() && precioText.toDoubleOrNull() == null
                )
                ExposedDropdownMenuBox(expanded = estadoExpanded, onExpandedChange = { estadoExpanded = it }) {
                    OutlinedTextField(
                        value = estadoSeleccionado,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Estado") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(estadoExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    ExposedDropdownMenu(expanded = estadoExpanded, onDismissRequest = { estadoExpanded = false }) {
                        estadosDisponibles.forEach { estado ->
                            DropdownMenuItem(
                                text = { Text(estado) },
                                onClick = { estadoSeleccionado = estado; estadoExpanded = false }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(precioText.toDouble(), estadoSeleccionado) },
                enabled = valido
            ) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
