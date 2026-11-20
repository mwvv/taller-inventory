package com.example.gestion_movil.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestion_movil.model.UbicacionErrores
import com.example.gestion_movil.model.UbicacionUiState
import com.example.gestion_movil.data.repository.UbicacionRepository
import com.example.gestion_movil.model.data.local.GestionDatabase
import com.example.gestion_movil.model.data.local.UbicacionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class UbicacionViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = UbicacionRepository(GestionDatabase.getDatabase(app).ubicacionDao())

    val ubicaciones: Flow<List<UbicacionEntity>> = repository.ubicaciones

    private val _estado = MutableStateFlow(UbicacionUiState())

    val estado: StateFlow<UbicacionUiState> = _estado

    val estadosUbicacion = listOf("Disponible", "Ocupada", "En reorganización", "Fuera de uso")

    fun nuevo() {
        _estado.value = UbicacionUiState()
    }

    fun cargar(ubicacion: UbicacionEntity) {
        _estado.value = UbicacionUiState(
            id = ubicacion.id,
            codigo = ubicacion.codigo,
            zona = ubicacion.zona,
            estante = ubicacion.estante,
            repisa = ubicacion.repisa,
            posicion = ubicacion.posicion,
            descripcion = ubicacion.descripcion,
            estado = ubicacion.estado
        )
    }

    fun onCodigoChange(valor: String) {
        _estado.update { it.copy(codigo = valor, errores = it.errores.copy(codigo = null)) }
    }

    fun onZonaChange(valor: String) {
        _estado.update { it.copy(zona = valor, errores = it.errores.copy(zona = null)) }
    }

    fun onEstanteChange(valor: String) {
        _estado.update { it.copy(estante = valor, errores = it.errores.copy(estante = null)) }
    }

    fun onRepisaChange(valor: String) {
        _estado.update { it.copy(repisa = valor, errores = it.errores.copy(repisa = null)) }
    }

    fun onPosicionChange(valor: String) {
        _estado.update { it.copy(posicion = valor) }
    }

    fun onDescripcionChange(valor: String) {
        _estado.update { it.copy(descripcion = valor) }
    }

    fun onEstadoChange(valor: String) {
        _estado.update { it.copy(estado = valor) }
    }

    fun validarFormulario(): Boolean {
        val estadoActual = _estado.value

        val errores = UbicacionErrores(
            codigo = if (estadoActual.codigo.isBlank()) "Campo obligatorio" else null,
            zona = if (estadoActual.zona.isBlank()) "Campo obligatorio" else null,
            estante = if (estadoActual.estante.isBlank()) "Campo obligatorio" else null,
            repisa = if (estadoActual.repisa.isBlank()) "Campo obligatorio" else null
        )

        val hayErrores = listOfNotNull(
            errores.codigo,
            errores.zona,
            errores.estante,
            errores.repisa
        ).isNotEmpty()

        _estado.update {
            it.copy(errores = errores)
        }

        return !hayErrores
    }

    fun guardar(onListo: () -> Unit) {
        val a = _estado.value
        viewModelScope.launch {
            val ubicacion = UbicacionEntity(
                id = a.id,
                codigo = a.codigo.trim(),
                zona = a.zona.trim(),
                estante = a.estante.trim(),
                repisa = a.repisa.trim(),
                posicion = a.posicion.trim(),
                descripcion = a.descripcion.trim(),
                estado = a.estado,
                fechaActualizacion = System.currentTimeMillis()
            )
            if (a.id == 0L) repository.insertar(ubicacion) else repository.actualizar(ubicacion)
            onListo()
        }
    }

    fun eliminar(ubicacion: UbicacionEntity) {
        viewModelScope.launch {
            repository.eliminar(ubicacion)
        }
    }

    fun texto(ubicacion: UbicacionEntity?): String {
        if (ubicacion == null) return "Sin ubicación"
        val posicion = if (ubicacion.posicion.isBlank()) "" else ", posición ${ubicacion.posicion}"
        return "${ubicacion.zona}, estante ${ubicacion.estante}, repisa ${ubicacion.repisa}$posicion"
    }

    fun buscar(lista: List<UbicacionEntity>, id: Long?): UbicacionEntity? {
        return lista.find { it.id == id }
    }

}
