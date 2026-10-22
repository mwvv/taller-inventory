package com.example.gestion_movil.model

// Modelo principal que representa el estado del formulario de ubicación
data class UbicacionUiState(
    val id: Long = 0,                           // 0 = ubicación nueva
    val codigo: String = "",
    val zona: String = "",
    val estante: String = "",
    val repisa: String = "",
    val posicion: String = "",
    val descripcion: String = "",
    val estado: String = "Disponible",
    val errores: UbicacionErrores = UbicacionErrores()
)
