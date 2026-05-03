package com.lokodom.alquilatucoche.network.api

import com.lokodom.alquilatucoche.model.entidad.Reserva
import com.lokodom.alquilatucoche.model.peticion.reservas.CreacionReservaRequest
import retrofit2.Response
import retrofit2.http.*

interface ReservasApi {

    @POST("reservas/crear")
    suspend fun crearReserva(
        @Header("Authorization") token: String,
        @Body request: CreacionReservaRequest
    ): Response<Reserva>

    @GET("reservas/{id}")
    suspend fun getReserva(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<Reserva>

    @GET("reservas/usuario/{usuarioId}")
    suspend fun getReservasUsuario(
        @Header("Authorization") token: String,
        @Path("usuarioId") usuarioId: Long
    ): Response<List<Reserva>>
}