package com.lokodom.alquilatucoche.model.peticion.ofertas

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ModificarOfertaRequest(
    @SerialName("precioPorDia") val precioPorDia: Double,
    @SerialName("estado") val estado: String
)