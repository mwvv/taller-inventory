package com.example.gestion_movil.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// Cliente Retrofit único para toda la app
object RetrofitClient {

    // API de prueba: se lee el archivo db.json de la raíz del repositorio directamente desde GitHub (raw).
    // Formato: https://raw.githubusercontent.com/<usuario>/<repositorio>/<rama>/
    // Si se usa un fork u otro repositorio, cambiar solo esta URL (debe terminar en "/").
    // Ojo: GitHub cachea el archivo unos minutos; tras un push los cambios pueden tardar en verse.
    private const val BASE_URL = "https://raw.githubusercontent.com/mwvv/taller-inventory/main/"

    // Muestra en Logcat las peticiones y respuestas (útil para la demo)
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    val proveedorApi: ProveedorApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ProveedorApi::class.java)
    }
}
