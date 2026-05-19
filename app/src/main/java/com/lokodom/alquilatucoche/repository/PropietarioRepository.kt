package com.lokodom.alquilatucoche.repository

import com.lokodom.alquilatucoche.model.entidad.Oferta
import com.lokodom.alquilatucoche.model.entidad.Propietario
import com.lokodom.alquilatucoche.model.entidad.Vehiculo
import com.lokodom.alquilatucoche.model.peticion.auth.RegistrarPropietarioRequest
import com.lokodom.alquilatucoche.model.peticion.ofertas.CreacionOfertaRequest
import com.lokodom.alquilatucoche.model.peticion.ofertas.ModificarOfertaRequest
import com.lokodom.alquilatucoche.model.peticion.vehiculos.CrearVehiculoRequest
import com.lokodom.alquilatucoche.network.RetrofitClient
import com.lokodom.alquilatucoche.utils.UiState

class PropietarioRepository {
    private val api = RetrofitClient.propietarioApi

    suspend fun getPropietario(token: String, id: Long): UiState<Propietario> {
        return safeCall { api.getPropietario("Bearer $token", id) }
    }

    suspend fun getMiPerfil(token: String): UiState<Propietario> {
        return safeCall { api.getMiPerfil("Bearer $token") }
    }

    suspend fun registrarComoPropietario(
        token: String,
        imagenContrato: ByteArray
    ): UiState<Propietario> {
        return safeCall {
            api.registrarComoPropietario(
                "Bearer $token",
                RegistrarPropietarioRequest(imagenContrato)
            )
        }
    }

    suspend fun crearVehiculo(token: String, request: CrearVehiculoRequest): UiState<Vehiculo> {
        return safeCall { api.crearVehiculo("Bearer $token", request) }
    }

    suspend fun getMisOfertas(token: String): UiState<List<Oferta>> {
        return safeCall { api.getMisOfertas("Bearer $token") }
    }

    suspend fun crearOferta(token: String, request: CreacionOfertaRequest): UiState<Oferta> {
        return safeCall { api.crearOferta("Bearer $token", request) }
    }

    suspend fun modificarOferta(
        token: String,
        id: Long,
        request: ModificarOfertaRequest
    ): UiState<Oferta> {
        return safeCall { api.modificarOferta("Bearer $token", id, request) }
    }

    suspend fun eliminarOferta(token: String, id: Long): UiState<Unit> {
        return safeCall { api.eliminarOferta("Bearer $token", id) }
    }

    // Helper genérico para evitar repetir try/catch en cada función
    private suspend fun <T> safeCall(call: suspend () -> retrofit2.Response<T>): UiState<T> {
        return try {
            val response = call()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) UiState.Success(body)
                else UiState.Error("Respuesta vacía del servidor")
            } else {
                UiState.Error("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            UiState.Error(e.message ?: "Error de conexión")
        }
    }
}