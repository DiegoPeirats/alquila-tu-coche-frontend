package com.lokodom.alquilatucoche.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.lokodom.alquilatucoche.network.api.AuthApi
import com.lokodom.alquilatucoche.network.api.OfertasApi
import com.lokodom.alquilatucoche.network.api.PagosApi
import com.lokodom.alquilatucoche.network.api.PropietarioApi
import com.lokodom.alquilatucoche.network.api.ReservasApi
import com.lokodom.alquilatucoche.network.api.UsuariosApi
import com.lokodom.alquilatucoche.network.api.ValoracionesApi
import com.lokodom.alquilatucoche.network.api.VehiculosApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "http://192.168.0.15:8080/api/"

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    // ✅ Servicios separados
    val authApi: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }

    val ofertasApi: OfertasApi by lazy {
        retrofit.create(OfertasApi::class.java)
    }

    val usuariosApi: UsuariosApi by lazy {
        retrofit.create(UsuariosApi::class.java)
    }

    val propietarioApi: PropietarioApi by lazy {
        retrofit.create(PropietarioApi::class.java)
    }

    val vehiculosApi: VehiculosApi by lazy {
        retrofit.create(VehiculosApi::class.java)
    }

    val reservasApi: ReservasApi by lazy {
        retrofit.create(ReservasApi::class.java)
    }

    val valoracionesApi: ValoracionesApi by lazy {
        retrofit.create(ValoracionesApi::class.java)
    }

    val pagosApi: PagosApi = retrofit.create(PagosApi::class.java)
}