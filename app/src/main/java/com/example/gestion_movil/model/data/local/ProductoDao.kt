package com.example.gestion_movil.model.data.local


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {

    @Insert
    suspend fun insertar(producto: ProductoEntity)

    // Modifica un registro existente
    @Update
    suspend fun actualizar(producto: ProductoEntity)

    // Elimina un registro
    @Delete
    suspend fun eliminar(producto: ProductoEntity)

    @Query("SELECT * FROM productos ORDER BY nombre")
    fun obtenerTodos(): Flow<List<ProductoEntity>>

    @Query("SELECT * FROM productos WHERE id = :id")
    suspend fun obtenerPorId(id: Long): ProductoEntity?

    // Cuenta si otro producto ya usa el mismo código (evita duplicados)
    @Query("SELECT COUNT(*) FROM productos WHERE codigo = :codigo AND id != :id")
    suspend fun contarCodigo(codigo: String, id: Long): Int

    // Productos bajo el mínimo de un proveedor (para el correo)
    @Query("SELECT * FROM productos WHERE proveedorId = :proveedorId AND cantidad <= existenciaMinima AND estado = 'Activo' ORDER BY nombre")
    suspend fun obtenerBajoMinimoPorProveedor(proveedorId: Long): List<ProductoEntity>
}
