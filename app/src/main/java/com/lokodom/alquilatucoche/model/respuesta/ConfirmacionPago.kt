package com.lokodom.alquilatucoche.model.respuesta

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConfirmacionPago(
    @SerialName("paymentIntent") val paymentIntent: String,
    @SerialName("pagoId")         val pagoId: Long
)