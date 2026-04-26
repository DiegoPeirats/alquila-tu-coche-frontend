package com.lokodom.alquilatucoche.model.entidad

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Pago(
    @SerialName("id")
    val id: Long,

    @SerialName("importe")
    val importe: Double,

    @SerialName("idUsuario")
    val idUsuario: String,

)