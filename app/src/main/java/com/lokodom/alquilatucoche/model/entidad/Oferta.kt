package com.lokodom.alquilatucoche.model.entidad

import com.lokodom.alquilatucoche.model.entidad.Vehiculo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Oferta(

    val id: Long? = null,

    val estado: String,

    val precioPorDia: Double,

    val idVehiculo: Long? = null,

    val reservas: List<Reserva> = emptyList()
)