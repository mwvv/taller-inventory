package com.example.gestion_movil.model.data.local


import androidx.room.Entity
import androidx.room.PrimaryKey

// Todos los datos del proveedor son ficticios
@Entity(tableName = "proveedores")
data class ProveedorEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val codigo: String,
    val nombre: String,                     // Nombre comercial ficticio
    val contacto: String,
    val correo: String,
    val telefono: String,
    val categorias: String,                 // Productos o categorías que suministra
    val activo: Boolean,
    val observaciones: String
)
