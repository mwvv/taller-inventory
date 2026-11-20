package com.example.gestion_movil.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestion_movil.model.ProductoErrores
import com.example.gestion_movil.model.ProductoUiState
import com.example.gestion_movil.data.repository.ProductoRepository
import com.example.gestion_movil.data.repository.ProveedorRepository
import com.example.gestion_movil.data.repository.UbicacionRepository
import com.example.gestion_movil.model.data.local.DatosIniciales
import com.example.gestion_movil.model.data.local.GestionDatabase
import com.example.gestion_movil.model.data.local.ProductoEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class ProductoViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = ProductoRepository(GestionDatabase.getDatabase(app).productoDao())
    private val proveedorRepository = ProveedorRepository(GestionDatabase.getDatabase(app).proveedorDao())
    private val ubicacionRepository = UbicacionRepository(GestionDatabase.getDatabase(app).ubicacionDao())

    val productos: Flow<List<ProductoEntity>> = repository.productos

    private val _estado = MutableStateFlow(ProductoUiState())

    val estado: StateFlow<ProductoUiState> = _estado

    val estadosProducto = listOf("Activo", "Inactivo", "Descontinuado")

    init {
        viewModelScope.launch {
            cargarDatosIniciales()
        }
    }

    private suspend fun cargarDatosIniciales() {
        val baseVacia = repository.contar() == 0 &&
                proveedorRepository.contar() == 0 &&
                ubicacionRepository.contar() == 0
        if (!baseVacia) return

        val ahora = System.currentTimeMillis()
        DatosIniciales.proveedores().forEach { proveedorRepository.insertar(it) }
        DatosIniciales.ubicaciones(ahora).forEach { ubicacionRepository.insertar(it) }
        DatosIniciales.productos(ahora).forEach { repository.insertar(it) }
        val movimientoDao = GestionDatabase.getDatabase(getApplication()).movimientoDao()
        DatosIniciales.movimientos(ahora).forEach { movimientoDao.insertar(it) }
    }


    fun nuevo() {
        _estado.update {
            it.copy(
                id = 0, codigo = "", nombre = "", descripcion = "", categoria = "",
                marca = "", unidadMedida = "Unidad", cantidad = "", existenciaMinima = "",
                existenciaObjetivo = "", precioCompra = "", precioVenta = "", estado = "Activo",
                proveedorId = null, ubicacionId = null, isLoading = false,
                errores = ProductoErrores()
            )
        }
    }

    fun nuevoConCodigo(codigo: String) {
        nuevo()
        _estado.update { it.copy(codigo = codigo.trim()) }
    }

    fun cargar(producto: ProductoEntity) {
        _estado.update {
            it.copy(
                id = producto.id,
                codigo = producto.codigo,
                nombre = producto.nombre,
                descripcion = producto.descripcion,
                categoria = producto.categoria,
                marca = producto.marca,
                unidadMedida = producto.unidadMedida,
                cantidad = producto.cantidad.toString(),
                existenciaMinima = producto.existenciaMinima.toString(),
                existenciaObjetivo = producto.existenciaObjetivo.toString(),
                precioCompra = producto.precioCompra.toString(),
                precioVenta = producto.precioVenta.toString(),
                estado = producto.estado,
                proveedorId = producto.proveedorId,
                ubicacionId = producto.ubicacionId,
                isLoading = false,
                errores = ProductoErrores()
            )
        }
    }

    fun onCodigoChange(valor: String) {
        _estado.update { it.copy(codigo = valor, errores = it.errores.copy(codigo = null)) }
    }

    fun onNombreChange(valor: String) {
        _estado.update { it.copy(nombre = valor, errores = it.errores.copy(nombre = null)) }
    }

    fun onDescripcionChange(valor: String) {
        _estado.update { it.copy(descripcion = valor) }
    }

    fun onCategoriaChange(valor: String) {
        _estado.update { it.copy(categoria = valor, errores = it.errores.copy(categoria = null)) }
    }

    fun onMarcaChange(valor: String) {
        _estado.update { it.copy(marca = valor) }
    }

    fun onUnidadMedidaChange(valor: String) {
        _estado.update { it.copy(unidadMedida = valor) }
    }

    fun onCantidadChange(valor: String) {
        _estado.update { it.copy(cantidad = valor, errores = it.errores.copy(cantidad = null)) }
    }

    fun onExistenciaMinimaChange(valor: String) {
        _estado.update { it.copy(existenciaMinima = valor, errores = it.errores.copy(existenciaMinima = null)) }
    }

    fun onExistenciaObjetivoChange(valor: String) {
        _estado.update { it.copy(existenciaObjetivo = valor, errores = it.errores.copy(existenciaObjetivo = null)) }
    }

    fun onPrecioCompraChange(valor: String) {
        _estado.update { it.copy(precioCompra = valor, errores = it.errores.copy(precioCompra = null)) }
    }

    fun onPrecioVentaChange(valor: String) {
        _estado.update { it.copy(precioVenta = valor, errores = it.errores.copy(precioVenta = null)) }
    }

    fun onEstadoChange(valor: String) {
        _estado.update { it.copy(estado = valor) }
    }

    fun onProveedorChange(valor: Long?) {
        _estado.update { it.copy(proveedorId = valor) }
    }

    fun onUbicacionChange(valor: Long?) {
        _estado.update { it.copy(ubicacionId = valor) }
    }

    fun validarFormulario(): Boolean {
        val estadoActual = _estado.value
        val cantidad = estadoActual.cantidad.toIntOrNull()
        val minima = estadoActual.existenciaMinima.toIntOrNull()
        val objetivo = estadoActual.existenciaObjetivo.toIntOrNull()

        val errores = ProductoErrores(
            codigo = if (estadoActual.codigo.isBlank())
                "Campo obligatorio"
            else null,

            nombre = if (estadoActual.nombre.isBlank())
                "Campo obligatorio"
            else null,

            categoria = if (estadoActual.categoria.isBlank())
                "Campo obligatorio"
            else null,

            cantidad = if (cantidad == null || cantidad < 0)
                "Debe ser un número mayor o igual a 0"
            else null,

            existenciaMinima = if (minima == null || minima < 0)
                "Debe ser un número mayor o igual a 0"
            else null,

            existenciaObjetivo = if (objetivo == null || (minima != null && objetivo < minima))
                "Debe ser un número mayor o igual al mínimo"
            else null,

            precioCompra = if (!esPrecioValido(estadoActual.precioCompra))
                "Debe ser un número mayor o igual a 0"
            else null,

            precioVenta = if (!esPrecioValido(estadoActual.precioVenta))
                "Debe ser un número mayor o igual a 0"
            else null
        )

        val hayErrores = listOfNotNull(
            errores.codigo,
            errores.nombre,
            errores.categoria,
            errores.cantidad,
            errores.existenciaMinima,
            errores.existenciaObjetivo,
            errores.precioCompra,
            errores.precioVenta
        ).isNotEmpty()

        _estado.update {
            it.copy(errores = errores)
        }

        return !hayErrores
    }

    private fun esPrecioValido(valor: String): Boolean {
        if (valor.isBlank()) return true
        val numero = valor.toIntOrNull()
        return numero != null && numero >= 0
    }

    fun guardar(onListo: () -> Unit) {
        val a = _estado.value
        _estado.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            if (repository.existeCodigo(a.codigo.trim(), a.id)) {
                _estado.update {
                    it.copy(isLoading = false, errores = it.errores.copy(codigo = "Ya existe un producto con este código"))
                }
                return@launch
            }

            val ahora = System.currentTimeMillis()

            if (a.id == 0L) {
                val cantidadInicial = a.cantidad.toInt()
                repository.insertar(
                    ProductoEntity(
                        codigo = a.codigo.trim(),
                        nombre = a.nombre.trim(),
                        descripcion = a.descripcion.trim(),
                        categoria = a.categoria.trim(),
                        marca = a.marca.trim(),
                        unidadMedida = a.unidadMedida.trim(),
                        cantidad = cantidadInicial,
                        existenciaMinima = a.existenciaMinima.toInt(),
                        existenciaObjetivo = a.existenciaObjetivo.toInt(),
                        precioCompra = a.precioCompra.toIntOrNull() ?: 0,
                        precioVenta = a.precioVenta.toIntOrNull() ?: 0,
                        estado = a.estado,
                        fechaUltimoIngreso = if (cantidadInicial > 0) ahora else null,
                        proveedorId = a.proveedorId,
                        ubicacionId = a.ubicacionId,
                        fechaCambioUbicacion = if (a.ubicacionId != null) ahora else null
                    )
                )
            } else {
                val anterior = repository.obtenerPorId(a.id) ?: return@launch
                repository.actualizar(
                    anterior.copy(
                        codigo = a.codigo.trim(),
                        nombre = a.nombre.trim(),
                        descripcion = a.descripcion.trim(),
                        categoria = a.categoria.trim(),
                        marca = a.marca.trim(),
                        unidadMedida = a.unidadMedida.trim(),
                        existenciaMinima = a.existenciaMinima.toInt(),
                        existenciaObjetivo = a.existenciaObjetivo.toInt(),
                        precioCompra = a.precioCompra.toIntOrNull() ?: 0,
                        precioVenta = a.precioVenta.toIntOrNull() ?: 0,
                        estado = a.estado,
                        proveedorId = a.proveedorId,
                        ubicacionId = a.ubicacionId,
                        fechaCambioUbicacion = if (anterior.ubicacionId != a.ubicacionId) ahora
                        else anterior.fechaCambioUbicacion
                    )
                )
            }

            _estado.update { it.copy(isLoading = false) }
            onListo()
        }
    }

    fun eliminar(producto: ProductoEntity) {
        viewModelScope.launch {
            repository.eliminar(producto)
        }
    }


    fun seleccionar(id: Long) {
        _estado.update { it.copy(seleccionadoId = id) }
    }

    fun onBusquedaChange(valor: String) {
        _estado.update { it.copy(busqueda = valor) }
    }

    fun onFiltroCategoriaChange(valor: String?) {
        _estado.update { it.copy(filtroCategoria = valor) }
    }

    fun onFiltroProveedorChange(valor: Long?) {
        _estado.update { it.copy(filtroProveedorId = valor) }
    }

    fun filtrar(lista: List<ProductoEntity>, estado: ProductoUiState): List<ProductoEntity> {
        val texto = estado.busqueda.trim()
        return lista.filter { p ->
            (texto.isEmpty() || p.nombre.contains(texto, ignoreCase = true) || p.codigo.contains(texto, ignoreCase = true)) &&
                    (estado.filtroCategoria == null || p.categoria == estado.filtroCategoria) &&
                    (estado.filtroProveedorId == null || p.proveedorId == estado.filtroProveedorId)
        }
    }

    fun categorias(lista: List<ProductoEntity>): List<String> {
        return lista.map { it.categoria }.distinct().sorted()
    }

    fun estadoStock(producto: ProductoEntity): String {
        return when {
            producto.cantidad == 0 -> "Agotado"
            producto.cantidad <= producto.existenciaMinima -> "Bajo el mínimo"
            else -> "Disponible"
        }
    }

    fun contarBajoMinimo(lista: List<ProductoEntity>): Int {
        return lista.count { it.estado == "Activo" && it.cantidad <= it.existenciaMinima }
    }

}
