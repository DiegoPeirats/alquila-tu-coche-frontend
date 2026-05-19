package com.lokodom.alquilatucoche.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lokodom.alquilatucoche.model.entidad.Propietario
import com.lokodom.alquilatucoche.model.entidad.Vehiculo
import com.lokodom.alquilatucoche.model.entidad.estados.TipoVehiculo
import com.lokodom.alquilatucoche.model.peticion.ofertas.CreacionOfertaRequest
import com.lokodom.alquilatucoche.model.peticion.vehiculos.CrearVehiculoRequest
import com.lokodom.alquilatucoche.ui.components.*
import com.lokodom.alquilatucoche.ui.theme.*
import com.lokodom.alquilatucoche.utils.UiState
import com.lokodom.alquilatucoche.utils.ValidationResult
import com.lokodom.alquilatucoche.utils.Validators
import com.lokodom.alquilatucoche.viewmodels.MiPerfilViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiPerfilScreen(
    token: String,
    onBack: () -> Unit,
    onNavigateToOfertas: () -> Unit,
    onNavigateToMiPerfil: () -> Unit,
    onNavigateToMisReservas: () -> Unit,
    onNavigateToMisOfertas: () -> Unit,
    onLogout: () -> Unit,
    vm: MiPerfilViewModel = viewModel()
) {
    val state by vm.state.collectAsState()
    val registroState by vm.registroState.collectAsState()
    val crearVehiculoState by vm.crearVehiculoState.collectAsState()
    val crearOfertaState by vm.crearOfertaState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var mostrarDialogoRegistro by remember { mutableStateOf(false) }
    var mostrarDialogoVehiculo by remember { mutableStateOf(false) }
    var mostrarDialogoOferta by remember { mutableStateOf(false) }
    var vehiculoAEliminar by remember { mutableStateOf<Vehiculo?>(null) }

    LaunchedEffect(token) { vm.loadMiPerfil(token) }

    LaunchedEffect(registroState) {
        when (registroState) {
            is UiState.Success -> { snackbarHostState.showSnackbar("¡Ahora eres propietario!"); vm.resetRegistroState() }
            is UiState.Error -> { snackbarHostState.showSnackbar((registroState as UiState.Error).message); vm.resetRegistroState() }
            else -> Unit
        }
    }
    LaunchedEffect(crearVehiculoState) {
        when (crearVehiculoState) {
            is UiState.Success -> { snackbarHostState.showSnackbar("Vehículo añadido correctamente"); vm.resetVehiculoState() }
            is UiState.Error -> { snackbarHostState.showSnackbar((crearVehiculoState as UiState.Error).message); vm.resetVehiculoState() }
            else -> Unit
        }
    }
    LaunchedEffect(crearOfertaState) {
        when (crearOfertaState) {
            is UiState.Success -> { snackbarHostState.showSnackbar("Oferta creada correctamente"); vm.resetOfertaState() }
            is UiState.Error -> { snackbarHostState.showSnackbar((crearOfertaState as UiState.Error).message); vm.resetOfertaState() }
            else -> Unit
        }
    }

    WithDrawer(
        onNavigateToOfertas = onNavigateToOfertas,
        onNavigateToMiPerfil = onNavigateToMiPerfil,
        onNavigateToMisReservas = onNavigateToMisReservas,
        onNavigateToMisOfertas = onNavigateToMisOfertas,
        onLogout = onLogout,
        esPropietario = (state as? UiState.Success<Propietario>)?.data?.vehiculos?.isNotEmpty() == true
    ) { drawerState, scope ->
        Scaffold(
            containerColor = Background,
            topBar = {
                TopAppBar(
                    title = { Text("Mi perfil", fontWeight = FontWeight.Bold, color = OnBackground) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, "Menú", tint = OnSurface)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
                )
            },
            snackbarHost = {
                SnackbarHost(snackbarHostState) { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = SurfaceElevated,
                        contentColor = OnSurface
                    )
                }
            }
        ) { padding ->
            when (val s = state) {
                is UiState.Loading -> LoadingScreen()
                is UiState.Error -> ErrorScreen(s.message) { vm.loadMiPerfil(token) }
                is UiState.Success<Propietario> -> {
                    val propietario = s.data
                    val esPropietario = propietario.vehiculos.isNotEmpty()
                    val vehiculos = propietario.vehiculos

                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(padding),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // — Avatar ——————————————————————————
                        item {
                            Column(
                                Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(shape = CircleShape, color = Primary.copy(0.15f), modifier = Modifier.size(96.dp)) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            propietario.nombre?.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                                            style = MaterialTheme.typography.displaySmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Primary
                                        )
                                    }
                                }
                                Text("${propietario.nombre ?: ""} ${propietario.apellidos ?: ""}".trim(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = OnBackground)
                                Text(propietario.email, style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
                                if (esPropietario) {
                                    Surface(color = Primary.copy(0.15f), shape = AppShapes.pill) {
                                        Text("⭐ Propietario", modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), style = MaterialTheme.typography.labelMedium, color = Primary, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }

                        // — Info personal ———————————————————
                        item {
                            AppCard(modifier = Modifier.fillMaxWidth()) {
                                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    SectionHeader("Información personal")
                                    SubtleDivider()
                                    propietario.direccion.let { InfoRow("Dirección", it) }
                                    propietario.provincia?.let { InfoRow("Provincia", it) }
                                    propietario.numeroTelefono?.let { InfoRow("Teléfono", it) }
                                    propietario.genero?.let { InfoRow("Género", it) }
                                }
                            }
                        }

                        // — Acciones según rol ———————————————
                        item {
                            if (esPropietario) {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    PrimaryButton("Crear oferta", onClick = { mostrarDialogoOferta = true }, icon = Icons.Filled.Add)
                                    SecondaryButton("Añadir vehículo", onClick = { mostrarDialogoVehiculo = true }, icon = Icons.Filled.DirectionsCar)
                                }
                            } else {
                                AppCard(modifier = Modifier.fillMaxWidth()) {
                                    Column(
                                        Modifier.padding(20.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("¿Tienes un vehículo?", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = OnBackground)
                                        Text("Regístrate como propietario y empieza a ganar.", style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center, color = OnSurfaceVariant)
                                        PrimaryButton(
                                            text = "Registrarse como propietario",
                                            onClick = { mostrarDialogoRegistro = true },
                                            loading = registroState is UiState.Loading,
                                            icon = Icons.Filled.DirectionsCar
                                        )
                                    }
                                }
                            }
                        }

                        // — Vehículos con acciones ——————————
                        if (esPropietario && vehiculos.isNotEmpty()) {
                            item { SectionHeader("Mis vehículos") }
                            items(vehiculos, key = { it.id }) { vehiculo ->
                                VehiculoCardConAcciones(
                                    vehiculo = vehiculo,
                                    onEditar = { /* TODO: navegación a editar */ },
                                    onEliminar = { vehiculoAEliminar = vehiculo }
                                )
                            }
                        }
                    }
                }
                else -> Unit
            }
        }
    }

    // Dialogo confirmar eliminación de vehículo
    vehiculoAEliminar?.let { vehiculo ->
        AlertDialog(
            onDismissRequest = { vehiculoAEliminar = null },
            containerColor = SurfaceVariant,
            title = { Text("Eliminar vehículo", color = OnBackground) },
            text = { Text("¿Seguro que quieres eliminar el ${vehiculo.tipo} de ${vehiculo.provincia}? Esta acción no se puede deshacer.", color = OnSurfaceVariant) },
            confirmButton = {
                Button(
                    onClick = {
                        // TODO: llamar vm.eliminarVehiculo(token, vehiculo.id)
                        vehiculoAEliminar = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
                ) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { vehiculoAEliminar = null }) {
                    Text("Cancelar", color = OnSurfaceVariant)
                }
            }
        )
    }

    if (mostrarDialogoRegistro) {
        RegistrarPropietarioDialog(
            loading = registroState is UiState.Loading,
            onConfirm = { bytes -> vm.registrarComoPropietario(token, bytes); mostrarDialogoRegistro = false },
            onDismiss = { mostrarDialogoRegistro = false }
        )
    }

    if (mostrarDialogoVehiculo) {
        CrearVehiculoDialogV2(
            loading = crearVehiculoState is UiState.Loading,
            onConfirm = { request -> vm.crearVehiculo(token, request); mostrarDialogoVehiculo = false },
            onDismiss = { mostrarDialogoVehiculo = false }
        )
    }

    val vehiculos = (state as? UiState.Success<Propietario>)?.data?.vehiculos ?: emptyList()
    if (mostrarDialogoOferta) {
        CrearOfertaDialog(
            vehiculos = vehiculos,
            loading = crearOfertaState is UiState.Loading,
            onConfirm = { request -> vm.crearOferta(token, request); mostrarDialogoOferta = false },
            onDismiss = { mostrarDialogoOferta = false }
        )
    }
}

// ─────────────────────────────────────────────
// VehiculoCardConAcciones
// Tarjeta moderna con botones editar y eliminar
// ─────────────────────────────────────────────
@Composable
fun VehiculoCardConAcciones(
    vehiculo: Vehiculo,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Header con tipo y estado
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(shape = AppShapes.medium, color = Primary.copy(0.12f), modifier = Modifier.size(40.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            val emoji = when (vehiculo.tipo.uppercase()) {
                                "MOTO" -> "🏍️"
                                "FURGONETA" -> "🚐"
                                "CAMION" -> "🚛"
                                else -> "🚗"
                            }
                            Text(emoji, style = MaterialTheme.typography.titleMedium)
                        }
                    }
                    Column {
                        Text(vehiculo.tipo, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = OnBackground)
                        Text(vehiculo.provincia, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                    }
                }
                vehiculo.ofertas.firstOrNull()?.let { EstadoChip(it.estado) }
            }

            SubtleDivider()

            InfoRow("Ofertas activas", "${vehiculo.ofertas.size}")
            vehiculo.ofertas.firstOrNull()?.precioPorDia?.let {
                InfoRow("Precio/día", "${String.format("%.2f", it)}€")
            }

            // Botones de acción
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onEditar,
                    modifier = Modifier.weight(1f).height(40.dp),
                    shape = AppShapes.medium,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Border),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = OnSurface)
                ) {
                    Icon(Icons.Filled.Edit, null, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Editar", style = MaterialTheme.typography.labelMedium)
                }
                Button(
                    onClick = onEliminar,
                    modifier = Modifier.weight(1f).height(40.dp),
                    shape = AppShapes.medium,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A0D0D))
                ) {
                    Icon(Icons.Filled.Delete, null, modifier = Modifier.size(14.dp), tint = AccentRed)
                    Spacer(Modifier.width(4.dp))
                    Text("Eliminar", style = MaterialTheme.typography.labelMedium, color = AccentRed)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
// CrearVehiculoDialogV2
// Con dropdown de provincias y validación de matrícula
// ─────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearVehiculoDialogV2(
    loading: Boolean,
    onConfirm: (CrearVehiculoRequest) -> Unit,
    onDismiss: () -> Unit
) {
    var tipo by remember { mutableStateOf<TipoVehiculo?>(null) }
    var provincia by remember { mutableStateOf("") }
    var matricula by remember { mutableStateOf("") }
    var tipoExpanded by remember { mutableStateOf(false) }

    val matriculaValidacion = remember(matricula) {
        if (matricula.isBlank()) null else Validators.validarMatricula(matricula)
    }
    val matriculaValida = matriculaValidacion is ValidationResult.Ok
    val valido = tipo != null && provincia.isNotBlank() && matriculaValida

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceVariant,
        shape = AppShapes.xxLarge,
        title = {
            Text("Añadir vehículo", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = OnBackground)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // Tipo de vehículo
                ExposedDropdownMenuBox(expanded = tipoExpanded, onExpandedChange = { tipoExpanded = it }) {
                    AppTextField(
                        value = tipo?.name ?: "",
                        onValueChange = {},
                        label = "Tipo de vehículo *",
                        readOnly = true,
                        modifier = Modifier.menuAnchor(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(tipoExpanded) }
                    )
                    ExposedDropdownMenu(
                        expanded = tipoExpanded,
                        onDismissRequest = { tipoExpanded = false },
                        containerColor = SurfaceElevated
                    ) {
                        TipoVehiculo.entries.forEach { t ->
                            DropdownMenuItem(
                                text = { Text(t.name, color = OnSurface) },
                                onClick = { tipo = t; tipoExpanded = false }
                            )
                        }
                    }
                }

                // Provincia con búsqueda
                ProvinciaDropdown(
                    value = provincia,
                    onValueChange = { provincia = it },
                    label = "Provincia *"
                )

                // Matrícula con validación
                MatriculaField(
                    value = matricula,
                    onValueChange = { matricula = it }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(CrearVehiculoRequest(tipo!!, provincia, matricula)) },
                enabled = valido && !loading,
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                shape = AppShapes.large
            ) {
                if (loading) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Color.White)
                } else {
                    Text("Añadir", color = Color.White)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = OnSurfaceVariant)
            }
        }
    )
}
@Composable
fun RegistrarPropietarioDialog(
    loading: Boolean,
    onConfirm: (ByteArray) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var imagenBytes by remember { mutableStateOf<ByteArray?>(null) }
    var imagenUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            imagenUri = it
            imagenBytes =
                context.contentResolver
                    .openInputStream(it)
                    ?.readBytes()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Registrarse como propietario")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    "Para registrarte como propietario necesitas subir una imagen de tu contrato o documento de propiedad.",
                    style = MaterialTheme.typography.bodyMedium
                )

                OutlinedButton(
                    onClick = {
                        launcher.launch("image/*")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {

                    Icon(Icons.Filled.Image, null)

                    Spacer(Modifier.width(8.dp))

                    Text(
                        if (imagenUri != null)
                            "Documento seleccionado ✓"
                        else
                            "Seleccionar documento"
                    )
                }
            }
        },
        confirmButton = {

            Button(
                onClick = {
                    imagenBytes?.let {
                        onConfirm(it)
                    }
                },
                enabled = imagenBytes != null && !loading
            ) {

                if (loading) {

                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )

                } else {

                    Text("Confirmar")
                }
            }
        },
        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancelar")
            }
        }
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearOfertaDialog(
    vehiculos: List<Vehiculo>,
    loading: Boolean,
    onConfirm: (CreacionOfertaRequest) -> Unit,
    onDismiss: () -> Unit
) {

    var vehiculoSeleccionado by remember {
        mutableStateOf<Vehiculo?>(null)
    }

    var precioText by remember {
        mutableStateOf("")
    }

    var vehiculoExpanded by remember {
        mutableStateOf(false)
    }

    val valido =
        vehiculoSeleccionado != null &&
                precioText.toDoubleOrNull() != null

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text("Crear oferta")
        },

        text = {

            Column(
                verticalArrangement =
                Arrangement.spacedBy(10.dp)
            ) {

                // Selector vehículo
                ExposedDropdownMenuBox(
                    expanded = vehiculoExpanded,
                    onExpandedChange = {
                        vehiculoExpanded = it
                    }
                ) {

                    OutlinedTextField(
                        value =
                        vehiculoSeleccionado?.let {
                            "${it.tipo} — ${it.provincia}"
                        }
                            ?: "Seleccionar vehículo *",

                        onValueChange = {},

                        readOnly = true,

                        label = {
                            Text("Vehículo")
                        },

                        trailingIcon = {
                            ExposedDropdownMenuDefaults
                                .TrailingIcon(
                                    vehiculoExpanded
                                )
                        },

                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),

                        shape = RoundedCornerShape(8.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = vehiculoExpanded,
                        onDismissRequest = {
                            vehiculoExpanded = false
                        }
                    ) {

                        if (vehiculos.isEmpty()) {

                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "Sin vehículos disponibles"
                                    )
                                },
                                onClick = {}
                            )
                        }

                        vehiculos.forEach { v ->

                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "${v.tipo} — ${v.provincia} (ID: ${v.id})"
                                    )
                                },

                                onClick = {
                                    vehiculoSeleccionado = v
                                    vehiculoExpanded = false
                                }
                            )
                        }
                    }
                }

                // Precio
                OutlinedTextField(
                    value = precioText,

                    onValueChange = {
                        precioText = it
                    },

                    label = {
                        Text("Precio por día (€) *")
                    },

                    modifier = Modifier.fillMaxWidth(),

                    singleLine = true,

                    shape = RoundedCornerShape(8.dp),

                    keyboardOptions = KeyboardOptions(
                        keyboardType =
                        KeyboardType.Decimal
                    ),

                    isError =
                    precioText.isNotBlank() &&
                            precioText.toDoubleOrNull() == null,

                    supportingText = {

                        if (
                            precioText.isNotBlank() &&
                            precioText.toDoubleOrNull() == null
                        ) {

                            Text(
                                "Introduce un precio válido",
                                color =
                                MaterialTheme
                                    .colorScheme
                                    .error
                            )
                        }
                    }
                )
            }
        },

        confirmButton = {

            Button(
                onClick = {

                    onConfirm(
                        CreacionOfertaRequest(
                            precioText.toDouble(),
                            vehiculoSeleccionado!!.id
                        )
                    )
                },

                enabled = valido && !loading
            ) {

                if (loading) {

                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )

                } else {

                    Text("Crear")
                }
            }
        },

        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancelar")
            }
        }
    )
}