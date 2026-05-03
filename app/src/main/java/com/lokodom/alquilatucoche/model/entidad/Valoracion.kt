package com.lokodom.alquilatucoche.model.entidad

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Valoracion(
    @SerialName("id")
    val id: Long,

    @SerialName("usuarioId")
    val usuarioId: Long,

    @SerialName("ofertaId")
    val ofertaId: Long,

    @SerialName("mensaje")
    val mensaje: String,

    @SerialName("valoracion")
    val valoracion: Int,

    @SerialName("createdAt")
    val createdAt: String
)