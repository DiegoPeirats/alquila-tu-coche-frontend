package com.lokodom.alquilatucoche.model.entidad

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Propietario(
    @SerialName("id")
    val id: Long,

    @SerialName("direccion")
    val direccion: String,

    @SerialName("email")
    val email: String,

    @SerialName("nombre")
    val nombre: String? = null,

    @SerialName("provincia")
    val provincia: String? = null,

    @SerialName("genero")
    val genero: String? = null,

    @SerialName("numeroTelefono")
    val numeroTelefono: String? = null,

    @SerialName("apellidos")
    val apellidos: String? = null,

    @SerialName("valoracionesRecibidas")
    val valoracionesRecibidas: List<Valoracion> = emptyList(),

    @SerialName("reservas")
    val reservas: List<Reserva> = emptyList(),

    @SerialName("vehiculos")
    val vehiculos: List<Vehiculo> = emptyList()
)