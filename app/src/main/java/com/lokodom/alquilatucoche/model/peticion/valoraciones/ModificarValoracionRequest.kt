package com.lokodom.alquilatucoche.model.peticion.valoraciones

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ModificarValoracionRequest(
    @SerialName("id")
    val id: Long,

    @SerialName("mensaje")
    val mensaje: String,

    @SerialName("valoracion")
    val valoracion: Int
)