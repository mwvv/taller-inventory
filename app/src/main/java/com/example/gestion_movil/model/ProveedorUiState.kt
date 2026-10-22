package com.example.gestion_movil.model

// Modelo principal que representa el estado del formulario de proveedor
data class ProveedorUiState(
    val id: Long = 0,                           // 0 = proveedor nuevo
    val codigo: String = "",
    val nombre: String = "",
    val contacto: String = "",
    val correo: String = "",
    val telefono: String = "",
    val categorias: String = "",
    val activo: Boolean = true,
    val observaciones: String = "",
    val errores: ProveedorErrores = ProveedorErrores()
)
