package com.example.gestion_movil.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestion_movil.data.model.Credential
import com.example.gestion_movil.data.model.Rol
import com.example.gestion_movil.model.SesionUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class SesionViewModel : ViewModel() {

    // Estado interno mutable
    private val _estado = MutableStateFlow(SesionUiState())

    // Estado expuesto para la UI
    val estado: StateFlow<SesionUiState> = _estado

    init {
        // Revisa cada pocos segundos si pasó el tiempo máximo sin actividad
        viewModelScope.launch {
            while (true) {
                delay(REVISAR_CADA_MS)
                revisarInactividad()
            }
        }
    }

    // ---------- Inicio y cierre de sesión ----------

    fun iniciar(cuenta: Credential) {
        _estado.value = SesionUiState(
            usuario = cuenta.username,
            rol = cuenta.rol,
            ultimaActividad = System.currentTimeMillis()
        )
    }

    fun cerrar() {
        _estado.value = SesionUiState()
    }

    // ---------- Bloqueo por inactividad ----------

    // Se llama cada vez que el usuario toca la pantalla (desde MainActivity)
    fun registrarActividad() {
        _estado.update { it.copy(ultimaActividad = System.currentTimeMillis()) }
    }

    private fun revisarInactividad() {
        val actual = _estado.value
        if (actual.rol == null) return      // No hay sesión abierta
        val sinActividad = System.currentTimeMillis() - actual.ultimaActividad
        if (sinActividad >= TIEMPO_INACTIVIDAD_MS) {
            _estado.value = SesionUiState(
                bloqueadaPorInactividad = true,
                mensaje = "Sesión cerrada por inactividad. Ingrese nuevamente."
            )
        }
    }

    // La navegación ya volvió al login: se apaga la marca de bloqueo (el mensaje se mantiene)
    fun bloqueoAtendido() {
        _estado.update { it.copy(bloqueadaPorInactividad = false) }
    }

    fun limpiarMensaje() {
        _estado.update { it.copy(mensaje = null) }
    }

    // ---------- Permisos según el perfil ----------

    fun esPropietario(): Boolean = _estado.value.rol == Rol.PROPIETARIO

    // Registrar, modificar y eliminar productos (y su existencia mínima)
    fun puedeAdministrarProductos(): Boolean = esPropietario()

    // Precios de compra y venta (información sensible)
    fun puedeVerPrecios(): Boolean = esPropietario()

    // Crear, modificar y eliminar ubicaciones (el operador solo las consulta)
    fun puedeAdministrarUbicaciones(): Boolean = esPropietario()

    // Proveedores, reposición, correo e indicadores
    fun puedeAdministrarCompras(): Boolean = esPropietario()

    // Tipos de movimiento que puede registrar cada perfil
    fun tiposMovimientoPermitidos(todos: List<String>): List<String> {
        return if (esPropietario()) todos
        else todos.filter { it == "Ingreso" || it == "Venta" || it == "Consumo en servicio" }
    }

    companion object {
        // Tiempo máximo sin tocar la pantalla antes de cerrar la sesión (2 minutos)
        const val TIEMPO_INACTIVIDAD_MS = 2 * 60 * 1000L
        private const val REVISAR_CADA_MS = 5_000L
    }
}
