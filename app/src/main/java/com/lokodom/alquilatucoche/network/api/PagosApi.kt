package com.lokodom.alquilatucoche.network.api

import com.lokodom.alquilatucoche.model.peticion.ofertas.DatosPagoOferta
import com.lokodom.alquilatucoche.model.peticion.pagos.DatosConfirmarPago
import com.lokodom.alquilatucoche.model.respuesta.ConfirmacionPago
import com.lokodom.alquilatucoche.model.respuesta.StripeSessionDetails
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface PagosApi {

    @POST("pagos/recibirPago")
    suspend fun recibirPago(
        @Header("Authorization") token: String,
        @Body datos: DatosPagoOferta
    ): Response<StripeSessionDetails>

    // ← antes devolvía Response<String>, ahora Response<ConfirmacionPago>
    @POST("pagos/confirmarPago")
    suspend fun confirmarPago(
        @Header("Authorization") token: String,
        @Body datos: DatosConfirmarPago
    ): Response<ConfirmacionPago>
}