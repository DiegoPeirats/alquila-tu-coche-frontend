package com.lokodom.alquilatucoche.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lokodom.alquilatucoche.model.peticion.reservas.CreacionReservaRequest
import com.lokodom.alquilatucoche.repository.PaymentRepository
import com.lokodom.alquilatucoche.repository.ReservasRepository
import com.lokodom.alquilatucoche.utils.UiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

// Estado que expone el ViewModel de pago
sealed class PaymentUiState {
    object Idle : PaymentUiState()
    object Loading : PaymentUiState()
    // clientSecret listo → mostrar PaymentSheet de Stripe
    data class ReadyToPayment(val clientSecret: String, val publishableKey: String) : PaymentUiState()
    // Reserva creada con éxito → navegar a éxito
    data class ReservaCreada(val reservaId: Long) : PaymentUiState()
    data class Error(val message: String) : PaymentUiState()
}

class PaymentViewModel : ViewModel() {

    private val paymentRepo = PaymentRepository()
    private val reservasRepo = ReservasRepository()

    private val _state = MutableStateFlow<PaymentUiState>(PaymentUiState.Idle)
    val state = _state.asStateFlow()

    /**
     * PASO 1: Solicita al backend el clientSecret de Stripe.
     * El backend crea el PaymentIntent y lo devuelve.
     */
    fun iniciarPago(
        token: String,
        ofertaId: Long,
        usuarioId: Long,
        diasContratados: Int
    ) {
        viewModelScope.launch {
            _state.value = PaymentUiState.Loading

            // Generamos un sessionId temporal (en producción vendría del backend)
            val sessionId = UUID.randomUUID().toString()

            val result = paymentRepo.contratarOferta(
                token = token,
                ofertaId = ofertaId,
                usuarioId = usuarioId,
                diasContratados = diasContratados,
                sessionId = sessionId,
                reservaId = 0L // 0 = nueva reserva (el backend la crea)
            )

            _state.value = when (result) {
                is UiState.Success -> PaymentUiState.ReadyToPayment(
                    clientSecret = result.data,
                    publishableKey = "" // en producción viene del backend
                )
                is UiState.Error -> PaymentUiState.Error(result.message)
                else -> PaymentUiState.Error("Error inesperado")
            }
        }
    }

    /**
     * PASO 2: Stripe confirmó el pago → crear la reserva en el backend.
     */
    fun confirmarPagoYCrearReserva(
        token: String,
        ofertaId: Long,
        pagoId: Long,
        diasContratados: Int
    ) {
        viewModelScope.launch {
            _state.value = PaymentUiState.Loading

            val hoy = LocalDate.now()
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE
            val fechaInicio = hoy.format(formatter)
            val fechaFin = hoy.plusDays(diasContratados.toLong()).format(formatter)

            val result = reservasRepo.crearReserva(
                token = token,
                request = CreacionReservaRequest(
                    estadoReserva = "CONFIRMADA",
                    pagoId = pagoId,
                    fechaInicio = fechaInicio,
                    fechaFin = fechaFin,
                    ofertaId = ofertaId
                )
            )

            _state.value = when (result) {
                is UiState.Success -> PaymentUiState.ReservaCreada(result.data.id)
                is UiState.Error -> PaymentUiState.Error(result.message)
                else -> PaymentUiState.Error("Error inesperado")
            }
        }
    }

    // Para modo demo/simulado sin Stripe real
    fun simularPagoExitoso(
        token: String,
        ofertaId: Long,
        diasContratados: Int
    ) {
        viewModelScope.launch {
            _state.value = PaymentUiState.Loading
            kotlinx.coroutines.delay(2000) // simula latencia

            confirmarPagoYCrearReserva(
                token = token,
                ofertaId = ofertaId,
                pagoId = System.currentTimeMillis(), // ID simulado
                diasContratados = diasContratados
            )
        }
    }

    fun resetState() {
        _state.value = PaymentUiState.Idle
    }
}