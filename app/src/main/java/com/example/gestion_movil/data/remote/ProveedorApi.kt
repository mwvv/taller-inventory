package com.example.gestion_movil.data.remote

import retrofit2.http.GET

// Contenido completo de db.json: { "proveedores": [ ... ] }
data class DbJsonDto(
    val proveedores: List<ProveedorDto> = emptyList()
)

// Endpoints de la API REST de proveedores (integración de prueba, no productiva)
interface ProveedorApi {

    // Descarga db.json desde GitHub (raw) y devuelve la lista de proveedores
    @GET("db.json")
    suspend fun obtenerDb(): DbJsonDto
}

// Atajo para obtener solo la lista de proveedores
suspend fun ProveedorApi.obtenerProveedores(): List<ProveedorDto> = obtenerDb().proveedores
