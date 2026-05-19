package com.lokodom.alquilatucoche.model.entidad.estados

import kotlinx.serialization.Serializable

@Serializable
enum class TipoVehiculo {
    COCHE, MOTO, FURGONETA, CAMION
}