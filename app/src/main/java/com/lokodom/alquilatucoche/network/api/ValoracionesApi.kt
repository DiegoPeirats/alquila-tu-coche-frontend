package com.lokodom.alquilatucoche.network.api

import com.lokodom.alquilatucoche.model.*
import com.lokodom.alquilatucoche.model.entidad.Valoracion
import retrofit2.Response
import retrofit2.http.*

interface ValoracionesApi {

    @POST("valoraciones/crearValoracion")
    suspend fun crearValoracion(
        @Header("Authorization") token: String,
        @Body request: Valoracion
    ): Response<Unit>

    @GET("valoraciones/obtenerValoracion/{id}")
    suspend fun getValoracion(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<Valoracion>
}