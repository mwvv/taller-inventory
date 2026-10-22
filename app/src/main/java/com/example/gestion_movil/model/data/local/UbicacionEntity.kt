package com.example.gestion_movil.model.data.local


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ubicaciones")
data class UbicacionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val codigo: String,
    val zona: String,                       // Zona o sector (ej: Bodega A)
    val estante: String,
    val repisa: String,
    val posicion: String,                   // Contenedor o posición (ej: izquierda)
    val descripcion: String,
    val estado: String,                     // Disponible / Ocupada / En reorganización / Fuera de uso
    val fechaActualizacion: Long
)
