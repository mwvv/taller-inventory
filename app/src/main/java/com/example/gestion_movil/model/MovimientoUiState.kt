package com.example.gestion_movil.model

// Modelo principal que representa el estado del formulario de movimiento
data class MovimientoUiState(
    val productoId: Long = 0,
    val tipo: String = "Ingreso",
    val cantidad: String = "",
    val motivo: String = "",
    val observacion: String = "",
    val isLoading: Boolean = false,
    val errores: MovimientoErrores = MovimientoErrores()
)
