package com.lokodom.alquilatucoche.repository

import com.lokodom.alquilatucoche.model.peticion.ofertas.ContratarOfertaRequest
import com.lokodom.alquilatucoche.model.respuesta.ContratarOfertaResponse
import com.lokodom.alquilatucoche.model.peticion.ofertas.DatosPagoOferta
import com.lokodom.alquilatucoche.network.RetrofitClient
import com.lokodom.alquilatucoche.utils.UiState

class PaymentRepository {
    private val api = RetrofitClient.ofertasApi

    /**
     * Llama al backend para contratar la oferta.
     * El backend devuelve un clientSecret de Stripe con el que
     * inicializamos el PaymentSheet en el cliente.
     */
    suspend fun contratarOferta(
        token: String,
        ofertaId: Long,
        usuarioId: Long,
        diasContratados: Int,
        sessionId: String,
        reservaId: Long
    ): UiState<String> {
        return try {
            val request = ContratarOfertaRequest(
                sessionId = sessionId,
                datos = DatosPagoOferta(
                    ofertaId = ofertaId,
                    diasContratados = diasContratados,
                    usuarioId = usuarioId
                ),
                reservaId = reservaId
            )
            val response = api.contratarOferta("Bearer $token", request)
            if (response.isSuccessful) {
                val clientSecret = response.body()?.clientSecret
                if (clientSecret != null) UiState.Success(clientSecret)
                else UiState.Error("No se recibió clientSecret")
            } else {
                UiState.Error("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            UiState.Error(e.message ?: "Error de conexión")
        }
    }
}