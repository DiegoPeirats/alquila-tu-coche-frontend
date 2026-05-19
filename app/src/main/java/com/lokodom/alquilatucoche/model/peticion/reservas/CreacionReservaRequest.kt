package com.lokodom.alquilatucoche.model.peticion.reservas

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class CreacionReservaRequest(
    @SerialName("estadoReserva") val estadoReserva: String,
    @SerialName("pagoId")        val pagoId: Long,
    @SerialName("fechaInicio")   val fechaInicio: String,
    @SerialName("fechaFin")      val fechaFin: String,
    @SerialName("ofertaId")      val ofertaId: Long
)