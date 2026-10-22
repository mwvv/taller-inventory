package com.example.gestion_movil.data.model

// Perfiles de usuario de la app
enum class Rol(val texto: String) {
    PROPIETARIO("Propietario"),     // Administra todo
    OPERADOR("Operador")            // Permisos limitados (consulta y registra ventas/consumos/ingresos)
}
