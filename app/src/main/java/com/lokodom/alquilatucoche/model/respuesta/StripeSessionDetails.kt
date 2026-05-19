package com.lokodom.alquilatucoche.model.respuesta

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StripeSessionDetails(
    @SerialName("sessionId") val sessionId: String,
    @SerialName("url") val url: String
)