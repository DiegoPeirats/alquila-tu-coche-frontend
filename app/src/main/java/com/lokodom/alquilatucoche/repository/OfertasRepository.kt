package com.lokodom.alquilatucoche.repository

import com.lokodom.alquilatucoche.model.entidad.Oferta
import com.lokodom.alquilatucoche.model.peticion.ofertas.OfertasFiltro
import com.lokodom.alquilatucoche.network.RetrofitClient
import com.lokodom.alquilatucoche.utils.UiState

class OfertasRepository {
    private val api = RetrofitClient.ofertasApi

    suspend fun getOfertas(token: String): UiState<List<Oferta>> {
        return try {
            val response = api.getOfertas("Bearer $token", OfertasFiltro())
            if (response.isSuccessful) {
                UiState.Success(response.body() ?: emptyList())
            } else {
                UiState.Error("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            UiState.Error(e.message ?: "Error al cargar ofertas")
        }
    }

    suspend fun deleteOferta(token: String, id: Long): UiState<Unit> {
        return try {
            val response = api.deleteOferta("Bearer $token", id)
            if (response.isSuccessful) UiState.Success(Unit)
            else UiState.Error("Error ${response.code()}: ${response.message()}")
        } catch (e: Exception) {
            UiState.Error(e.message ?: "Error al eliminar")
        }
    }
}