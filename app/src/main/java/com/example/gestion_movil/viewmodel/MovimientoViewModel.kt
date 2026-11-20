package com.example.gestion_movil.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestion_movil.model.MovimientoErrores
import com.example.gestion_movil.model.MovimientoUiState
import com.example.gestion_movil.data.repository.MovimientoRepository
import com.example.gestion_movil.data.repository.ProductoRepository
import com.example.gestion_movil.model.data.local.GestionDatabase
import com.example.gestion_movil.model.data.local.MovimientoEntity
import com.example.gestion_movil.utils.NotificacionHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class MovimientoViewModel(app: Application) : AndroidViewModel(app) {

    private val database = GestionDatabase.getDatabase(app)
    private val movimientoRepository = MovimientoRepository(database.movimientoDao())
    private val productoRepository = ProductoRepository(database.productoDao())

    val movimientos: Flow<List<MovimientoEntity>> = movimientoRepository.movimientos

    private val _estado = MutableStateFlow(MovimientoUiState())

    val estado: StateFlow<MovimientoUiState> = _estado

    val tipos = listOf("Ingreso", "Venta", "Consumo en servicio", "Ajuste", "Devolución")

    private val salidas = listOf("Venta", "Consumo en servicio", "Devolución")

    fun iniciar(productoId: Long) {
        _estado.value = MovimientoUiState(productoId = productoId)
    }

    fun onTipoChange(valor: String) {
        _estado.update { it.copy(tipo = valor, errores = MovimientoErrores()) }
    }

    fun onCantidadChange(valor: String) {
        _estado.update { it.copy(cantidad = valor, errores = it.errores.copy(cantidad = null)) }
    }

    fun onMotivoChange(valor: String) {
        _estado.update { it.copy(motivo = valor, errores = it.errores.copy(motivo = null)) }
    }

    fun onObservacionChange(valor: String) {
        _estado.update { it.copy(observacion = valor) }
    }

    fun descripcionTipo(tipo: String): String {
        return when (tipo) {
            "Ingreso" -> "Suma a la existencia"
            "Ajuste" -> "Deja la existencia en la cantidad contada"
            "Devolución" -> "Devolución a proveedor: resta de la existencia"
            else -> "Resta de la existencia"
        }
    }

    fun calcularResultante(tipo: String, existencia: Int, cantidad: Int): Int {
        return when {
            tipo == "Ingreso" -> existencia + cantidad
            tipo == "Ajuste" -> cantidad
            else -> existencia - cantidad
        }
    }

    fun validarFormulario(existenciaActual: Int): Boolean {
        val estadoActual = _estado.value
        val cantidad = estadoActual.cantidad.toIntOrNull()

        val errores = MovimientoErrores(
            cantidad = when {
                cantidad == null -> "Ingrese un número"
                estadoActual.tipo == "Ajuste" && cantidad < 0 -> "No puede ser negativa"
                estadoActual.tipo != "Ajuste" && cantidad <= 0 -> "Debe ser mayor a 0"
                estadoActual.tipo in salidas && cantidad > existenciaActual ->
                    "No hay stock suficiente (disponible: $existenciaActual)"
                else -> null
            },

            motivo = if (estadoActual.tipo == "Ajuste" && estadoActual.motivo.isBlank())
                "Indique el motivo del ajuste"
            else null
        )

        val hayErrores = listOfNotNull(
            errores.cantidad,
            errores.motivo
        ).isNotEmpty()

        _estado.update {
            it.copy(errores = errores)
        }

        return !hayErrores
    }

    fun guardar(usuario: String, onListo: () -> Unit) {
        val a = _estado.value
        _estado.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val producto = productoRepository.obtenerPorId(a.productoId)
            if (producto == null) {
                _estado.update { it.copy(isLoading = false) }
                return@launch
            }

            val cantidad = a.cantidad.toInt()
            val resultante = calcularResultante(a.tipo, producto.cantidad, cantidad)

            if (resultante < 0) {
                _estado.update {
                    it.copy(
                        isLoading = false,
                        errores = it.errores.copy(cantidad = "No hay stock suficiente (disponible: ${producto.cantidad})")
                    )
                }
                return@launch
            }

            val ahora = System.currentTimeMillis()

            movimientoRepository.insertar(
                MovimientoEntity(
                    productoId = producto.id,
                    productoNombre = producto.nombre,
                    tipo = a.tipo,
                    cantidad = cantidad,
                    fecha = ahora,
                    motivo = a.motivo.trim(),
                    observacion = a.observacion.trim(),
                    existenciaAnterior = producto.cantidad,
                    existenciaResultante = resultante,
                    usuario = usuario
                )
            )

            productoRepository.actualizar(
                producto.copy(
                    cantidad = resultante,
                    fechaUltimoIngreso = if (a.tipo == "Ingreso") ahora else producto.fechaUltimoIngreso,
                    fechaUltimaSalida = if (a.tipo in salidas) ahora else producto.fechaUltimaSalida
                )
            )

            if (debeAvisarStockBajo(producto.cantidad, resultante, producto.existenciaMinima, producto.estado)) {
                NotificacionHelper.mostrarStockBajo(
                    getApplication(),
                    producto.id,
                    producto.nombre,
                    resultante,
                    producto.existenciaMinima
                )
            }

            _estado.update { it.copy(isLoading = false) }
            onListo()
        }
    }

    fun debeAvisarStockBajo(anterior: Int, resultante: Int, minimo: Int, estado: String): Boolean {
        if (estado != "Activo") return false
        val cruzoMinimo = anterior > minimo && resultante <= minimo
        val seAgoto = anterior > 0 && resultante == 0
        return cruzoMinimo || seAgoto
    }

    fun filtrarPorProducto(lista: List<MovimientoEntity>, productoId: Long): List<MovimientoEntity> {
        return lista.filter { it.productoId == productoId }
    }

}
