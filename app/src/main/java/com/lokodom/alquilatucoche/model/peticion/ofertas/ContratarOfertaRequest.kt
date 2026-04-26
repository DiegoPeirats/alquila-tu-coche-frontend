package com.lokodom.alquilatucoche.model.peticion.ofertas

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContratarOfertaRequest(
    @SerialName("sessionId")
    val sessionId: String,

    @SerialName("datos")
    val datos: DatosPagoOferta,

    @SerialName("reservaId")
    val reservaId: Long
)