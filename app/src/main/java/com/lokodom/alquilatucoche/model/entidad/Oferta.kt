package com.lokodom.alquilatucoche.model.entidad

import com.lokodom.alquilatucoche.model.entidad.Vehiculo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Oferta(
    @SerialName("id")
    val id: Long,

    @SerialName("idVehiculo")
    val idVehiculo: Long,

    @SerialName("estado")
    val estado: String,

    @SerialName("reservas")
    val reservas: List<Reserva> = emptyList(),

    @SerialName("valoraciones")
    val valoraciones: List<Valoracion> = emptyList()
)