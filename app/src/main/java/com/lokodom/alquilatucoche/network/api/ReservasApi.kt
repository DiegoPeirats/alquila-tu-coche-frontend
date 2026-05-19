package com.lokodom.alquilatucoche.network.api

import com.lokodom.alquilatucoche.model.entidad.Reserva
import com.lokodom.alquilatucoche.model.peticion.reservas.CreacionReservaRequest
import com.lokodom.alquilatucoche.model.peticion.reservas.ModificarReservaRequest
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

    @GET("reservas/usuario")
    suspend fun getReservasUsuario(
        @Header("Authorization") token: String
    ): Response<List<Reserva>>

    @PUT("reservas/modificar/{id}")
    suspend fun modificarReserva(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Body request: ModificarReservaRequest
    ): Response<Reserva>

    @DELETE("reservas/cancelar/{id}")
    suspend fun cancelarReserva(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<Unit>
}