package com.lokodom.alquilatucoche.repository

import com.lokodom.alquilatucoche.model.entidad.Reserva
import com.lokodom.alquilatucoche.model.peticion.reservas.CreacionReservaRequest
import com.lokodom.alquilatucoche.model.peticion.reservas.ModificarReservaRequest
import com.lokodom.alquilatucoche.network.RetrofitClient
import com.lokodom.alquilatucoche.utils.UiState

class ReservasRepository {
    private val api = RetrofitClient.reservasApi

    suspend fun crearReserva(token: String, request: CreacionReservaRequest): UiState<Reserva> {
        return safeCall { api.crearReserva("Bearer $token", request) }
    }

    suspend fun getReservasUsuario(token: String): UiState<List<Reserva>> {
        return safeCall { api.getReservasUsuario("Bearer $token") }
    }

    suspend fun modificarReserva(
        token: String,
        id: Long,
        request: ModificarReservaRequest
    ): UiState<Reserva> {
        return safeCall { api.modificarReserva("Bearer $token", id, request) }
    }

    suspend fun cancelarReserva(token: String, id: Long): UiState<Unit> {
        return try {
            val response = api.cancelarReserva("Bearer $token", id)
            if (response.isSuccessful) UiState.Success(Unit)
            else UiState.Error("Error ${response.code()}: ${response.message()}")
        } catch (e: Exception) {
            UiState.Error(e.message ?: "Error de conexión")
        }
    }

    private suspend fun <T> safeCall(call: suspend () -> retrofit2.Response<T>): UiState<T> {
        return try {
            val response = call()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) UiState.Success(body)
                else UiState.Error("Respuesta vacía")
            } else {
                UiState.Error("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            UiState.Error(e.message ?: "Error de conexión")
        }
    }
}
