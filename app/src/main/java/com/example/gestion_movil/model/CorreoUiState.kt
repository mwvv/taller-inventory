package com.example.gestion_movil.model

// Modelo que representa el borrador de correo para un proveedor
data class CorreoUiState(
    val proveedorId: Long = 0,
    val destinatario: String = "",
    val asunto: String = "",
    val cuerpo: String = "",
    val error: String? = null
)
