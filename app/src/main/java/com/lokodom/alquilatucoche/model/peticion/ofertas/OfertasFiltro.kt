package com.lokodom.alquilatucoche.model.peticion.ofertas

import com.lokodom.alquilatucoche.model.entidad.estados.TipoVehiculo
import kotlinx.serialization.Serializable

@Serializable
data class OfertasFiltro(
    val tipoVehiculo: TipoVehiculo? = null,
    val provincia: String? = null,
    val precioMinimo: Double? = null,
    val precioMaximo: Double? = null
)
