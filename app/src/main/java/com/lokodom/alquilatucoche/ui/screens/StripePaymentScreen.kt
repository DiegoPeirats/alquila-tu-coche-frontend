package com.lokodom.alquilatucoche.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lokodom.alquilatucoche.ui.components.InfoRow
import com.lokodom.alquilatucoche.ui.components.LoadingScreen
import com.lokodom.alquilatucoche.viewmodels.PaymentUiState
import com.lokodom.alquilatucoche.viewmodels.PaymentViewModel

/**
 * StripePaymentScreen
 *
 * Arquitectura de pago:
 * 1. Se solicita al backend el clientSecret del PaymentIntent.
 * 2. Se inicializa Stripe PaymentSheet con ese secret.
 * 3. El usuario completa el pago en la PaymentSheet nativa de Stripe.
 * 4. On success → se crea la reserva en el backend.
 *
 * Para habilitar Stripe real:
 * - Añadir en build.gradle: implementation("com.stripe:stripe-android:20.x.x")
 * - Inicializar PaymentConfiguration.init(context, publishableKey) en Application
 * - Usar PaymentSheet.Builder para presentar la hoja de pago
 *
 * En esta implementación se usa modo SIMULADO que puede activarse/desactivarse.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StripePaymentScreen(
    token: String,
    ofertaId: Long,
    usuarioId: Long,
    diasContratados: Int,
    onPagoExitoso: (reservaId: Long) -> Unit,
    onBack: () -> Unit,
    vm: PaymentViewModel = viewModel()
) {
    val state by vm.state.collectAsState()
    val modoSimulado = true  // ← Cambiar a false para integración Stripe real

    // Reaccionar a estado de reserva creada
    LaunchedEffect(state) {
        if (state is PaymentUiState.ReservaCreada) {
            onPagoExitoso((state as PaymentUiState.ReservaCreada).reservaId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, enabled = state !is PaymentUiState.Loading) {
                        Icon(Icons.Filled.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { padding ->
        when (val s = state) {
            is PaymentUiState.Loading -> LoadingScreen()

            is PaymentUiState.Error -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("❌ ${s.message}", color = MaterialTheme.colorScheme.error)
                        Button(onClick = { vm.resetState() }) { Text("Reintentar") }
                    }
                }
            }

            // clientSecret listo → aquí lanzaríamos PaymentSheet de Stripe
            is PaymentUiState.ReadyToPayment -> {
                // TODO para Stripe real:
                // val paymentSheet = rememberPaymentSheet { result -> ... }
                // LaunchedEffect(s) { paymentSheet.presentWithPaymentIntent(s.clientSecret) }
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text("💳 Pago listo para procesar", style = MaterialTheme.typography.titleMedium)
                        Text("clientSecret recibido del backend.", style = MaterialTheme.typography.bodySmall)
                        Button(
                            onClick = {
                                vm.confirmarPagoYCrearReserva(
                                    token = token,
                                    ofertaId = ofertaId,
                                    pagoId = System.currentTimeMillis(),
                                    diasContratados = diasContratados
                                )
                            },
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) { Text("Confirmar pago (integración Stripe)") }
                    }
                }
            }

            // Idle o cualquier otro → mostrar resumen y botón de pago
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Text(
                        "Resumen del pedido",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            InfoRow("Oferta ID", "$ofertaId")
                            InfoRow("Usuario ID", "$usuarioId")
                            InfoRow("Días contratados", "$diasContratados día${if (diasContratados != 1) "s" else ""}")
                            HorizontalDivider()
                            InfoRow("Método de pago", if (modoSimulado) "Simulado (demo)" else "Stripe")
                        }
                    }

                    Spacer(Modifier.weight(1f))

                    if (modoSimulado) {
                        // MODO SIMULADO
                        Button(
                            onClick = {
                                vm.simularPagoExitoso(
                                    token = token,
                                    ofertaId = ofertaId,
                                    diasContratados = diasContratados
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(
                                "✅  Confirmar pago (simulado)",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                        Text(
                            "Modo demo activo. Cambia modoSimulado=false para usar Stripe real.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    } else {
                        // MODO STRIPE REAL
                        Button(
                            onClick = {
                                vm.iniciarPago(
                                    token = token,
                                    ofertaId = ofertaId,
                                    usuarioId = usuarioId,
                                    diasContratados = diasContratados
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("💳  Pagar con Stripe", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        }
    }
}