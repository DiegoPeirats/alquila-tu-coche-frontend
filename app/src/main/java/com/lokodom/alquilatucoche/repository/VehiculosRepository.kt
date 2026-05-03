package com.lokodom.alquilatucoche.repository

import com.lokodom.alquilatucoche.model.entidad.Vehiculo
import com.lokodom.alquilatucoche.network.RetrofitClient
import com.lokodom.alquilatucoche.utils.UiState

class VehiculosRepository {
    private val api = RetrofitClient.vehiculosApi

    suspend fun getVehiculo(token: String, id: Long): UiState<Vehiculo> {
        return try {
            val response = api.getVehiculo("Bearer $token", id)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) UiState.Success(body)
                else UiState.Error("Vehículo no encontrado")
            } else {
                UiState.Error("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            UiState.Error(e.message ?: "Error de conexión")
        }
    }
}