package com.lokodom.alquilatucoche.model.respuesta

import com.lokodom.alquilatucoche.model.entidad.Usuario
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    @SerialName("token")
    val token: String,

    @SerialName("usuario")
    val usuario: Usuario? = null,

    @SerialName("message")
    val message: String? = null
)