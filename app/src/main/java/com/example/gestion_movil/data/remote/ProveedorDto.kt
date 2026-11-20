package com.example.gestion_movil.data.remote

import com.example.gestion_movil.model.data.local.ProveedorEntity

// Proveedor tal como llega desde la API REST (datos ficticios).
// Los nombres de los campos coinciden con las claves del JSON (db.json).
data class ProveedorDto(
    val codigo: String,
    val nombre: String,                 // Nombre comercial
    val contacto: String? = null,
    val correo: String,
    val telefono: String? = null,       // Opcional
    val categorias: String? = null,     // Productos / categorías que suministra
    val activo: Boolean = true,         // Estado
    val observaciones: String? = null
)

// Convierte el DTO a la entidad de Room. Se conserva el id local (0 = nuevo).
fun ProveedorDto.aEntidad(idLocal: Long = 0): ProveedorEntity = ProveedorEntity(
    id = idLocal,
    codigo = codigo.trim(),
    nombre = nombre.trim(),
    contacto = contacto.orEmpty().trim(),
    correo = correo.trim(),
    telefono = telefono.orEmpty().trim(),
    categorias = categorias.orEmpty().trim(),
    activo = activo,
    observaciones = observaciones.orEmpty().trim()
)
