package com.lokodom.alquilatucoche.network.api

import com.lokodom.alquilatucoche.model.entidad.Propietario
import com.lokodom.alquilatucoche.model.entidad.Usuario
import retrofit2.Response
import retrofit2.http.*

interface UsuariosApi {

    @GET("usuarios/{id}")
    suspend fun getUsuario(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<Propietario>

    @GET("usuarios/me")
    suspend fun getMe(
        @Header("Authorization") token: String
    ): Response<Usuario>
}
