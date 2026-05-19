package com.lokodom.alquilatucoche.model.peticion.vehiculos

import com.lokodom.alquilatucoche.model.entidad.estados.TipoVehiculo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CrearVehiculoRequest(
    @SerialName("tipo")
    val tipo: TipoVehiculo,

    @SerialName("provincia")
    val provincia: String,

    @SerialName("matricula")
    val matricula: String
)