package com.lokodom.alquilatucoche.model.respuesta

import com.lokodom.alquilatucoche.model.entidad.Oferta
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContratarOfertaResponse(
    @SerialName("oferta")
    val oferta: Oferta,

    @SerialName("resultado")
    val resultado: String,

    val clientSecret: String,
    val publishableKey: String,
    val paymentIntentId: String? = null
)