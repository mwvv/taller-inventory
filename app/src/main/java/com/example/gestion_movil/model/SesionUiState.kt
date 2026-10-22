package com.example.gestion_movil.model

import com.example.gestion_movil.data.model.Rol

// Estado de la sesión del usuario que ingresó a la app
data class SesionUiState(
    val usuario: String = "",
    val rol: Rol? = null,                               // null = nadie ha iniciado sesión
    val ultimaActividad: Long = 0,                      // Último toque en la pantalla (milisegundos)
    val bloqueadaPorInactividad: Boolean = false,
    val mensaje: String? = null                         // Mensaje para mostrar en el login
)
