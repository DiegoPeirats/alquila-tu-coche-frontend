package com.lokodom.alquilatucoche.model.peticion.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ModificarUsuarioRequest(
    @SerialName("id")
    val id: Long,

    @SerialName("nombre")
    val nombre: String,

    @SerialName("apellidos")
    val apellidos: String,

    @SerialName("genero")
    val genero: String,

    @SerialName("direccion")
    val direccion: String,

    @SerialName("provincia")
    val provincia: String,

    @SerialName("email")
    val email: String,

    @SerialName("password")
    val password: String,

    @SerialName("numeroTelefono")
    val numeroTelefono: String,

    @SerialName("imagenPerfil")
    val imagenPerfil: ByteArray?
)