package com.example.gestion_movil.model

// Modelo que almacena posibles errores del formulario de movimiento
data class MovimientoErrores(
    val cantidad: String? = null,
    val motivo: String? = null
)
