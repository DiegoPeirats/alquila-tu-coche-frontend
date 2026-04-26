package com.lokodom.alquilatucoche.model.peticion.ofertas

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DatosPagoOferta(
    @SerialName("ofertaId")
    val ofertaId: Long,

    @SerialName("diasContratados")
    val diasContratados: Int,

    @SerialName("usuarioId")
    val usuarioId: Long
)