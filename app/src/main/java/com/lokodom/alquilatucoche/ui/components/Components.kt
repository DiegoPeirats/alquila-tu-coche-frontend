package com.lokodom.alquilatucoche.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lokodom.alquilatucoche.model.entidad.Valoracion
import com.lokodom.alquilatucoche.ui.theme.*
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import com.lokodom.alquilatucoche.utils.PROVINCIAS_ESPANA
import com.lokodom.alquilatucoche.utils.ValidationResult
import com.lokodom.alquilatucoche.utils.Validators
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

// ─────────────────────────────────────────────
// BOTONES
// ─────────────────────────────────────────────

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    icon: ImageVector? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(54.dp),
        enabled = enabled && !loading,
        shape = AppShapes.large,
        colors = ButtonDefaults.buttonColors(
            containerColor = Primary,
            disabledContainerColor = Border
        )
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                strokeWidth = 2.dp,
                color = OnPrimary
            )
        } else {
            icon?.let {
                Icon(it, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
            }
            Text(text, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(54.dp),
        enabled = enabled,
        shape = AppShapes.large,
        border = BorderStroke(1.dp, Border),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = OnSurface)
    ) {
        icon?.let {
            Icon(it, null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun DestructiveButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    loading: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(54.dp),
        enabled = !loading,
        shape = AppShapes.large,
        colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
    ) {
        if (loading) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp, color = Color.White)
        else Text(text, style = MaterialTheme.typography.labelLarge, color = Color.White)
    }
}

// ─────────────────────────────────────────────
// TARJETAS BASE
// ─────────────────────────────────────────────

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = if (onClick != null) modifier.clickable { onClick() } else modifier,
        shape = AppShapes.xLarge,
        colors = CardDefaults.cardColors(containerColor = SurfaceVariant),
        border = BorderStroke(1.dp, Border)
    ) {
        Column(content = content)
    }
}

// ─────────────────────────────────────────────
// LOADING — Skeleton
// ─────────────────────────────────────────────

@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = AppShapes.medium
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(SurfaceVariant, SurfaceElevated, SurfaceVariant),
        start = Offset(translateAnim - 200f, 0f),
        end = Offset(translateAnim, 0f)
    )

    Box(modifier = modifier.clip(shape).background(shimmerBrush))
}

@Composable
fun OfertaCardSkeleton() {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ShimmerBox(Modifier.fillMaxWidth(0.6f).height(20.dp))
            ShimmerBox(Modifier.fillMaxWidth(0.4f).height(14.dp))
            ShimmerBox(Modifier.fillMaxWidth().height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ShimmerBox(Modifier.width(60.dp).height(24.dp), AppShapes.pill)
                ShimmerBox(Modifier.width(80.dp).height(24.dp), AppShapes.pill)
            }
        }
    }
}

@Composable
fun LoadingScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(4) { OfertaCardSkeleton() }
    }
}

// ─────────────────────────────────────────────
// ESTADOS VACÍOS
// ─────────────────────────────────────────────

@Composable
fun EmptyState(
    icon: ImageVector = Icons.Filled.SearchOff,
    title: String,
    subtitle: String? = null,
    action: Pair<String, () -> Unit>? = null
) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(Spacing.xl)
        ) {
            Surface(
                shape = CircleShape,
                color = SurfaceVariant,
                modifier = Modifier.size(80.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, modifier = Modifier.size(36.dp), tint = OnSurfaceMuted)
                }
            }
            Text(title, style = MaterialTheme.typography.titleMedium, color = OnSurface)
            subtitle?.let { Text(it, style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant) }
            action?.let { (label, onClick) ->
                Spacer(Modifier.height(4.dp))
                PrimaryButton(label, onClick, modifier = Modifier.width(200.dp))
            }
        }
    }
}

// ─────────────────────────────────────────────
// ERROR STATE
// ─────────────────────────────────────────────

@Composable
fun ErrorScreen(message: String, onRetry: (() -> Unit)? = null) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(Spacing.xl)
        ) {
            Surface(shape = CircleShape, color = Color(0xFF4A0D0D), modifier = Modifier.size(80.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.ErrorOutline, null, modifier = Modifier.size(36.dp), tint = AccentRed)
                }
            }
            Text("Algo salió mal", style = MaterialTheme.typography.titleMedium)
            Text(message, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
            onRetry?.let {
                PrimaryButton("Reintentar", it, modifier = Modifier.width(200.dp))
            }
        }
    }
}

// ─────────────────────────────────────────────
// CHIPS / BADGES
// ─────────────────────────────────────────────

@Composable
fun EstadoChip(estado: String) {
    val (bg, fg, label) = when (estado.uppercase()) {
        "ACTIVA", "DISPONIBLE", "CONFIRMADA" ->
            Triple(Color(0xFF0D3320), AccentGreen, estado)
        "INACTIVA", "CANCELADA" ->
            Triple(Color(0xFF4A0D0D), AccentRed, estado)
        "PENDIENTE", "EJECUTANDO" ->
            Triple(Color(0xFF3D2900), AccentAmber, estado)
        "CONTRATADA", "BLOQUEADA", "CERRADA" ->
            Triple(SurfaceElevated, OnSurfaceVariant, estado)
        else -> Triple(SurfaceElevated, OnSurfaceVariant, estado)
    }
    Surface(color = bg, shape = AppShapes.pill) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = fg,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ─────────────────────────────────────────────
// INFO ROW
// ─────────────────────────────────────────────

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = OnSurface)
    }
}

// ─────────────────────────────────────────────
// SELECTOR DE DÍAS
// ─────────────────────────────────────────────

@Composable
fun DiasSelectorRow(dias: Int, onDiasChange: (Int) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Surface(
            shape = CircleShape,
            color = if (dias > 1) Primary else Border,
            modifier = Modifier.size(36.dp).clickable(enabled = dias > 1) { onDiasChange(dias - 1) }
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text("−", style = MaterialTheme.typography.titleMedium, color = OnPrimary)
            }
        }
        Text("$dias día${if (dias != 1) "s" else ""}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Surface(
            shape = CircleShape,
            color = Primary,
            modifier = Modifier.size(36.dp).clickable { onDiasChange(dias + 1) }
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text("+", style = MaterialTheme.typography.titleMedium, color = OnPrimary)
            }
        }
    }
}

// ─────────────────────────────────────────────
// VALORACIÓN CARD
// ─────────────────────────────────────────────

@Composable
fun ValoracionCard(valoracion: Valoracion) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row {
                repeat(5) { idx ->
                    Icon(
                        if (idx < valoracion.valoracion) Icons.Filled.Star else Icons.Outlined.StarOutline,
                        null,
                        tint = AccentAmber,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
            Text(valoracion.mensaje, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

// ─────────────────────────────────────────────
// SECTION HEADER
// ─────────────────────────────────────────────

@Composable
fun SectionHeader(title: String, trailing: @Composable (() -> Unit)? = null) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        trailing?.invoke()
    }
}

// ─────────────────────────────────────────────
// DIVIDER SUTIL
// ─────────────────────────────────────────────

@Composable
fun SubtleDivider() = HorizontalDivider(color = Border, thickness = 0.5.dp)

// ─────────────────────────────────────────────
// PRECIO DISPLAY grande
// ─────────────────────────────────────────────

@Composable
fun PrecioDisplay(precioPorDia: Double, dias: Int) {
    val total = precioPorDia * dias
    Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            "${String.format("%.0f", precioPorDia)}€",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = OnBackground
        )
        Text("/día", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant, modifier = Modifier.padding(bottom = 4.dp))
        if (dias > 1) {
            Spacer(Modifier.width(8.dp))
            Text(
                "· ${String.format("%.0f", total)}€ total",
                style = MaterialTheme.typography.bodyMedium,
                color = Primary,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
    }
}

// ─────────────────────────────────────────────
// INPUT FIELD ESTILIZADO
// ─────────────────────────────────────────────

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    keyboardOptions: androidx.compose.foundation.text.KeyboardOptions = androidx.compose.foundation.text.KeyboardOptions.Default,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation = androidx.compose.ui.text.input.VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    supportingText: @Composable (() -> Unit)? = null,
    readOnly: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, style = MaterialTheme.typography.bodySmall) },
        modifier = modifier.fillMaxWidth(),
        singleLine = singleLine,
        shape = AppShapes.large,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
        isError = isError,
        supportingText = supportingText,
        readOnly = readOnly,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Primary,
            unfocusedBorderColor = Border,
            focusedLabelColor = Primary,
            unfocusedLabelColor = OnSurfaceVariant,
            focusedTextColor = OnSurface,
            unfocusedTextColor = OnSurface,
            cursorColor = Primary,
            focusedContainerColor = SurfaceVariant,
            unfocusedContainerColor = SurfaceVariant
        )
    )
}

// ─────────────────────────────────────────────
// IMAGEN PLACEHOLDER vehículo
// ─────────────────────────────────────────────

@Composable
fun VehiculoImagePlaceholder(
    tipo: String = "COCHE",
    modifier: Modifier = Modifier
) {
    val emoji = when (tipo.uppercase()) {
        "MOTO" -> "🏍️"
        "FURGONETA" -> "🚐"
        "CAMION" -> "🚛"
        else -> "🚗"
    }
    Box(
        modifier = modifier.background(
            Brush.verticalGradient(listOf(Color(0xFF1A1A2E), Color(0xFF0A0A0F)))
        ),
        contentAlignment = Alignment.Center
    ) {
        Text(emoji, style = MaterialTheme.typography.displayLarge)
    }
}

// ─────────────────────────────────────────────
// DatePickerField
// Campo de fecha con DatePickerDialog de Material 3
// ─────────────────────────────────────────────
fun isoToMillisUtc(iso: String): Long? {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        sdf.isLenient = false
        sdf.parse(iso)?.time
    } catch (e: Exception) { null }
}

fun millisUtcToIso(millis: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    return sdf.format(Date(millis))
}

fun isoToDisplay(iso: String): String {
    return try {
        val sdfIn  = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val sdfOut = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date   = sdfIn.parse(iso) ?: return ""
        sdfOut.format(date)
    } catch (e: Exception) { "" }
}

fun calendarToMillisUtc(cal: Calendar): Long {
    val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    utc.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0)
    utc.set(Calendar.MILLISECOND, 0)
    return utc.timeInMillis
}

// ─────────────────────────────────────────────
// DatePickerField
// - minDate: Calendar con fecha mínima seleccionable
// - fechasReservadas: Set<String> ISO que se bloquean en el picker
// ─────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    minDate: Calendar,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null,
    fechasReservadas: Set<String> = emptySet()   // ← nuevo parámetro
) {
    var showPicker by remember { mutableStateOf(false) }

    val minMillis   = remember(minDate) { calendarToMillisUtc(minDate) }
    val initialMillis = remember(value) { isoToMillisUtc(value) ?: System.currentTimeMillis() }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialMillis,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                if (utcTimeMillis < minMillis) return false
                // Bloquear fechas reservadas
                if (fechasReservadas.isNotEmpty()) {
                    val iso = millisUtcToIso(utcTimeMillis)
                    if (iso in fechasReservadas) return false
                }
                return true
            }
        }
    )

    val displayValue = remember(value) { isoToDisplay(value) }

    Column(modifier = modifier) {
        AppTextField(
            value = displayValue,
            onValueChange = {},
            label = label,
            readOnly = true,
            isError = isError,
            trailingIcon = {
                IconButton(onClick = { showPicker = true }) {
                    Icon(
                        Icons.Filled.CalendarMonth,
                        "Seleccionar fecha",
                        tint = when {
                            isError -> MaterialTheme.colorScheme.error
                            value.isNotBlank() -> AccentGreen
                            else -> Primary
                        }
                    )
                }
            }
        )
        AnimatedVisibility(visible = isError && errorMessage != null, enter = fadeIn(), exit = fadeOut()) {
            Text(
                errorMessage ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }

    if (showPicker) {
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            onValueChange(millisUtcToIso(millis))
                        }
                        showPicker = false
                    }
                ) { Text("Aceptar", color = Primary) }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) {
                    Text("Cancelar", color = OnSurfaceVariant)
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor             = SurfaceVariant,
                    titleContentColor          = OnSurface,
                    headlineContentColor       = OnBackground,
                    weekdayContentColor        = OnSurfaceVariant,
                    dayContentColor            = OnSurface,
                    disabledDayContentColor    = OnSurfaceMuted,   // ← días reservados en gris apagado
                    selectedDayContentColor    = OnPrimary,
                    selectedDayContainerColor  = Primary,
                    todayContentColor          = Primary,
                    todayDateBorderColor       = Primary,
                    navigationContentColor     = OnSurface,
                    yearContentColor           = OnSurface,
                    currentYearContentColor    = Primary,
                    selectedYearContentColor   = OnPrimary,
                    selectedYearContainerColor = Primary
                )
            )
        }
    }
}
// ─────────────────────────────────────────────
// ProvinciaDropdown
// Dropdown con búsqueda de provincia española
// ─────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProvinciaDropdown(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Provincia"
) {
    var expanded by remember { mutableStateOf(false) }
    var query by remember(value) { mutableStateOf(value) }

    // Filtrar provincias según lo que escribe el usuario
    val filtered = remember(query) {
        if (query.isBlank()) PROVINCIAS_ESPANA
        else PROVINCIAS_ESPANA.filter { it.contains(query, ignoreCase = true) }
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        AppTextField(
            value = query,
            onValueChange = { query = it; expanded = true },
            label = label,
            modifier = Modifier.menuAnchor(),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            }
        )

        if (filtered.isNotEmpty()) {
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.heightIn(max = 240.dp),
                containerColor = SurfaceElevated
            ) {
                filtered.forEach { provincia ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                provincia,
                                style = MaterialTheme.typography.bodyMedium,
                                color = OnSurface
                            )
                        },
                        onClick = {
                            query = provincia
                            onValueChange(provincia)
                            expanded = false
                        },
                        colors = MenuDefaults.itemColors(textColor = OnSurface)
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
// MatriculaField
// Input con validación regex en tiempo real
// ─────────────────────────────────────────────
@Composable
fun MatriculaField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val validacion = remember(value) {
        if (value.isBlank()) null else Validators.validarMatricula(value)
    }
    val isError = validacion is ValidationResult.Error

    Column(modifier = modifier) {
        AppTextField(
            value = value,
            onValueChange = { nuevo ->
                // Forzar mayúsculas y limitar longitud a 7
                onValueChange(nuevo.uppercase().take(7))
            },
            label = "Matrícula (ej: 1234ABC)",
            isError = isError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Characters
            ),
            trailingIcon = {
                when (validacion) {
                    is ValidationResult.Ok -> Icon(
                        Icons.Filled.CheckCircle, null,
                        tint = AccentGreen, modifier = Modifier.size(20.dp)
                    )
                    is ValidationResult.Error -> Icon(
                        Icons.Filled.Error, null,
                        tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp)
                    )
                    null -> {}
                }
            }
        )
        AnimatedVisibility(visible = isError, enter = fadeIn(), exit = fadeOut()) {
            Text(
                validacion?.errorMessage ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
        AnimatedVisibility(visible = validacion is ValidationResult.Ok) {
            Text(
                "Matrícula válida ✓",
                style = MaterialTheme.typography.bodySmall,
                color = AccentGreen,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

// ─────────────────────────────────────────────
// BannerInfoCard
// Banner de aviso elegante (ej: "No puedes reservar tu vehículo")
// ─────────────────────────────────────────────
@Composable
fun BannerInfoCard(
    message: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector = Icons.Filled.Info,
    color: androidx.compose.ui.graphics.Color = AccentAmber,
    containerColor: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color(0xFF3D2900),
    modifier: Modifier = Modifier
) {
    Surface(
        color = containerColor,
        shape = AppShapes.large,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            Text(
                message,
                style = MaterialTheme.typography.bodyMedium,
                color = color,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
