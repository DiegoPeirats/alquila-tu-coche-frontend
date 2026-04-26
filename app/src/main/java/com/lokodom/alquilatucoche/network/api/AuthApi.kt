package com.lokodom.alquilatucoche.network.api

import com.lokodom.alquilatucoche.model.*
import com.lokodom.alquilatucoche.model.peticion.auth.*
import com.lokodom.alquilatucoche.model.respuesta.LoginResponse
import retrofit2.Response
import retrofit2.http.*

interface AuthApi {

    @POST("public/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("public/alta")
    suspend fun register(@Body request: AltaUsuarioRequest): Response<Unit>

    @POST("public/registrarPropietario")
    suspend fun registerPropietario(@Body request: RegistrarPropietarioRequest): Response<Unit>
}