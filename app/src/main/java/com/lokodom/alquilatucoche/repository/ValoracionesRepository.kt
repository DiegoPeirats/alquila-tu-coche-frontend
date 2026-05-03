package com.lokodom.alquilatucoche.repository

import com.lokodom.alquilatucoche.model.entidad.Valoracion
import com.lokodom.alquilatucoche.model.peticion.valoraciones.CrearValoracionRequest
import com.lokodom.alquilatucoche.network.RetrofitClient
import com.lokodom.alquilatucoche.utils.UiState

class ValoracionesRepository {
    private val api = RetrofitClient.valoracionesApi

    suspend fun crearValoracion(token: String, request: CrearValoracionRequest): UiState<Valoracion> {
        return try {
            val response = api.crearValoracion("Bearer $token", request)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) UiState.Success(body)
                else UiState.Error("Error creando valoración")
            } else {
                UiState.Error("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            UiState.Error(e.message ?: "Error de conexión")
        }
    }
}
