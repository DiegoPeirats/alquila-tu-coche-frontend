package com.lokodom.alquilatucoche.model.peticion.reservas

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ModificarReservaRequest(
    @SerialName("fechaInicio") val fechaInicio: String,
    @SerialName("fechaFin") val fechaFin: String
)