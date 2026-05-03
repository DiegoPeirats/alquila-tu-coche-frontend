package com.lokodom.alquilatucoche.repository

import com.lokodom.alquilatucoche.model.entidad.Reserva
import com.lokodom.alquilatucoche.model.peticion.reservas.CreacionReservaRequest
import com.lokodom.alquilatucoche.network.RetrofitClient
import com.lokodom.alquilatucoche.utils.UiState

class ReservasRepository {
    private val api = RetrofitClient.reservasApi

    suspend fun crearReserva(token: String, request: CreacionReservaRequest): UiState<Reserva> {
        return try {
            val response = api.crearReserva("Bearer $token", request)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) UiState.Success(body)
                else UiState.Error("Error creando reserva")
            } else {
                UiState.Error("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            UiState.Error(e.message ?: "Error de conexión")
        }
    }
}