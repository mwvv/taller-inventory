package com.example.gestion_movil.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

// Endpoints de la API REST de proveedores (integración de prueba, no productiva)
interface ProveedorApi {

    // Lista todos los proveedores
    @GET("proveedores")
    suspend fun obtenerProveedores(): List<ProveedorDto>

    // Un proveedor por su id remoto
    @GET("proveedores/{id}")
    suspend fun obtenerProveedor(@Path("id") id: Int): ProveedorDto
}
