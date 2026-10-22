package com.example.gestion_movil.data.repository

import com.example.gestion_movil.model.data.local.MovimientoDao
import com.example.gestion_movil.model.data.local.MovimientoEntity
import kotlinx.coroutines.flow.Flow

// Repositorio del historial de movimientos de inventario
class MovimientoRepository(private val dao: MovimientoDao) {

    val movimientos: Flow<List<MovimientoEntity>> = dao.obtenerTodos()

    suspend fun insertar(movimiento: MovimientoEntity) = dao.insertar(movimiento)
}
