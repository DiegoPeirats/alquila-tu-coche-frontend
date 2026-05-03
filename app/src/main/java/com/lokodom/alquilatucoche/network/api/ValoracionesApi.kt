package com.lokodom.alquilatucoche.network.api

import com.lokodom.alquilatucoche.model.entidad.Valoracion
import com.lokodom.alquilatucoche.model.peticion.valoraciones.CrearValoracionRequest
import retrofit2.Response
import retrofit2.http.*

interface ValoracionesApi {

    @POST("valoraciones/crear")
    suspend fun crearValoracion(
        @Header("Authorization") token: String,
        @Body request: CrearValoracionRequest
    ): Response<Valoracion>

    @GET("valoraciones/oferta/{ofertaId}")
    suspend fun getValoracionesOferta(
        @Header("Authorization") token: String,
        @Path("ofertaId") ofertaId: Long
    ): Response<List<Valoracion>>
}