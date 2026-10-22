package com.example.gestion_movil.model.data.local


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UbicacionDao {

    @Insert
    suspend fun insertar(ubicacion: UbicacionEntity)

    // Modifica un registro existente
    @Update
    suspend fun actualizar(ubicacion: UbicacionEntity)

    // Elimina un registro
    @Delete
    suspend fun eliminar(ubicacion: UbicacionEntity)

    @Query("SELECT * FROM ubicaciones ORDER BY codigo")
    fun obtenerTodas(): Flow<List<UbicacionEntity>>

    @Query("SELECT * FROM ubicaciones WHERE id = :id")
    suspend fun obtenerPorId(id: Long): UbicacionEntity?
}
