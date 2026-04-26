package com.lokodom.alquilatucoche.network.api

import com.lokodom.alquilatucoche.model.*
import com.lokodom.alquilatucoche.model.entidad.Oferta
import com.lokodom.alquilatucoche.model.peticion.ofertas.ContratarOfertaRequest
import com.lokodom.alquilatucoche.model.peticion.ofertas.OfertasFiltro
import retrofit2.Response
import retrofit2.http.*

interface OfertasApi {

    @POST("ofertas/obtenerOfertas")
    suspend fun getOfertas(
        @Header("Authorization") token: String,
        @Body filtro: OfertasFiltro // o tu DTO real
    ): Response<List<Oferta>>

    @POST("ofertas/crearOferta")
    suspend fun createOferta(
        @Header("Authorization") token: String,
        @Body oferta: Oferta
    ): Response<Unit>

    @PUT("ofertas/modificarOferta")
    suspend fun updateOferta(
        @Header("Authorization") token: String,
        @Body oferta: Oferta
    ): Response<Unit>

    @DELETE("ofertas/eliminarOferta")
    suspend fun deleteOferta(
        @Header("Authorization") token: String,
        @Body request: Long
    ): Response<Unit>

    @POST("ofertas/contratarOferta")
    suspend fun contratarOferta(
        @Header("Authorization") token: String,
        @Body request: ContratarOfertaRequest
    ): Response<Unit>
}