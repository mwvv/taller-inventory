package com.example.gestion_movil.viewmodel

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestion_movil.model.CorreoUiState
import com.example.gestion_movil.data.repository.ProductoRepository
import com.example.gestion_movil.data.repository.ProveedorRepository
import com.example.gestion_movil.data.repository.SolicitudRepository
import com.example.gestion_movil.model.data.local.GestionDatabase
import com.example.gestion_movil.model.data.local.ProductoEntity
import com.example.gestion_movil.model.data.local.SolicitudEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class ReposicionViewModel(app: Application) : AndroidViewModel(app) {

    private val database = GestionDatabase.getDatabase(app)
    // El ViewModel trabaja con el repositorio, no con el DAO directamente
    private val productoRepository = ProductoRepository(database.productoDao())
    private val proveedorRepository = ProveedorRepository(database.proveedorDao())
    private val solicitudRepository = SolicitudRepository(database.solicitudDao())

    // Solicitudes de reposición guardadas en Room
    val solicitudes: Flow<List<SolicitudEntity>> = solicitudRepository.solicitudes

    // Estado del borrador de correo
    private val _correo = MutableStateFlow(CorreoUiState())
    val correo: StateFlow<CorreoUiState> = _correo

    // Estados de una solicitud, en orden
    private val estadosSolicitud = listOf("Pendiente", "Preparada", "Enviada", "Atendida")

    // Nombre comercial ficticio del taller (va en la firma del correo)
    private val nombreTaller = "Taller Mecánico Puente Alto (ficticio)"

    // ---------- Reposición ----------

    // Productos activos agotados o bajo su existencia mínima
    fun bajoMinimo(lista: List<ProductoEntity>): List<ProductoEntity> {
        return lista.filter { it.estado == "Activo" && it.cantidad <= it.existenciaMinima }
    }

    // Regla académica del caso: cantidad sugerida = existencia objetivo - cantidad disponible
    fun cantidadSugerida(producto: ProductoEntity): Int {
        return (producto.existenciaObjetivo - producto.cantidad).coerceAtLeast(0)
    }

    // Agrupa las necesidades de compra por proveedor
    fun agruparPorProveedor(lista: List<ProductoEntity>): Map<Long?, List<ProductoEntity>> {
        return bajoMinimo(lista).groupBy { it.proveedorId }
    }

    // Estado de la última solicitud del proveedor (si no hay, está Pendiente)
    fun estadoSolicitud(lista: List<SolicitudEntity>, proveedorId: Long): String {
        return lista.filter { it.proveedorId == proveedorId }.maxByOrNull { it.id }?.estado ?: "Pendiente"
    }

    // Pasa la solicitud al siguiente estado: Pendiente -> Preparada -> Enviada -> Atendida -> Pendiente
    fun cambiarEstado(proveedorId: Long) {
        viewModelScope.launch {
            val ultima = solicitudRepository.obtenerUltima(proveedorId)
            val actual = ultima?.estado ?: "Pendiente"
            val siguiente = estadosSolicitud[(estadosSolicitud.indexOf(actual) + 1) % estadosSolicitud.size]
            guardarEstado(ultima, proveedorId, siguiente)
        }
    }

    // Una solicitud Atendida ya terminó: el siguiente estado crea una solicitud nueva
    private suspend fun guardarEstado(ultima: SolicitudEntity?, proveedorId: Long, estado: String) {
        if (ultima == null || ultima.estado == "Atendida") {
            solicitudRepository.insertar(
                SolicitudEntity(
                    proveedorId = proveedorId,
                    fecha = System.currentTimeMillis(),
                    estado = estado,
                    observaciones = ""
                )
            )
        } else {
            solicitudRepository.actualizar(ultima.copy(estado = estado))
        }
    }

    // ---------- Correo tipo ----------

    // Arma el borrador del correo para un proveedor
    fun prepararCorreo(proveedorId: Long) {
        viewModelScope.launch {
            val proveedor = proveedorRepository.obtenerPorId(proveedorId) ?: return@launch
            val productos = productoRepository.obtenerBajoMinimoPorProveedor(proveedorId)

            val listado = productos.joinToString("\n") { p ->
                "- ${p.nombre} (código ${p.codigo}): ${cantidadSugerida(p)} ${p.unidadMedida}"
            }

            val saludo = if (proveedor.contacto.isBlank()) "Estimados ${proveedor.nombre}:"
            else "Estimado/a ${proveedor.contacto}:"

            val cuerpo = """
                |$saludo
                |
                |Junto con saludar, solicitamos disponibilidad y cotización de los siguientes productos:
                |
                |$listado
                |
                |Observaciones:
                |
                |Quedamos atentos a su respuesta.
                |
                |Saludos cordiales,
                |$nombreTaller
            """.trimMargin()

            _correo.value = CorreoUiState(
                proveedorId = proveedorId,
                destinatario = proveedor.correo,
                asunto = "Solicitud de cotización - $nombreTaller",
                cuerpo = cuerpo
            )
        }
    }

    // El propietario puede revisar y modificar todo antes de abrir el correo
    fun onDestinatarioChange(valor: String) {
        _correo.update { it.copy(destinatario = valor, error = null) }
    }

    fun onAsuntoChange(valor: String) {
        _correo.update { it.copy(asunto = valor, error = null) }
    }

    fun onCuerpoChange(valor: String) {
        _correo.update { it.copy(cuerpo = valor, error = null) }
    }

    fun validarCorreo(): Boolean {
        val c = _correo.value
        val error = when {
            !c.destinatario.contains("@") -> "Destinatario inválido"
            c.asunto.isBlank() -> "El asunto es obligatorio"
            c.cuerpo.isBlank() -> "El mensaje está vacío"
            else -> null
        }
        _correo.update { it.copy(error = error) }
        return error == null
    }

    fun onErrorCorreo(mensaje: String) {
        _correo.update { it.copy(error = mensaje) }
    }

    // Intent que abre la app de correo con el borrador. NO envía nada: el propietario decide.
    fun crearIntentCorreo(): Intent {
        val c = _correo.value
        val mailto = "mailto:" + Uri.encode(c.destinatario) +
                "?subject=" + Uri.encode(c.asunto) +
                "&body=" + Uri.encode(c.cuerpo)
        return Intent(Intent.ACTION_SENDTO, Uri.parse(mailto)).apply {
            putExtra(Intent.EXTRA_EMAIL, arrayOf(c.destinatario))
            putExtra(Intent.EXTRA_SUBJECT, c.asunto)
            putExtra(Intent.EXTRA_TEXT, c.cuerpo)
        }
    }

    // Al abrir el correo la solicitud queda Preparada (si estaba Pendiente)
    fun marcarPreparada() {
        val proveedorId = _correo.value.proveedorId
        viewModelScope.launch {
            val ultima = solicitudRepository.obtenerUltima(proveedorId)
            val actual = ultima?.estado ?: "Pendiente"
            if (actual == "Pendiente" || actual == "Atendida") {
                guardarEstado(ultima, proveedorId, "Preparada")
            }
        }
    }

}
