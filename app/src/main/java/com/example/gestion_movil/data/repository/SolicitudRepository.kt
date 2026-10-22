package com.example.gestion_movil.data.repository

import com.example.gestion_movil.model.data.local.SolicitudDao
import com.example.gestion_movil.model.data.local.SolicitudEntity
import kotlinx.coroutines.flow.Flow

// Repositorio de solicitudes de reposición a proveedores
class SolicitudRepository(private val dao: SolicitudDao) {

    val solicitudes: Flow<List<SolicitudEntity>> = dao.obtenerTodas()

    suspend fun insertar(solicitud: SolicitudEntity) = dao.insertar(solicitud)

    suspend fun actualizar(solicitud: SolicitudEntity) = dao.actualizar(solicitud)

    suspend fun obtenerUltima(proveedorId: Long): SolicitudEntity? = dao.obtenerUltima(proveedorId)
}
