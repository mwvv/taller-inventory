package com.example.gestion_movil.model.data.local


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SolicitudDao {

    @Insert
    suspend fun insertar(solicitud: SolicitudEntity)

    // Modifica un registro existente
    @Update
    suspend fun actualizar(solicitud: SolicitudEntity)

    @Query("SELECT * FROM solicitudes ORDER BY id DESC")
    fun obtenerTodas(): Flow<List<SolicitudEntity>>

    // Última solicitud de un proveedor
    @Query("SELECT * FROM solicitudes WHERE proveedorId = :proveedorId ORDER BY id DESC LIMIT 1")
    suspend fun obtenerUltima(proveedorId: Long): SolicitudEntity?
}
