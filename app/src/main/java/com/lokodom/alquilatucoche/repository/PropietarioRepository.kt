package com.lokodom.alquilatucoche.repository

import com.lokodom.alquilatucoche.model.entidad.Propietario
import com.lokodom.alquilatucoche.network.RetrofitClient
import com.lokodom.alquilatucoche.utils.UiState

class PropietarioRepository {

    private val api = RetrofitClient.usuariosApi

    suspend fun getPropietario(token: String, id: Long): UiState<Propietario> {
        return try {
            val response = api.getUsuario("Bearer $token", id)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) UiState.Success(body)
                else UiState.Error("Usuario no encontrado")
            } else {
                UiState.Error("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            UiState.Error(e.message ?: "Error de conexión")
        }
    }
}