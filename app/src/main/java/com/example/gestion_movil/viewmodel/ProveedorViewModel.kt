package com.example.gestion_movil.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestion_movil.model.ProveedorErrores
import com.example.gestion_movil.model.ProveedorUiState
import com.example.gestion_movil.data.repository.ProveedorRepository
import com.example.gestion_movil.model.data.local.GestionDatabase
import com.example.gestion_movil.model.data.local.ProveedorEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException


class ProveedorViewModel(app: Application) : AndroidViewModel(app) {

    // El ViewModel trabaja con el repositorio, no con el DAO directamente
    private val repository = ProveedorRepository(GestionDatabase.getDatabase(app).proveedorDao())

    // Proveedores guardados en Room
    val proveedores: Flow<List<ProveedorEntity>> = repository.proveedores

    // Estado interno mutable
    private val _estado = MutableStateFlow(ProveedorUiState())

    // Estado expuesto para la UI
    val estado: StateFlow<ProveedorUiState> = _estado

    // ---------- API REST ----------

    // true mientras se consulta la API (para mostrar el indicador de carga)
    private val _sincronizando = MutableStateFlow(false)
    val sincronizando: StateFlow<Boolean> = _sincronizando

    // Mensaje con el resultado de la última sincronización (null = nada que mostrar)
    private val _mensajeApi = MutableStateFlow<String?>(null)
    val mensajeApi: StateFlow<String?> = _mensajeApi

    // Consulta la API REST de proveedores y actualiza Room
    fun sincronizarDesdeApi() {
        if (_sincronizando.value) return
        viewModelScope.launch {
            _sincronizando.value = true
            _mensajeApi.value = try {
                val r = repository.sincronizarDesdeApi()
                "API: ${r.nuevos} nuevos, ${r.actualizados} actualizados"
            } catch (e: IOException) {
                "Sin conexión: se mantienen los proveedores locales"
            } catch (e: Exception) {
                "No se pudo consultar la API (${e.message})"
            } finally {
                _sincronizando.value = false
            }
        }
    }

    fun mensajeApiMostrado() {
        _mensajeApi.value = null
    }

    fun nuevo() {
        _estado.value = ProveedorUiState()
    }

    fun cargar(proveedor: ProveedorEntity) {
        _estado.value = ProveedorUiState(
            id = proveedor.id,
            codigo = proveedor.codigo,
            nombre = proveedor.nombre,
            contacto = proveedor.contacto,
            correo = proveedor.correo,
            telefono = proveedor.telefono,
            categorias = proveedor.categorias,
            activo = proveedor.activo,
            observaciones = proveedor.observaciones
        )
    }

    fun onCodigoChange(valor: String) {
        _estado.update { it.copy(codigo = valor, errores = it.errores.copy(codigo = null)) }
    }

    fun onNombreChange(valor: String) {
        _estado.update { it.copy(nombre = valor, errores = it.errores.copy(nombre = null)) }
    }

    fun onContactoChange(valor: String) {
        _estado.update { it.copy(contacto = valor) }
    }

    fun onCorreoChange(valor: String) {
        _estado.update { it.copy(correo = valor, errores = it.errores.copy(correo = null)) }
    }

    fun onTelefonoChange(valor: String) {
        _estado.update { it.copy(telefono = valor) }
    }

    fun onCategoriasChange(valor: String) {
        _estado.update { it.copy(categorias = valor) }
    }

    fun onActivoChange(valor: Boolean) {
        _estado.update { it.copy(activo = valor) }
    }

    fun onObservacionesChange(valor: String) {
        _estado.update { it.copy(observaciones = valor) }
    }

    // Validación global del formulario
    fun validarFormulario(): Boolean {
        val estadoActual = _estado.value

        val errores = ProveedorErrores(
            codigo = if (estadoActual.codigo.isBlank())
                "Campo obligatorio"
            else null,

            nombre = if (estadoActual.nombre.isBlank())
                "Campo obligatorio"
            else null,

            correo = if (!estadoActual.correo.contains("@"))
                "Correo inválido"
            else null
        )

        val hayErrores = listOfNotNull(
            errores.codigo,
            errores.nombre,
            errores.correo
        ).isNotEmpty()

        _estado.update {
            it.copy(errores = errores)
        }

        return !hayErrores
    }

    // Guarda en Room (inserta si es nuevo, actualiza si ya existe)
    fun guardar(onListo: () -> Unit) {
        val a = _estado.value
        viewModelScope.launch {
            val proveedor = ProveedorEntity(
                id = a.id,
                codigo = a.codigo.trim(),
                nombre = a.nombre.trim(),
                contacto = a.contacto.trim(),
                correo = a.correo.trim(),
                telefono = a.telefono.trim(),
                categorias = a.categorias.trim(),
                activo = a.activo,
                observaciones = a.observaciones.trim()
            )
            if (a.id == 0L) repository.insertar(proveedor) else repository.actualizar(proveedor)
            onListo()
        }
    }

    fun eliminar(proveedor: ProveedorEntity) {
        viewModelScope.launch {
            repository.eliminar(proveedor)
        }
    }

    // Nombre del proveedor de un producto
    fun nombre(lista: List<ProveedorEntity>, id: Long?): String {
        return lista.find { it.id == id }?.nombre ?: "Sin proveedor"
    }

}
