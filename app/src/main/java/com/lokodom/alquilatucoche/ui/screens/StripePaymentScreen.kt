package com.lokodom.alquilatucoche.ui.screens
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lokodom.alquilatucoche.ui.components.*
import com.lokodom.alquilatucoche.ui.theme.*
import com.lokodom.alquilatucoche.viewmodels.PaymentContext
import com.lokodom.alquilatucoche.viewmodels.PaymentStep
import com.lokodom.alquilatucoche.viewmodels.PaymentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StripePaymentScreen(
    token: String,
    ofertaId: Long,
    usuarioId: Long,
    diasContratados: Int,
    precioPorDia: Double,
    fechaInicio: String,
    fechaFin: String,
    onPagoExitoso: (reservaId: Long, totalPagado: Double, fechaInicio: String, fechaFin: String, dias: Int) -> Unit,
    onBack: () -> Unit,
    vm: PaymentViewModel = viewModel()
) {
    val step by vm.step.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Total calculado aquí para garantizar que es siempre correcto
    val totalEuros = precioPorDia * diasContratados

    // PaymentContext empaqueta todo — se construye una sola vez
    val paymentContext = remember {
        PaymentContext(
            token = token,
            ofertaId = ofertaId,
            usuarioId = usuarioId,
            diasContratados = diasContratados,
            precioPorDia = precioPorDia,
            fechaInicio = fechaInicio,
            fechaFin = fechaFin
        )
    }

    // ── Detectar retorno desde Stripe (app vuelve a primer plano) ─
    // Cuando el usuario completa o cancela el pago en el CustomTab,
    // la Activity vuelve a ON_RESUME. Aquí decidimos qué hacer.
    var stripeAbierto by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && stripeAbierto) {
                stripeAbierto = false
                // El usuario volvió de Stripe — llamamos a confirmar.
                // En producción se debería verificar el estado real de la sesión.
                // Aquí confiamos en el flujo optimista: si volvió, asumimos éxito.
                // Para mayor seguridad, usa un deep link con el resultado de Stripe.
                vm.confirmarPago()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // ── Abrir CustomTab cuando el step es RedirigendoAStripe ──────
    LaunchedEffect(step) {
        if (step is PaymentStep.RedirigendoAStripe) {
            val s = step as PaymentStep.RedirigendoAStripe
            val customTab = CustomTabsIntent.Builder()
                .setShowTitle(true)
                .build()
            try {
                stripeAbierto = true
                customTab.launchUrl(context, Uri.parse(s.url))
            } catch (e: Exception) {
                // Fallback si no hay navegador compatible
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(s.url)))
            }
        }
    }

    // ── Navegar al éxito ──────────────────────────────────────────
    LaunchedEffect(step) {
        if (step is PaymentStep.Exito) {
            val data = (step as PaymentStep.Exito).data
            onPagoExitoso(data.reservaId, data.totalPagado, data.fechaInicio, data.fechaFin, data.dias)
        }
    }

    // Bloquear botón atrás durante el procesamiento
    BackHandler(enabled = step is PaymentStep.ConfirmandoPago || step is PaymentStep.CreandoReserva) {}

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = { Text("Checkout", fontWeight = FontWeight.Bold, color = OnBackground) },
                navigationIcon = {
                    val bloqueado = step is PaymentStep.ConfirmandoPago ||
                            step is PaymentStep.CreandoReserva ||
                            step is PaymentStep.IniciandoPago
                    IconButton(onClick = onBack, enabled = !bloqueado) {
                        Icon(Icons.Filled.ArrowBack, "Volver", tint = if (!bloqueado) OnSurface else OnSurfaceMuted)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = step,
                transitionSpec = {
                    fadeIn(tween(300)) togetherWith fadeOut(tween(200))
                },
                label = "payment_step"
            ) { currentStep ->
                when (currentStep) {

                    // ── IDLE: resumen del pedido ─────────────────
                    is PaymentStep.Idle -> {
                        PaymentSummary(
                            ofertaId = ofertaId,
                            fechaInicio = fechaInicio,
                            fechaFin = fechaFin,
                            diasContratados = diasContratados,
                            precioPorDia = precioPorDia,
                            totalEuros = totalEuros,
                            onConfirmar = { vm.iniciarPago(paymentContext) }
                        )
                    }

                    // ── CARGANDO ─────────────────────────────────
                    is PaymentStep.IniciandoPago -> PaymentLoadingState(
                        mensaje = "Preparando pago seguro..."
                    )

                    is PaymentStep.RedirigendoAStripe -> PaymentLoadingState(
                        mensaje = "Abriendo pasarela de pago..."
                    )

                    is PaymentStep.ConfirmandoPago -> PaymentLoadingState(
                        mensaje = "Confirmando tu pago..."
                    )

                    is PaymentStep.CreandoReserva -> PaymentLoadingState(
                        mensaje = "Creando tu reserva..."
                    )

                    // ── ERROR ─────────────────────────────────────
                    is PaymentStep.Error -> PaymentErrorState(
                        message = currentStep.message,
                        retryable = currentStep.retryable,
                        onReintentar = { vm.reintentar() },
                        onVolver = { vm.reset(); onBack() }
                    )

                    // ── ÉXITO: gestionado por LaunchedEffect ──────
                    is PaymentStep.Exito -> PaymentLoadingState(mensaje = "¡Pago completado!")
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
// Componentes internos de StripePaymentScreen
// ─────────────────────────────────────────────

@Composable
private fun PaymentSummary(
    ofertaId: Long,
    fechaInicio: String,
    fechaFin: String,
    diasContratados: Int,
    precioPorDia: Double,
    totalEuros: Double,
    onConfirmar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            "Resumen del pedido",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = OnBackground
        )

        // — Detalles de la reserva ——————————
        AppCard(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                InfoRow("Oferta", "#$ofertaId")
                InfoRow("Fecha inicio", fechaInicio)
                InfoRow("Fecha fin", fechaFin)
                InfoRow("Duración", "$diasContratados día${if (diasContratados != 1) "s" else ""}")
                InfoRow("Precio/día", "${String.format("%.2f", precioPorDia)}€")
                SubtleDivider()
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = OnBackground)
                    Text(
                        "${String.format("%.2f", totalEuros)}€",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                }
            }
        }

        // — Método de pago ——————————————————
        AppCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                Modifier.padding(20.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Surface(
                    color = Primary.copy(0.15f),
                    shape = AppShapes.medium,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.CreditCard, null, tint = Primary, modifier = Modifier.size(22.dp))
                    }
                }
                Column {
                    Text("Stripe · Pago seguro", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = OnBackground)
                    Text("Cifrado SSL · PCI DSS", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                }
                Spacer(Modifier.weight(1f))
                Icon(Icons.Filled.Lock, null, tint = AccentGreen, modifier = Modifier.size(16.dp))
            }
        }

        Spacer(Modifier.weight(1f))

        PrimaryButton(
            text = "Pagar ${String.format("%.2f", totalEuros)}€",
            onClick = onConfirmar,
            icon = Icons.Filled.Lock
        )

        Text(
            "Serás redirigido a Stripe para completar el pago de forma segura.",
            style = MaterialTheme.typography.bodySmall,
            color = OnSurfaceMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun PaymentLoadingState(mensaje: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "payment_loading")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier.padding(40.dp)
    ) {
        CircularProgressIndicator(
            color = Primary,
            strokeWidth = 3.dp,
            modifier = Modifier.size(56.dp)
        )
        Text(
            mensaje,
            style = MaterialTheme.typography.titleMedium,
            color = OnSurface.copy(alpha = alpha),
            textAlign = TextAlign.Center
        )
        Text(
            "No cierres la aplicación",
            style = MaterialTheme.typography.bodySmall,
            color = OnSurfaceMuted
        )
    }
}

@Composable
private fun PaymentErrorState(
    message: String,
    retryable: Boolean,
    onReintentar: () -> Unit,
    onVolver: () -> Unit
) {
    Column(
        modifier = Modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Surface(shape = CircleShape, color = Color(0xFF4A0D0D), modifier = Modifier.size(80.dp)) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.ErrorOutline, null, tint = AccentRed, modifier = Modifier.size(36.dp))
            }
        }

        Text("Error en el pago", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = OnBackground)
        Text(message, style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant, textAlign = TextAlign.Center)

        if (!retryable) {
            BannerInfoCard(
                message = "El pago pudo haberse procesado. Contacta con soporte si tienes dudas.",
                icon = Icons.Filled.Info,
                color = AccentAmber,
                containerColor = Color(0xFF3D2900)
            )
        }

        Spacer(Modifier.height(8.dp))

        if (retryable) {
            PrimaryButton("Reintentar", onClick = onReintentar, icon = Icons.Filled.Refresh)
        }
        SecondaryButton("Volver", onClick = onVolver, icon = Icons.Filled.ArrowBack)
    }
}