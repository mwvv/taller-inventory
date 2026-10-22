package com.example.gestion_movil.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Convierte una fecha guardada como número (milisegundos) a texto legible
fun formatearFecha(fecha: Long?): String {
    if (fecha == null) return "Sin registro"
    val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "CL"))
    return formato.format(Date(fecha))
}
