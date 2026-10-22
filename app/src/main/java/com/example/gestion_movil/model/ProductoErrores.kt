package com.example.gestion_movil.model

// Modelo que almacena posibles errores individuales del formulario de producto
data class ProductoErrores(
    val codigo: String? = null,
    val nombre: String? = null,
    val categoria: String? = null,
    val cantidad: String? = null,
    val existenciaMinima: String? = null,
    val existenciaObjetivo: String? = null,
    val precioCompra: String? = null,
    val precioVenta: String? = null
)
