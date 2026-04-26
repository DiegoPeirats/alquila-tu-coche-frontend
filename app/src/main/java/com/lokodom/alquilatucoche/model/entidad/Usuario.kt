package com.lokodom.alquilatucoche.model.entidad

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Usuario(
    @SerialName("id")
    val id: Long,

    @SerialName("direccion")
    val direccion: Long,

    @SerialName("email")
    val email: String,

    @SerialName("nombre")
    val nombre: String? = null,

    @SerialName("reservas")
    val reservas: List<Reserva> = emptyList(),

    @SerialName("valoraciones")
    val valoraciones: List<Valoracion> = emptyList()
)