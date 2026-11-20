package com.example.gestion_movil.data.repository

import com.example.gestion_movil.data.remote.ProveedorApi
import com.example.gestion_movil.data.remote.RetrofitClient
import com.example.gestion_movil.data.remote.aEntidad
import com.example.gestion_movil.model.data.local.ProveedorDao
import com.example.gestion_movil.model.data.local.ProveedorEntity
import kotlinx.coroutines.flow.Flow

// Resultado de sincronizar con la API REST
data class ResultadoSincronizacion(val nuevos: Int, val actualizados: Int)

// Repositorio de proveedores (datos ficticios).
// Room es la fuente local (funciona sin conexión); la API REST es opcional y solo los actualiza.
class ProveedorRepository(
    private val dao: ProveedorDao,
    private val api: ProveedorApi = RetrofitClient.proveedorApi
) {

    val proveedores: Flow<List<ProveedorEntity>> = dao.obtenerTodos()

    suspend fun insertar(proveedor: ProveedorEntity) = dao.insertar(proveedor)

    suspend fun actualizar(proveedor: ProveedorEntity) = dao.actualizar(proveedor)

    suspend fun eliminar(proveedor: ProveedorEntity) = dao.eliminar(proveedor)

    suspend fun obtenerPorId(id: Long): ProveedorEntity? = dao.obtenerPorId(id)

    suspend fun contar(): Int = dao.contar()

    // Descarga los proveedores de la API y los guarda en Room.
    // Se emparejan por código: si ya existe se actualiza (conservando el id local), si no se inserta.
    // Lanza excepción si no hay conexión o la API falla; los datos locales no se tocan.
    suspend fun sincronizarDesdeApi(): ResultadoSincronizacion {
        val remotos = api.obtenerProveedores()
        var nuevos = 0
        var actualizados = 0
        for (dto in remotos) {
            if (dto.codigo.isBlank() || dto.nombre.isBlank()) continue
            val local = dao.obtenerPorCodigo(dto.codigo.trim())
            if (local == null) {
                dao.insertar(dto.aEntidad())
                nuevos++
            } else {
                dao.actualizar(dto.aEntidad(idLocal = local.id))
                actualizados++
            }
        }
        return ResultadoSincronizacion(nuevos, actualizados)
    }
}
