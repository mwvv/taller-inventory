package com.example.gestion_movil.model

// Modelo que almacena posibles errores del formulario de ubicación
data class UbicacionErrores(
    val codigo: String? = null,
    val zona: String? = null,
    val estante: String? = null,
    val repisa: String? = null
)
