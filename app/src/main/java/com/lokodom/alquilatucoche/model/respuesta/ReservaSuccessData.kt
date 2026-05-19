package com.lokodom.alquilatucoche.model.respuesta

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReservaSuccessData(
    @SerialName("reservaId") val reservaId: Long,
    @SerialName("totalPagado") val totalPagado: Double,
    @SerialName("fechaInicio") val fechaInicio: String,
    @SerialName("fechaFin") val fechaFin: String,
    @SerialName("dias") val dias: Int
)