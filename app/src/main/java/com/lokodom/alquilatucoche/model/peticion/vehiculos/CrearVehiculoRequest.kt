package com.lokodom.alquilatucoche.model.peticion.vehiculos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CrearVehiculoRequest(
    @SerialName("tipo")
    val tipo: String,

    @SerialName("provincia")
    val provincia: String,

    @SerialName("matricula")
    val matricula: String
)