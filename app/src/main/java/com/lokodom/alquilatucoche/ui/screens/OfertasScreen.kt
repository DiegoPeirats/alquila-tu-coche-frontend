package com.lokodom.alquilatucoche.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lokodom.alquilatucoche.model.entidad.Oferta
import com.lokodom.alquilatucoche.model.entidad.estados.TipoVehiculo
import com.lokodom.alquilatucoche.model.peticion.ofertas.OfertasFiltro
import com.lokodom.alquilatucoche.session.SessionManager
import com.lokodom.alquilatucoche.ui.components.*
import com.lokodom.alquilatucoche.ui.theme.*
import com.lokodom.alquilatucoche.utils.UiState
import com.lokodom.alquilatucoche.viewmodels.OfertasViewModel
import kotlinx.coroutines.launch

// ── Título dinámico de oferta ──────────────────────────────────
fun tituloOferta(tipo: String?, provincia: String?, precioPorDia: Double?): String {
    val t = tipo?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "Vehículo"
    val p = provincia?.replaceFirstChar { it.uppercase() } ?: "España"
    val precio = precioPorDia?.let { "${String.format("%.0f", it)}€/día" } ?: ""
    return if (precio.isNotBlank()) "$t en $p por $precio" else "$t en $p"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfertasScreen(
    token: String,
    onOfertaClick: (Long) -> Unit,
    onNavigateToOfertas: () -> Unit,
    onNavigateToMiPerfil: () -> Unit,
    onNavigateToMisReservas: () -> Unit,
    onNavigateToMisOfertas: () -> Unit,
    onLogout: () -> Unit,
    vm: OfertasViewModel = viewModel()
) {
    val state by vm.state.collectAsState()
    val filtro by vm.filtro.collectAsState()
    val filtrosVisibles by vm.filtrosVisibles.collectAsState()

    LaunchedEffect(token) { vm.loadDisponibles(token) }

    WithDrawer(
        onNavigateToOfertas = onNavigateToOfertas,
        onNavigateToMiPerfil = onNavigateToMiPerfil,
        onNavigateToMisReservas = onNavigateToMisReservas,
        onNavigateToMisOfertas = onNavigateToMisOfertas,
        onLogout = onLogout,
        esPropietario = SessionManager.esPropietario
    ) { drawerState, scope ->
        Scaffold(
            containerColor = Background,
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text("Explorar", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = OnBackground)
                            Text("Vehículos disponibles", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, "Menú", tint = OnSurface)
                        }
                    },
                    actions = {
                        val hayFiltros = filtro != OfertasFiltro()
                        IconButton(onClick = { vm.toggleFiltros() }) {
                            Icon(
                                Icons.Filled.Tune,
                                "Filtros",
                                tint = if (hayFiltros) Primary else OnSurfaceVariant
                            )
                        }
                        if (hayFiltros) {
                            IconButton(onClick = { vm.limpiarFiltros(token) }) {
                                Icon(Icons.Filled.FilterListOff, "Limpiar", tint = AccentAmber)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
                )
            }
        ) { padding ->
            Column(Modifier.fillMaxSize().padding(padding)) {

                // Panel filtros animado
                AnimatedVisibility(filtrosVisibles, enter = expandVertically() + fadeIn(), exit = shrinkVertically() + fadeOut()) {
                    FiltroPanel(
                        filtro = filtro,
                        onFiltroChange = { vm.updateFiltro(it) },
                        onBuscar = { vm.buscarConFiltro(token) },
                        onCerrar = { vm.toggleFiltros() }
                    )
                }

                when (val s = state) {
                    is UiState.Loading -> LoadingScreen()
                    is UiState.Error -> ErrorScreen(s.message) { vm.loadDisponibles(token) }
                    is UiState.Success<List<Oferta>> -> {
                        if (s.data.isEmpty()) {
                            EmptyState(
                                icon = Icons.Filled.SearchOff,
                                title = "Sin resultados",
                                subtitle = "Prueba con otros filtros o vuelve más tarde",
                                action = "Ver todas" to { vm.limpiarFiltros(token) }
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(s.data, key = { it.id }) { oferta ->
                                    OfertaCard(oferta = oferta, onClick = { onOfertaClick(oferta.id) })
                                }
                                item { Spacer(Modifier.height(16.dp)) }
                            }
                        }
                    }
                    else -> Unit
                }
            }
        }
    }
}

// ── OfertaCard moderna ─────────────────────────────────────────
@Composable
fun OfertaCard(oferta: Oferta, onClick: () -> Unit) {
    // Para el título dinámico necesitamos los datos del vehículo.
    // Mientras no tengamos tipo/provincia en Oferta, mostramos lo disponible.
    // Si tu backend devuelve vehiculo embebido en oferta, añade esos campos al modelo.
    val titulo = tituloOferta(
        tipo = null,       // reemplazar por oferta.vehiculo?.tipo cuando esté disponible
        provincia = null,  // reemplazar por oferta.vehiculo?.provincia
        precioPorDia = oferta.precioPorDia
    )

    AppCard(modifier = Modifier.fillMaxWidth(), onClick = onClick) {
        Column {
            // Header con placeholder de imagen
            VehiculoImagePlaceholder(
                tipo = "COCHE",
                modifier = Modifier.fillMaxWidth().height(160.dp)
            )

            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Oferta #${oferta.id}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = OnBackground,
                        modifier = Modifier.weight(1f)
                    )
                    EstadoChip(oferta.estado)
                }

                oferta.precioPorDia?.let {
                    PrecioDisplay(precioPorDia = it, dias = 1)
                }

                SubtleDivider()

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    if (oferta.valoraciones.isNotEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Filled.Star, null, tint = AccentAmber, modifier = Modifier.size(14.dp))
                            val media = oferta.valoraciones.map { it.valoracion }.average()
                            Text("${String.format("%.1f", media)}", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                        }
                    }
                    if (oferta.reservas.isNotEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Filled.BookOnline, null, tint = OnSurfaceMuted, modifier = Modifier.size(14.dp))
                            Text("${oferta.reservas.size} reservas", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

// ── Panel de filtros ───────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FiltroPanel(
    filtro: OfertasFiltro,
    onFiltroChange: (OfertasFiltro) -> Unit,
    onBuscar: () -> Unit,
    onCerrar: () -> Unit
) {
    var precioMinText by remember(filtro.precioMinimo) { mutableStateOf(filtro.precioMinimo?.toString() ?: "") }
    var precioMaxText by remember(filtro.precioMaximo) { mutableStateOf(filtro.precioMaximo?.toString() ?: "") }
    var tipoExpanded by remember { mutableStateOf(false) }

    AppCard(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Filtros", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                IconButton(onClick = onCerrar, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Filled.Close, "Cerrar", tint = OnSurfaceVariant)
                }
            }
            SubtleDivider()

            ExposedDropdownMenuBox(expanded = tipoExpanded, onExpandedChange = { tipoExpanded = it }) {
                AppTextField(
                    value = filtro.tipoVehiculo?.name ?: "Todos los tipos",
                    onValueChange = {},
                    label = "Tipo de vehículo",
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(tipoExpanded) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(expanded = tipoExpanded, onDismissRequest = { tipoExpanded = false },
                    containerColor = SurfaceElevated) {
                    DropdownMenuItem(text = { Text("Todos") }, onClick = { onFiltroChange(filtro.copy(tipoVehiculo = null)); tipoExpanded = false })
                    TipoVehiculo.entries.forEach { t ->
                        DropdownMenuItem(text = { Text(t.name) }, onClick = { onFiltroChange(filtro.copy(tipoVehiculo = t)); tipoExpanded = false })
                    }
                }
            }

            AppTextField(
                value = filtro.provincia ?: "",
                onValueChange = { onFiltroChange(filtro.copy(provincia = it.ifBlank { null })) },
                label = "Provincia"
            )

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AppTextField(
                    value = precioMinText,
                    onValueChange = { precioMinText = it; onFiltroChange(filtro.copy(precioMinimo = it.toDoubleOrNull())) },
                    label = "Precio mín.",
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                AppTextField(
                    value = precioMaxText,
                    onValueChange = { precioMaxText = it; onFiltroChange(filtro.copy(precioMaximo = it.toDoubleOrNull())) },
                    label = "Precio máx.",
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
            }

            PrimaryButton("Buscar", onBuscar, icon = Icons.Filled.Search)
        }
    }
}