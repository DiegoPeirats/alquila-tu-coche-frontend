package com.lokodom.alquilatucoche.model.peticion.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegistrarPropietarioRequest(
    @SerialName("imagenContrato")
    val imagenContrato: ByteArray
)