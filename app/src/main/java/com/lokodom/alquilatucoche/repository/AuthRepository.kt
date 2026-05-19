package com.lokodom.alquilatucoche.repository

import com.lokodom.alquilatucoche.model.entidad.Propietario
import com.lokodom.alquilatucoche.model.entidad.Usuario
import com.lokodom.alquilatucoche.model.peticion.auth.AltaUsuarioRequest
import com.lokodom.alquilatucoche.model.peticion.auth.LoginRequest
import com.lokodom.alquilatucoche.model.respuesta.LoginResponse
import com.lokodom.alquilatucoche.network.RetrofitClient
import com.lokodom.alquilatucoche.utils.UiState

class AuthRepository {
    private val api = RetrofitClient.authApi

    suspend fun login(email: String, password: String): UiState<LoginResponse> {
        return try {
            val response = api.login(LoginRequest(email, password))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) UiState.Success(body)
                else UiState.Error("Respuesta vacía del servidor")
            } else {
                UiState.Error("Credenciales incorrectas (${response.code()})")
            }
        } catch (e: Exception) {
            UiState.Error(e.message ?: "Error de conexión")
        }
    }

    suspend fun registro(request: AltaUsuarioRequest): UiState<Usuario> {
        return try {
            val response = api.registro(request)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) UiState.Success(body)
                else UiState.Error("Error en el registro")
            } else {
                UiState.Error("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            UiState.Error(e.message ?: "Error de conexión")
        }
    }
}
