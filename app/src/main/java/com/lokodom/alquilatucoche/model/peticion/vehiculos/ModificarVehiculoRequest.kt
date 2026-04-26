package com.lokodom.alquilatucoche.model.peticion.vehiculos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ModificarVehiculoRequest(
    @SerialName("id")
    val id: Long,

    @SerialName("tipo")
    val tipo: String,

    @SerialName("idPropietario")
    val idPropietario: Long,

    @SerialName("provincia")
    val provincia: String,

    @SerialName("matricula")
    val matricula: String
)