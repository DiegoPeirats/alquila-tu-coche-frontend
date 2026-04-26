package com.lokodom.alquilatucoche.model.entidad

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Vehiculo(
    @SerialName("id")
    val id: Long,

    @SerialName("marca")
    val marca: String,

    @SerialName("modelo")
    val modelo: String,

    @SerialName("anio")
    val anio: Int? = null,

    @SerialName("placa")
    val placa: String? = null
)