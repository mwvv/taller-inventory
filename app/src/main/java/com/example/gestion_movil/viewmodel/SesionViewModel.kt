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

    private val _estado = MutableStateFlow(SesionUiState())

    val estado: StateFlow<SesionUiState> = _estado

    init {
        viewModelScope.launch {
            while (true) {
                delay(REVISAR_CADA_MS)
                revisarInactividad()
            }
        }
    }


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


    fun registrarActividad() {
        _estado.update { it.copy(ultimaActividad = System.currentTimeMillis()) }
    }

    private fun revisarInactividad() {
        val actual = _estado.value
        if (actual.rol == null) return
        val sinActividad = System.currentTimeMillis() - actual.ultimaActividad
        if (sinActividad >= TIEMPO_INACTIVIDAD_MS) {
            _estado.value = SesionUiState(
                bloqueadaPorInactividad = true,
                mensaje = "Sesión cerrada por inactividad. Ingrese nuevamente."
            )
        }
    }

    fun bloqueoAtendido() {
        _estado.update { it.copy(bloqueadaPorInactividad = false) }
    }

    fun limpiarMensaje() {
        _estado.update { it.copy(mensaje = null) }
    }


    fun esPropietario(): Boolean = _estado.value.rol == Rol.PROPIETARIO

    fun puedeAdministrarProductos(): Boolean = esPropietario()

    fun puedeVerPrecios(): Boolean = esPropietario()

    fun puedeAdministrarUbicaciones(): Boolean = esPropietario()

    fun puedeAdministrarCompras(): Boolean = esPropietario()

    fun tiposMovimientoPermitidos(todos: List<String>): List<String> {
        return if (esPropietario()) todos
        else todos.filter { it == "Ingreso" || it == "Venta" || it == "Consumo en servicio" }
    }

    companion object {
        const val TIEMPO_INACTIVIDAD_MS = 2 * 60 * 1000L
        private const val REVISAR_CADA_MS = 5_000L
    }
}
