package com.example.gestion_movil.data.repository

import com.example.gestion_movil.model.data.local.ProveedorDao
import com.example.gestion_movil.model.data.local.ProveedorEntity
import kotlinx.coroutines.flow.Flow

// Repositorio de proveedores (datos ficticios)
class ProveedorRepository(private val dao: ProveedorDao) {

    val proveedores: Flow<List<ProveedorEntity>> = dao.obtenerTodos()

    suspend fun insertar(proveedor: ProveedorEntity) = dao.insertar(proveedor)

    suspend fun actualizar(proveedor: ProveedorEntity) = dao.actualizar(proveedor)

    suspend fun eliminar(proveedor: ProveedorEntity) = dao.eliminar(proveedor)

    suspend fun obtenerPorId(id: Long): ProveedorEntity? = dao.obtenerPorId(id)
}
