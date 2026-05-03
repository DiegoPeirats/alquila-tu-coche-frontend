package com.lokodom.alquilatucoche.repository

import com.lokodom.alquilatucoche.model.entidad.Propietario
import com.lokodom.alquilatucoche.model.entidad.Usuario
import com.lokodom.alquilatucoche.network.RetrofitClient
import com.lokodom.alquilatucoche.utils.UiState

class UsuariosRepository {
    private val api = RetrofitClient.usuariosApi

    suspend fun getMe(token: String): UiState<Usuario> {
        return try {
            val response = api.getMe("Bearer $token")
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) UiState.Success(body)
                else UiState.Error("No se pudo obtener el usuario actual")
            } else {
                UiState.Error("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            UiState.Error(e.message ?: "Error de conexión")
        }
    }
}