package com.example.gestion_movil.model.data.local


import androidx.room.Entity
import androidx.room.PrimaryKey

// Solicitud de reposición a un proveedor
@Entity(tableName = "solicitudes")
data class SolicitudEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val proveedorId: Long,
    val fecha: Long,                        // Fecha de generación
    val estado: String,                     // Pendiente / Preparada / Enviada / Atendida
    val observaciones: String
)
