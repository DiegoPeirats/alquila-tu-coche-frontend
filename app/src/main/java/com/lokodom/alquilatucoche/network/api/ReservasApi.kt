package com.lokodom.alquilatucoche.network.api

import com.lokodom.alquilatucoche.model.*
import com.lokodom.alquilatucoche.model.entidad.Reserva
import retrofit2.Response
import retrofit2.http.*

interface ReservasApi {

    @POST("reservas/crearReserva")
    suspend fun crearReserva(
        @Header("Authorization") token: String,
        @Body request: Reserva
    ): Response<Unit>

    @POST("reservas/obtenerReservas")
    suspend fun obtenerReservas(
        @Header("Authorization") token: String,
        @Body filtro: Any
    ): Response<List<Reserva>>

    @GET("reservas/obtenerReserva/{id}")
    suspend fun obtenerReserva(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<Reserva>
}