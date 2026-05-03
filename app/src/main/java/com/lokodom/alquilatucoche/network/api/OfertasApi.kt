package com.lokodom.alquilatucoche.network.api

import com.lokodom.alquilatucoche.model.respuesta.ContratarOfertaResponse
import com.lokodom.alquilatucoche.model.entidad.Oferta
import com.lokodom.alquilatucoche.model.peticion.ofertas.ContratarOfertaRequest
import com.lokodom.alquilatucoche.model.peticion.ofertas.CreacionOfertaRequest
import com.lokodom.alquilatucoche.model.peticion.ofertas.OfertasFiltro
import retrofit2.Response
import retrofit2.http.*

interface OfertasApi {

    @POST("ofertas/obtenerOfertas")
    suspend fun getOfertas(
        @Header("Authorization") token: String,
        @Body filtro: OfertasFiltro // o tu DTO real
    ): Response<List<Oferta>>

    @GET("ofertas/{id}")
    suspend fun getOferta(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<Oferta>

    @POST("ofertas/crear")
    suspend fun crearOferta(
        @Header("Authorization") token: String,
        @Body request: CreacionOfertaRequest
    ): Response<Oferta>

    @DELETE("ofertas/eliminarOferta/{id}")
    suspend fun deleteOferta(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<Unit>

    @POST("ofertas/contratarOferta")
    suspend fun contratarOferta(
        @Header("Authorization") token: String,
        @Body request: ContratarOfertaRequest
    ): Response<ContratarOfertaResponse>
}