package com.lokodom.alquilatucoche.network.api

import com.lokodom.alquilatucoche.model.entidad.Vehiculo
import com.lokodom.alquilatucoche.model.peticion.vehiculos.CrearVehiculoRequest
import retrofit2.Response
import retrofit2.http.*

interface VehiculosApi {

    @GET("vehiculos/{id}")
    suspend fun getVehiculo(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<Vehiculo>

    @GET("vehiculos")
    suspend fun getVehiculos(
        @Header("Authorization") token: String
    ): Response<List<Vehiculo>>

    @POST("vehiculos/crear")
    suspend fun crearVehiculo(
        @Header("Authorization") token: String,
        @Body request: CrearVehiculoRequest
    ): Response<Vehiculo>

    @DELETE("vehiculos/eliminar/{id}")
    suspend fun eliminarVehiculo(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<Unit>

    @GET("reservas/fechasReservadas/{vehiculoId}")
    suspend fun getFechasReservadas(
        @Header("Authorization") token: String,
        @Path("vehiculoId") vehiculoId: Long
    ): Response<List<String>>   // ["2026-05-10", "2026-05-11", ...]
}