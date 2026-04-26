package com.lokodom.alquilatucoche.model.peticion.ofertas

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreacionOfertaRequest(
    @SerialName("precioPorDia")
    val precioPorDia: Double,

    @SerialName("idVehiculo")
    val idVehiculo: Long
)