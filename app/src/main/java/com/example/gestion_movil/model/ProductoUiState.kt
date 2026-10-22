package com.example.gestion_movil.model

// Modelo principal que representa el estado del formulario y la búsqueda de productos
data class ProductoUiState(
    val id: Long = 0,                           // 0 = producto nuevo
    val codigo: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val categoria: String = "",
    val marca: String = "",
    val unidadMedida: String = "Unidad",
    val cantidad: String = "",                  // Solo se ingresa al crear (después cambia con movimientos)
    val existenciaMinima: String = "",
    val existenciaObjetivo: String = "",
    val precioCompra: String = "",
    val precioVenta: String = "",
    val estado: String = "Activo",
    val proveedorId: Long? = null,
    val ubicacionId: Long? = null,
    val isLoading: Boolean = false,
    val errores: ProductoErrores = ProductoErrores(),

    // Búsqueda y filtros del catálogo
    val busqueda: String = "",
    val filtroCategoria: String? = null,
    val filtroProveedorId: Long? = null,
    val seleccionadoId: Long = 0                // Producto que se ve en el detalle
)
