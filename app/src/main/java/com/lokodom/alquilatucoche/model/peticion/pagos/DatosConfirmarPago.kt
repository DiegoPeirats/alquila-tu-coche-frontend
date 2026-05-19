package com.lokodom.alquilatucoche.model.peticion.pagos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DatosConfirmarPago(
    @SerialName("sessionId") val sessionId: String,
    @SerialName("usuarioId") val usuarioId: Long,
    @SerialName("precio") val precio: Long   // en céntimos o unidades enteras según backend
)