package com.lokodom.alquilatucoche.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lokodom.alquilatucoche.model.entidad.Valoracion

// ─── Loading full-screen ───────────────────────────────────────────────────
@Composable
fun LoadingScreen() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

// ─── Error full-screen ────────────────────────────────────────────────────
@Composable
fun ErrorScreen(message: String, onRetry: (() -> Unit)? = null) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(text = "⚠️ $message", style = MaterialTheme.typography.bodyLarge)
            if (onRetry != null) {
                Button(onClick = onRetry) { Text("Reintentar") }
            }
        }
    }
}

// ─── Chip de estado de oferta ─────────────────────────────────────────────
@Composable
fun EstadoChip(estado: String) {
    val color = when (estado.uppercase()) {
        "ACTIVA"    -> MaterialTheme.colorScheme.primaryContainer
        "INACTIVA"  -> MaterialTheme.colorScheme.errorContainer
        "PENDIENTE" -> MaterialTheme.colorScheme.tertiaryContainer
        "CERRADA"   -> MaterialTheme.colorScheme.surfaceVariant
        else        -> MaterialTheme.colorScheme.surfaceVariant
    }
    Surface(
        color = color,
        shape = RoundedCornerShape(50),
        modifier = Modifier.wrapContentSize()
    ) {
        Text(
            text = estado,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ─── Card de valoración ───────────────────────────────────────────────────
@Composable
fun ValoracionCard(valoracion: Valoracion) {
    Card(
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
            Text(text = valoracion.mensaje, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

// ─── Selector de días ─────────────────────────────────────────────────────
@Composable
fun DiasSelectorRow(
    dias: Int,
    onDiasChange: (Int) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FilledIconButton(
            onClick = { if (dias > 1) onDiasChange(dias - 1) },
            enabled = dias > 1
        ) { Text("−") }

        Text(
            text = "$dias día${if (dias != 1) "s" else ""}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        FilledIconButton(onClick = { onDiasChange(dias + 1) }) {
            Text("+")
        }
    }
}

// ─── Botón primario grande ────────────────────────────────────────────────
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        enabled = enabled,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

// ─── Botón secundario grande ──────────────────────────────────────────────
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

// ─── Info Row (etiqueta + valor) ──────────────────────────────────────────
@Composable
fun InfoRow(label: String, value: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}
