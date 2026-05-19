package com.lokodom.alquilatucoche.network.api

import com.lokodom.alquilatucoche.model.respuesta.ContratarOfertaResponse
import com.lokodom.alquilatucoche.model.entidad.Oferta
import com.lokodom.alquilatucoche.model.peticion.ofertas.ContratarOfertaRequest
import com.lokodom.alquilatucoche.model.peticion.ofertas.CreacionOfertaRequest
import com.lokodom.alquilatucoche.model.peticion.ofertas.OfertasFiltro
import retrofit2.Response
import retrofit2.http.*

interface OfertasApi {

    // Todas las ofertas (sin filtro)
    @POST("ofertas/obtenerOfertas")
    suspend fun getOfertas(
        @Header("Authorization") token: String,
        @Body filtro: OfertasFiltro?
    ): Response<List<Oferta>>

    // Ofertas disponibles (filtradas por estado en backend)
    @GET("ofertas/obtenerOfertas")
    suspend fun getOfertasDisponibles(
        @Header("Authorization") token: String
    ): Response<List<Oferta>>

    // Ofertas con filtro de búsqueda
    @POST("ofertas/obtenerOfertas")
    suspend fun getOfertasFiltradas(
        @Header("Authorization") token: String,
        @Body filtro: OfertasFiltro
    ): Response<List<Oferta>>

    @GET("ofertas/{id}")
    suspend fun getOferta(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<Oferta>

    @POST("ofertas/crear")
    suspend fun crearOferta(
        @Header("Authorization") token: String,
        @Body request: CreacionOfertaRequest
    ): Response<Oferta>

    @DELETE("ofertas/eliminarOferta/{id}")
    suspend fun deleteOferta(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<Unit>

    @POST("ofertas/contratar")
    suspend fun contratarOferta(
        @Header("Authorization") token: String,
        @Body request: ContratarOfertaRequest
    ): Response<ContratarOfertaResponse>
}
