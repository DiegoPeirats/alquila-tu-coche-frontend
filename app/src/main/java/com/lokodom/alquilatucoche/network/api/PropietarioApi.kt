package com.lokodom.alquilatucoche.network.api
import com.lokodom.alquilatucoche.model.entidad.Oferta
import com.lokodom.alquilatucoche.model.entidad.Propietario
import com.lokodom.alquilatucoche.model.entidad.Vehiculo
import com.lokodom.alquilatucoche.model.peticion.auth.RegistrarPropietarioRequest
import com.lokodom.alquilatucoche.model.peticion.ofertas.CreacionOfertaRequest
import com.lokodom.alquilatucoche.model.peticion.ofertas.ModificarOfertaRequest
import com.lokodom.alquilatucoche.model.peticion.vehiculos.CrearVehiculoRequest
import retrofit2.Response
import retrofit2.http.*
interface PropietarioApi {

    @GET("usuarios/{id}")
    suspend fun getPropietario(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<Propietario>

    @GET("usuarios/me")
    suspend fun getMiPerfil(
        @Header("Authorization") token: String
    ): Response<Propietario>

    @POST("usuarios/registrar-propietario")
    suspend fun registrarComoPropietario(
        @Header("Authorization") token: String,
        @Body request: RegistrarPropietarioRequest
    ): Response<Propietario>

    // Vehículos
    @POST("vehiculos/crear")
    suspend fun crearVehiculo(
        @Header("Authorization") token: String,
        @Body request: CrearVehiculoRequest
    ): Response<Vehiculo>

    // Ofertas propias del propietario
    @GET("ofertas/mis-ofertas")
    suspend fun getMisOfertas(
        @Header("Authorization") token: String
    ): Response<List<Oferta>>

    @POST("ofertas/crear")
    suspend fun crearOferta(
        @Header("Authorization") token: String,
        @Body request: CreacionOfertaRequest
    ): Response<Oferta>

    @PUT("ofertas/modificar/{id}")
    suspend fun modificarOferta(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Body request: ModificarOfertaRequest
    ): Response<Oferta>

    @DELETE("ofertas/eliminarOferta/{id}")
    suspend fun eliminarOferta(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<Unit>
}
