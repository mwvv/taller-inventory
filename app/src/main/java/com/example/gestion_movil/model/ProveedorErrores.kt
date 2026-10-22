package com.example.gestion_movil.model

// Modelo que almacena posibles errores del formulario de proveedor
data class ProveedorErrores(
    val codigo: String? = null,
    val nombre: String? = null,
    val correo: String? = null
)
