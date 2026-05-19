package com.lokodom.alquilatucoche.repository

import com.lokodom.alquilatucoche.model.entidad.Reserva
import com.lokodom.alquilatucoche.model.peticion.pagos.DatosConfirmarPago
import com.lokodom.alquilatucoche.model.peticion.ofertas.DatosPagoOferta
import com.lokodom.alquilatucoche.model.peticion.reservas.CreacionReservaRequest
import com.lokodom.alquilatucoche.model.respuesta.ConfirmacionPago
import com.lokodom.alquilatucoche.model.respuesta.StripeSessionDetails
import com.lokodom.alquilatucoche.network.RetrofitClient
import com.lokodom.alquilatucoche.utils.UiState

class PaymentRepository {

    private val pagosApi   = RetrofitClient.pagosApi
    private val reservasApi = RetrofitClient.reservasApi

    suspend fun iniciarPago(
        token: String,
        ofertaId: Long,
        diasContratados: Int,
        usuarioId: Long
    ): UiState<StripeSessionDetails> = safeCall {
        pagosApi.recibirPago(
            "Bearer $token",
            DatosPagoOferta(ofertaId = ofertaId, diasContratados = diasContratados, usuarioId = usuarioId)
        )
    }

    // ← devuelve ConfirmacionPago (no String)
    suspend fun confirmarPago(
        token: String,
        sessionId: String,
        usuarioId: Long,
        precioTotal: Long
    ): UiState<ConfirmacionPago> = safeCall {
        pagosApi.confirmarPago(
            "Bearer $token",
            DatosConfirmarPago(sessionId = sessionId, usuarioId = usuarioId, precio = precioTotal)
        )
    }

    // ← pagoId ya no es 0L fijo: viene de ConfirmacionPago
    suspend fun crearReserva(
        token: String,
        ofertaId: Long,
        fechaInicio: String,
        fechaFin: String,
        pagoId: Long
    ): UiState<Reserva> = safeCall {
        reservasApi.crearReserva(
            "Bearer $token",
            CreacionReservaRequest(
                estadoReserva = "PENDIENTE",
                pagoId        = pagoId,
                fechaInicio   = fechaInicio,
                fechaFin      = fechaFin,
                ofertaId      = ofertaId
            )
        )
    }

    private suspend fun <T> safeCall(call: suspend () -> retrofit2.Response<T>): UiState<T> {
        return try {
            val r = call()
            if (r.isSuccessful) {
                val body = r.body()
                if (body != null) UiState.Success(body)
                else UiState.Error("Respuesta vacía del servidor")
            } else {
                UiState.Error("Error ${r.code()}: ${r.message()}")
            }
        } catch (e: Exception) {
            UiState.Error(e.message ?: "Error de conexión")
        }
    }
}