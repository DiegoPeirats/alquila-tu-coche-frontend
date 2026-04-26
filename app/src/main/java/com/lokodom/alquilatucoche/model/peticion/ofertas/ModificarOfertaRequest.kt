package com.lokodom.alquilatucoche.model.peticion.ofertas

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ModificarOfertaRequest(
    @SerialName("id")
    val id: Long,

    @SerialName("precioPorDia")
    val precioPorDia: Double
)