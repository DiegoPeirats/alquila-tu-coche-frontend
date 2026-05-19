package com.lokodom.alquilatucoche.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lokodom.alquilatucoche.ui.components.*
import com.lokodom.alquilatucoche.ui.theme.*

@Composable
fun ReservaSuccessScreen(
    reservaId: Long,
    totalPagado: Double,
    fechaInicio: String,
    fechaFin: String,
    dias: Int,
    onVolverAOfertas: () -> Unit
) {
    var animStarted by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (animStarted) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "success_scale"
    )

    LaunchedEffect(Unit) { animStarted = true }

    Scaffold(containerColor = Background) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(Modifier.height(48.dp))

            // — Checkmark animado ——————————————————
            Surface(
                shape = CircleShape,
                color = Color(0xFF0D3320),
                modifier = Modifier.size(100.dp).scale(scale)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Filled.CheckCircle,
                        null,
                        tint = AccentGreen,
                        modifier = Modifier.size(52.dp)
                    )
                }
            }

            // — Textos ——————————————————————————————
            AnimatedVisibility(
                visible = animStarted,
                enter = fadeIn(tween(400, delayMillis = 200)) + slideInVertically(tween(400, 200)) { it / 2 }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "¡Reserva confirmada!",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = OnBackground,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "Tu pago se ha procesado correctamente.\nRecibirás los detalles por email.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // — Resumen del pago ————————————————————
            AnimatedVisibility(
                visible = animStarted,
                enter = fadeIn(tween(400, delayMillis = 350))
            ) {
                AppCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SectionHeader("Detalles de la reserva")
                        SubtleDivider()

                        InfoRow("Nº reserva", "#$reservaId")
                        InfoRow("Fecha inicio", fechaInicio)
                        InfoRow("Fecha fin", fechaFin)
                        InfoRow("Duración", "$dias día${if (dias != 1) "s" else ""}")

                        SubtleDivider()

                        // Total pagado con énfasis visual
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total pagado", style = MaterialTheme.typography.titleSmall, color = OnSurfaceVariant)
                            Text(
                                "${String.format("%.2f", totalPagado)}€",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = AccentGreen
                            )
                        }
                    }
                }
            }

            // — Badge de pago seguro ————————————————
            AnimatedVisibility(
                visible = animStarted,
                enter = fadeIn(tween(400, delayMillis = 500))
            ) {
                Surface(color = Color(0xFF0D3320), shape = AppShapes.pill) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Lock, null, tint = AccentGreen, modifier = Modifier.size(14.dp))
                        Text("Pago procesado de forma segura con Stripe", style = MaterialTheme.typography.labelSmall, color = AccentGreen)
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            // — Botón volver ————————————————————————
            AnimatedVisibility(
                visible = animStarted,
                enter = fadeIn(tween(400, delayMillis = 600)) + slideInVertically(tween(400, 600)) { it }
            ) {
                PrimaryButton(
                    text = "Explorar más ofertas",
                    onClick = onVolverAOfertas,
                    icon = Icons.Filled.Search
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}