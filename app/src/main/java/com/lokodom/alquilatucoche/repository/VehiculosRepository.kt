package com.lokodom.alquilatucoche.repository

import com.lokodom.alquilatucoche.model.entidad.Vehiculo
import com.lokodom.alquilatucoche.network.RetrofitClient
import com.lokodom.alquilatucoche.utils.UiState

class VehiculosRepository {

    private val api = RetrofitClient.vehiculosApi

    suspend fun getVehiculo(token: String, id: Long): UiState<Vehiculo> {
        return try {
            val r = api.getVehiculo("Bearer $token", id)
            if (r.isSuccessful) {
                val body = r.body()
                if (body != null) UiState.Success(body) else UiState.Error("Vehículo no encontrado")
            } else UiState.Error("Error ${r.code()}: ${r.message()}")
        } catch (e: Exception) {
            UiState.Error(e.message ?: "Error de conexión")
        }
    }

    // Devuelve lista de strings ISO "YYYY-MM-DD"
    suspend fun getFechasReservadas(token: String, vehiculoId: Long): UiState<List<String>> {
        return try {
            val r = api.getFechasReservadas("Bearer $token", vehiculoId)
            if (r.isSuccessful) UiState.Success(r.body() ?: emptyList())
            else UiState.Error("Error ${r.code()}: ${r.message()}")
        } catch (e: Exception) {
            UiState.Error(e.message ?: "Error de conexión")
        }
    }
}