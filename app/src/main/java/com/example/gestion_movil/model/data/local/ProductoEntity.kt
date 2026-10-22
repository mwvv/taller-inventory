package com.example.gestion_movil.model.data.local


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productos")
data class ProductoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val codigo: String,                     // Código interno
    val nombre: String,
    val descripcion: String,
    val categoria: String,
    val marca: String,
    val unidadMedida: String,
    val cantidad: Int,                      // Cantidad disponible
    val existenciaMinima: Int,
    val existenciaObjetivo: Int,            // Para calcular la cantidad sugerida de reposición
    val precioCompra: Int,                  // Precio de compra referencial
    val precioVenta: Int,                   // Precio de venta referencial
    val estado: String,                     // Activo / Inactivo / Descontinuado
    val fechaUltimoIngreso: Long? = null,
    val fechaUltimaSalida: Long? = null,
    val proveedorId: Long? = null,          // Proveedor principal
    val ubicacionId: Long? = null,          // Ubicación en la bodega
    val fechaCambioUbicacion: Long? = null
)
