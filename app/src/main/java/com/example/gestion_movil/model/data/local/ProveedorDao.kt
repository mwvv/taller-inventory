package com.example.gestion_movil.model.data.local


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProveedorDao {

    @Insert
    suspend fun insertar(proveedor: ProveedorEntity)

    // Modifica un registro existente
    @Update
    suspend fun actualizar(proveedor: ProveedorEntity)

    // Elimina un registro
    @Delete
    suspend fun eliminar(proveedor: ProveedorEntity)

    @Query("SELECT * FROM proveedores ORDER BY nombre")
    fun obtenerTodos(): Flow<List<ProveedorEntity>>

    @Query("SELECT * FROM proveedores WHERE id = :id")
    suspend fun obtenerPorId(id: Long): ProveedorEntity?
}
