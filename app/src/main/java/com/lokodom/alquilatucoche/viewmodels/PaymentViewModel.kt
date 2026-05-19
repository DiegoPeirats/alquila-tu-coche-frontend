package com.lokodom.alquilatucoche.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lokodom.alquilatucoche.model.respuesta.ConfirmacionPago
import com.lokodom.alquilatucoche.model.respuesta.ReservaSuccessData
import com.lokodom.alquilatucoche.repository.PaymentRepository
import com.lokodom.alquilatucoche.utils.UiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class PaymentStep {
    object Idle : PaymentStep()
    object IniciandoPago : PaymentStep()
    data class RedirigendoAStripe(
        val sessionId: String,
        val url: String,
        val ofertaId: Long,
        val usuarioId: Long,
        val diasContratados: Int,
        val totalEuros: Double,
        val fechaInicio: String,
        val fechaFin: String
    ) : PaymentStep()
    object ConfirmandoPago : PaymentStep()
    object CreandoReserva : PaymentStep()
    data class Exito(val data: ReservaSuccessData) : PaymentStep()
    data class Error(
        val message: String,
        val retryable: Boolean = true,
        val context: PaymentContext? = null
    ) : PaymentStep()
}

data class PaymentContext(
    val token: String,
    val ofertaId: Long,
    val usuarioId: Long,
    val diasContratados: Int,
    val precioPorDia: Double,
    val fechaInicio: String,
    val fechaFin: String
) {
    val totalEuros: Double get() = precioPorDia * diasContratados
    val totalLong: Long get() = totalEuros.toLong()
}

class PaymentViewModel : ViewModel() {

    private val repo = PaymentRepository()

    private val _step = MutableStateFlow<PaymentStep>(PaymentStep.Idle)
    val step = _step.asStateFlow()

    private var ctx: PaymentContext? = null
    private var sessionId: String? = null
    private var pagoId: Long? = null   // ← nuevo: guardado tras confirmarPago

    // PASO 2: Solicitar sesión Stripe
    fun iniciarPago(context: PaymentContext) {
        ctx = context
        viewModelScope.launch {
            _step.value = PaymentStep.IniciandoPago
            val result = repo.iniciarPago(
                token           = context.token,
                ofertaId        = context.ofertaId,
                diasContratados = context.diasContratados,
                usuarioId       = context.usuarioId
            )
            _step.value = when (result) {
                is UiState.Success -> {
                    sessionId = result.data.sessionId
                    PaymentStep.RedirigendoAStripe(
                        sessionId       = result.data.sessionId,
                        url             = result.data.url,
                        ofertaId        = context.ofertaId,
                        usuarioId       = context.usuarioId,
                        diasContratados = context.diasContratados,
                        totalEuros      = context.totalEuros,
                        fechaInicio     = context.fechaInicio,
                        fechaFin        = context.fechaFin
                    )
                }
                is UiState.Error -> PaymentStep.Error(result.message, context = context)
                else -> PaymentStep.Error("Error inesperado", context = context)
            }
        }
    }

    // PASO 5: Confirmar pago → obtiene pagoId real
    fun confirmarPago() {
        val context = ctx ?: return
        val sid     = sessionId ?: return

        viewModelScope.launch {
            _step.value = PaymentStep.ConfirmandoPago

            val result = repo.confirmarPago(
                token        = context.token,
                sessionId    = sid,
                usuarioId    = context.usuarioId,
                precioTotal  = context.totalLong
            )

            when (result) {
                is UiState.Success -> {
                    // Guardamos el pagoId devuelto por el backend
                    pagoId = result.data.pagoId
                    crearReserva(context, result.data)
                }
                is UiState.Error -> _step.value = PaymentStep.Error(
                    message   = result.message,
                    context   = context
                )
                else -> _step.value = PaymentStep.Error("Error inesperado", context = context)
            }
        }
    }

    // PASO 6+7: Crear reserva con pagoId real de ConfirmacionPago
    private suspend fun crearReserva(context: PaymentContext, confirmacion: ConfirmacionPago) {
        _step.value = PaymentStep.CreandoReserva

        // Validación defensiva: no crear reserva si pagoId es inválido
        if (confirmacion.pagoId <= 0L) {
            _step.value = PaymentStep.Error(
                message   = "pagoId inválido recibido del backend",
                retryable = false,
                context   = context
            )
            return
        }

        val result = repo.crearReserva(
            token       = context.token,
            ofertaId    = context.ofertaId,
            fechaInicio = context.fechaInicio,
            fechaFin    = context.fechaFin,
            pagoId      = confirmacion.pagoId   // ← pagoId real
        )

        _step.value = when (result) {
            is UiState.Success -> PaymentStep.Exito(
                ReservaSuccessData(
                    reservaId   = result.data.id,
                    totalPagado = context.totalEuros,
                    fechaInicio = context.fechaInicio,
                    fechaFin    = context.fechaFin,
                    dias        = context.diasContratados
                )
            )
            is UiState.Error -> PaymentStep.Error(
                message   = result.message,
                retryable = false,   // pago ya cobrado → no reintentar
                context   = context
            )
            else -> PaymentStep.Error("Error al crear la reserva", retryable = false, context = context)
        }
    }

    fun pagoCancelado() { _step.value = PaymentStep.Idle }

    fun reintentar() {
        val errorCtx = (step.value as? PaymentStep.Error)?.context ?: return
        iniciarPago(errorCtx)
    }

    fun reset() {
        _step.value = PaymentStep.Idle
        ctx       = null
        sessionId = null
        pagoId    = null
    }
}