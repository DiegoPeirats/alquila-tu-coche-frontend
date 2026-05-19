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
import com.lokodom.alquilatucoche.model.entidad.Propietario
import com.lokodom.alquilatucoche.model.entidad.Reserva
import com.lokodom.alquilatucoche.session.SessionManager
import com.lokodom.alquilatucoche.ui.components.*
import com.lokodom.alquilatucoche.utils.UiState
import com.lokodom.alquilatucoche.viewmodels.MisReservasViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisReservasScreen(
    token: String,
    onNavigateToOfertas: () -> Unit,
    onNavigateToMiPerfil: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToMisReservas: () -> Unit,
    onNavigateToMisOfertas: () -> Unit,
    esPropietario: Boolean,
    vm: MisReservasViewModel = viewModel()
) {
    val state by vm.state.collectAsState()
    val accionState by vm.accionState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val usuarioId = SessionManager.usuarioId

    LaunchedEffect(token) { vm.load(token, usuarioId) }

    // Feedback de acciones
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
        esPropietario = esPropietario
    ) { drawerState, scope ->
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Mis reservas", fontWeight = FontWeight.Bold) },
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
                is UiState.Error -> ErrorScreen(s.message) { vm.load(token, usuarioId) }
                is UiState.Success<List<Reserva>> -> {
                    val reservas = s.data
                    if (reservas.isEmpty()) {
                        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                            Text("No tienes reservas activas", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(padding),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(reservas, key = { it.id }) { reserva ->
                                ReservaCard(
                                    reserva = reserva,
                                    accionLoading = accionState is UiState.Loading,
                                    onModificar = { inicio, fin ->
                                        vm.modificarReserva(token, usuarioId, reserva.id, inicio, fin)
                                    },
                                    onCancelar = {
                                        vm.cancelarReserva(token, usuarioId, reserva.id)
                                    }
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
private fun ReservaCard(
    reserva: Reserva,
    accionLoading: Boolean,
    onModificar: (String, String) -> Unit,
    onCancelar: () -> Unit
) {
    var mostrarDialogoModificar by remember { mutableStateOf(false) }
    var mostrarDialogoCancelar by remember { mutableStateOf(false) }

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
                Text("Reserva #${reserva.id}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                EstadoChip(reserva.estado)
            }
            HorizontalDivider()
            InfoRow("Inicio", reserva.fechaInicio)
            InfoRow("Fin", reserva.fechaFin)

            // Solo mostrar acciones en reservas modificables
            val esModificable = reserva.estado !in listOf("PENDIENTE", "ACABADA", "EJECUTANDO")
            if (esModificable) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
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
                        onClick = { mostrarDialogoCancelar = true },
                        modifier = Modifier.weight(1f),
                        enabled = !accionLoading,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(Icons.Filled.Close, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Cancelar")
                    }
                }
            }
        }
    }

    // Diálogo modificar
    if (mostrarDialogoModificar) {
        ModificarReservaDialog(
            reservaActual = reserva,
            onConfirm = { inicio, fin ->
                onModificar(inicio, fin)
                mostrarDialogoModificar = false
            },
            onDismiss = { mostrarDialogoModificar = false }
        )
    }

    // Diálogo confirmar cancelación
    if (mostrarDialogoCancelar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoCancelar = false },
            title = { Text("Cancelar reserva") },
            text = { Text("¿Estás seguro de que quieres cancelar la reserva #${reserva.id}? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = { onCancelar(); mostrarDialogoCancelar = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Cancelar reserva") }
            },
            dismissButton = { TextButton(onClick = { mostrarDialogoCancelar = false }) { Text("Mantener") } }
        )
    }
}

@Composable
private fun ModificarReservaDialog(
    reservaActual: Reserva,
    onConfirm: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var fechaInicio by remember { mutableStateOf(reservaActual.fechaInicio) }
    var fechaFin by remember { mutableStateOf(reservaActual.fechaFin) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Modificar reserva #${reservaActual.id}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Introduce las nuevas fechas (formato: YYYY-MM-DD)", style = MaterialTheme.typography.bodySmall)
                OutlinedTextField(
                    value = fechaInicio,
                    onValueChange = { fechaInicio = it },
                    label = { Text("Fecha de inicio") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )
                OutlinedTextField(
                    value = fechaFin,
                    onValueChange = { fechaFin = it },
                    label = { Text("Fecha de fin") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(fechaInicio, fechaFin) },
                enabled = fechaInicio.isNotBlank() && fechaFin.isNotBlank()
            ) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
