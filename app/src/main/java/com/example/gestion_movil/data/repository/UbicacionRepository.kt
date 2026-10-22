package com.example.gestion_movil.data.repository

import com.example.gestion_movil.model.data.local.UbicacionDao
import com.example.gestion_movil.model.data.local.UbicacionEntity
import kotlinx.coroutines.flow.Flow

// Repositorio de ubicaciones de la bodega
class UbicacionRepository(private val dao: UbicacionDao) {

    val ubicaciones: Flow<List<UbicacionEntity>> = dao.obtenerTodas()

    suspend fun insertar(ubicacion: UbicacionEntity) = dao.insertar(ubicacion)

    suspend fun actualizar(ubicacion: UbicacionEntity) = dao.actualizar(ubicacion)

    suspend fun eliminar(ubicacion: UbicacionEntity) = dao.eliminar(ubicacion)
}
