package com.example.gestion_movil.model.data.local


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movimientos")
data class MovimientoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productoId: Long,
    val productoNombre: String,             // Se guarda para que el historial se lea aunque se elimine el producto
    val tipo: String,                       // Ingreso / Venta / Consumo en servicio / Ajuste / Devolución
    val cantidad: Int,
    val fecha: Long,                        // Fecha y hora
    val motivo: String,
    val observacion: String,
    val existenciaAnterior: Int,
    val existenciaResultante: Int,
    val usuario: String                     // Usuario responsable del registro
)
