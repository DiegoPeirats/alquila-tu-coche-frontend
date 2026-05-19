package com.lokodom.alquilatucoche.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lokodom.alquilatucoche.model.entidad.Oferta
import com.lokodom.alquilatucoche.session.SessionManager
import com.lokodom.alquilatucoche.ui.components.*
import com.lokodom.alquilatucoche.ui.theme.*
import com.lokodom.alquilatucoche.utils.UiState
import com.lokodom.alquilatucoche.utils.ValidationResult
import com.lokodom.alquilatucoche.utils.Validators
import com.lokodom.alquilatucoche.viewmodels.OfertaDetailViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfertaDetailScreen(
    token: String,
    ofertaId: Long,
    onVerVehiculo: (Long) -> Unit,
    onReservar: (usuarioId: Long, fechaInicio: String, fechaFin: String, dias: Int, precioPorDia: Double) -> Unit,
    onBack: () -> Unit,
    onNavigateToOfertas: () -> Unit,
    onNavigateToMiPerfil: () -> Unit,
    onNavigateToMisReservas: () -> Unit,
    onNavigateToMisOfertas: () -> Unit,
    onLogout: () -> Unit,
    vm: OfertaDetailViewModel = viewModel()
) {
    val state            by vm.state.collectAsState()
    val fechaInicio      by vm.fechaInicio.collectAsState()
    val fechaFin         by vm.fechaFin.collectAsState()
    val diasSeleccionados by vm.diasSeleccionados.collectAsState()
    val fechasReservadas by vm.fechasReservadas.collectAsState()

    LaunchedEffect(ofertaId) { vm.load(token, ofertaId) }

    WithDrawer(
        onNavigateToOfertas    = onNavigateToOfertas,
        onNavigateToMiPerfil   = onNavigateToMiPerfil,
        onNavigateToMisReservas = onNavigateToMisReservas,
        onNavigateToMisOfertas  = onNavigateToMisOfertas,
        onLogout               = onLogout,
        esPropietario          = SessionManager.esPropietario
    ) { drawerState, scope ->
        Box(Modifier.fillMaxSize().background(Background)) {
            when (val s = state) {
                is UiState.Loading -> LoadingScreen()
                is UiState.Error   -> ErrorScreen(s.message) { vm.load(token, ofertaId) }
                is UiState.Success<Oferta> -> {
                    val oferta = s.data

                    val propietarioVehiculoId: Long? = null
                    val esOfertaPropia = propietarioVehiculoId != null &&
                            propietarioVehiculoId == SessionManager.usuarioId

                    val validacionFechas = remember(fechaInicio, fechaFin) {
                        if (fechaInicio.isNotBlank() && fechaFin.isNotBlank())
                            Validators.validarFechas(fechaInicio, fechaFin)
                        else null
                    }

                    // El rango también falla si contiene fechas reservadas
                    val rangoConflicto = remember(fechaInicio, fechaFin) {
                        fechaInicio.isNotBlank() && fechaFin.isNotBlank() &&
                                vm.rangoContieneReservada(fechaInicio, fechaFin)
                    }

                    val fechasValidas  = validacionFechas is ValidationResult.Ok && !rangoConflicto
                    val isDisponible   = oferta.estado.uppercase() == "DISPONIBLE"

                    OfertaDetailContent(
                        oferta            = oferta,
                        fechaInicio       = fechaInicio,
                        fechaFin          = fechaFin,
                        diasSeleccionados = diasSeleccionados,
                        esOfertaPropia    = esOfertaPropia,
                        fechasValidas     = fechasValidas,
                        fechasErrorMessage = when {
                            rangoConflicto -> "El rango seleccionado incluye fechas ya reservadas"
                            else           -> (validacionFechas as? ValidationResult.Error)?.message
                        },
                        isDisponible      = isDisponible,
                        fechasReservadas  = fechasReservadas,
                        onFechaInicioChange = { vm.setFechaInicio(it) },
                        onFechaFinChange    = { vm.setFechaFin(it) },
                        onVerVehiculo       = { onVerVehiculo(oferta.idVehiculo) },
                        onReservar          = {
                            onReservar(
                                SessionManager.usuarioId,
                                fechaInicio,
                                fechaFin,
                                diasSeleccionados,
                                oferta.precioPorDia ?: 0.0
                            )
                        }
                    )
                }
                else -> Unit
            }

            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopStart)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(Icons.Filled.ArrowBack, "Volver", tint = Color.White)
            }

            IconButton(
                onClick = { scope.launch { drawerState.open() } },
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopEnd)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(Icons.Filled.Menu, "Menú", tint = Color.White)
            }
        }
    }
}

@Composable
private fun OfertaDetailContent(
    oferta: Oferta,
    fechaInicio: String,
    fechaFin: String,
    diasSeleccionados: Int,
    esOfertaPropia: Boolean,
    fechasValidas: Boolean,
    fechasErrorMessage: String?,
    isDisponible: Boolean,
    fechasReservadas: Set<String>,
    onFechaInicioChange: (String) -> Unit,
    onFechaFinChange: (String) -> Unit,
    onVerVehiculo: () -> Unit,
    onReservar: () -> Unit
) {
    val scrollState = rememberScrollState()

    val hoyCalendar = remember {
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }

    val minFechaFinCalendar = remember(fechaInicio) {
        if (fechaInicio.isNotBlank()) {
            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                sdf.isLenient = false
                val cal = Calendar.getInstance()
                sdf.parse(fechaInicio)?.let { cal.time = it }
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                cal.add(Calendar.DAY_OF_MONTH, 1)
                cal
            } catch (e: Exception) {
                Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                    add(Calendar.DAY_OF_MONTH, 1)
                }
            }
        } else {
            Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {

        // ── HERO ────────────────────────────────────────────
        Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
            VehiculoImagePlaceholder(tipo = "COCHE", modifier = Modifier.fillMaxSize())
            Box(
                modifier = Modifier.fillMaxSize().background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(0.75f)),
                        startY = 100f
                    )
                )
            )
            Column(
                modifier = Modifier.align(Alignment.BottomStart).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                EstadoChip(oferta.estado)
                Text(
                    "Oferta #${oferta.id}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                oferta.precioPorDia?.let {
                    Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("${String.format("%.0f", it)}€", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("/día", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(0.7f), modifier = Modifier.padding(bottom = 2.dp))
                    }
                }
            }
        }

        // ── CUERPO ────────────────────────────────────────
        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            // — Banners de aviso —————————————————————
            AnimatedVisibility(visible = esOfertaPropia, enter = fadeIn(), exit = fadeOut()) {
                BannerInfoCard(
                    message         = "No puedes reservar tu propio vehículo",
                    icon            = Icons.Filled.Block,
                    color           = AccentRed,
                    containerColor  = Color(0xFF4A0D0D)
                )
            }
            AnimatedVisibility(visible = !isDisponible && !esOfertaPropia, enter = fadeIn(), exit = fadeOut()) {
                BannerInfoCard(message = "Esta oferta no está disponible actualmente", icon = Icons.Filled.Info)
            }

            // — Galería ————————————————————————————————
            SectionHeader("Galería")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(4) {
                    VehiculoImagePlaceholder(
                        tipo = "COCHE",
                        modifier = Modifier.size(width = 160.dp, height = 100.dp).clip(AppShapes.large)
                    )
                }
            }

            // — Detalles ———————————————————————————————
            SectionHeader("Detalles")
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    InfoRow("ID Vehículo", "${oferta.idVehiculo}")
                    oferta.precioPorDia?.let { InfoRow("Precio por día", "${String.format("%.2f", it)}€") }
                    InfoRow("Reservas activas", "${oferta.reservas.size}")
                    InfoRow("Valoraciones", "${oferta.valoraciones.size}")
                }
            }

            // — Selector de fechas ——————————————————————
            SectionHeader("Selecciona las fechas")

            // Leyenda de disponibilidad solo si hay fechas reservadas
            if (fechasReservadas.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Surface(color = Primary, shape = AppShapes.pill, modifier = Modifier.size(10.dp)) {}
                        Text("Disponible", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Surface(color = OnSurfaceMuted, shape = AppShapes.pill, modifier = Modifier.size(10.dp)) {}
                        Text("Reservado", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                    }
                }
            }

            AppCard(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {

                    // Fecha inicio: mínimo hoy, bloqueando reservadas
                    DatePickerField(
                        label            = "Fecha de inicio",
                        value            = fechaInicio,
                        onValueChange    = onFechaInicioChange,
                        minDate          = hoyCalendar,
                        fechasReservadas = fechasReservadas
                    )

                    // Fecha fin: mínimo día siguiente al inicio, bloqueando reservadas
                    DatePickerField(
                        label            = "Fecha de fin",
                        value            = fechaFin,
                        onValueChange    = onFechaFinChange,
                        minDate          = minFechaFinCalendar,
                        isError          = !fechasValidas && fechaFin.isNotBlank(),
                        errorMessage     = fechasErrorMessage,
                        fechasReservadas = fechasReservadas
                    )

                    // Resumen dinámico
                    AnimatedVisibility(
                        visible = fechasValidas && diasSeleccionados > 0 && oferta.precioPorDia != null,
                        enter   = fadeIn(),
                        exit    = fadeOut()
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            SubtleDivider()
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        "$diasSeleccionados día${if (diasSeleccionados != 1) "s" else ""} × ${String.format("%.0f", oferta.precioPorDia)}€",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = OnSurfaceVariant
                                    )
                                    Text(
                                        "${String.format("%.2f", (oferta.precioPorDia ?: 0.0) * diasSeleccionados)}€ total",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Primary
                                    )
                                }
                                Surface(color = Color(0xFF0D3320), shape = AppShapes.pill) {
                                    Text(
                                        "$diasSeleccionados días",
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        style    = MaterialTheme.typography.labelMedium,
                                        color    = AccentGreen,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // — Valoraciones ————————————————————————————
            if (oferta.valoraciones.isNotEmpty()) {
                SectionHeader("Valoraciones")
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    oferta.valoraciones.forEach { ValoracionCard(it) }
                }
            }

            // — Botones ————————————————————————————————
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                val puedeReservar = isDisponible && !esOfertaPropia && fechasValidas && diasSeleccionados > 0
                val textoBoton = when {
                    esOfertaPropia                           -> "Tu propio vehículo"
                    !isDisponible                            -> "No disponible"
                    !fechasValidas && fechaFin.isNotBlank()  -> "Fechas no disponibles"
                    !fechasValidas || diasSeleccionados == 0 -> "Selecciona fechas"
                    else -> "Reservar · ${String.format("%.0f", (oferta.precioPorDia ?: 0.0) * diasSeleccionados)}€"
                }
                PrimaryButton(
                    text    = textoBoton,
                    onClick = onReservar,
                    enabled = puedeReservar,
                    icon    = if (puedeReservar) Icons.Filled.BookOnline else null
                )
                SecondaryButton(text = "Ver vehículo", onClick = onVerVehiculo, icon = Icons.Filled.DirectionsCar)
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}
