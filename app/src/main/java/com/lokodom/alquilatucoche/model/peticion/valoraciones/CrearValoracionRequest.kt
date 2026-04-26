package com.lokodom.alquilatucoche.model.peticion.valoraciones

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CrearValoracionRequest(
    @SerialName("idOferta")
    val idOferta: Long,

    @SerialName("mensaje")
    val mensaje: String,

    @SerialName("valoracion")
    val valoracion: Int
)