package com.lokodom.alquilatucoche.network.api

import com.lokodom.alquilatucoche.model.*
import com.lokodom.alquilatucoche.model.entidad.Vehiculo
import retrofit2.Response
import retrofit2.http.*

interface VehiculosApi {

    @POST("vehiculos/alta")
    suspend fun crearVehiculo(
        @Header("Authorization") token: String,
        @Body vehiculo: Vehiculo
    ): Response<Unit>

    @PUT("vehiculos/modificacion")
    suspend fun actualizarVehiculo(
        @Header("Authorization") token: String,
        @Body vehiculo: Vehiculo
    ): Response<Unit>

    @DELETE("vehiculos/baja/{id}")
    suspend fun eliminarVehiculo(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<Unit>

    @GET("vehiculos/vehiculosUsuario/{id}")
    suspend fun getVehiculosUsuario(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<List<Vehiculo>>
}