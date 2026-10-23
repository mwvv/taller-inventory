package com.example.gestion_movil.data.repository

import com.example.gestion_movil.model.data.local.ProductoDao
import com.example.gestion_movil.model.data.local.ProductoEntity
import kotlinx.coroutines.flow.Flow

// Repositorio de productos: único punto de acceso a los datos de productos.
// El ViewModel le pide los datos al repositorio y el repositorio usa el DAO de Room.
class ProductoRepository(private val dao: ProductoDao) {

    // Lista de productos que se actualiza sola cuando cambia la base de datos
    val productos: Flow<List<ProductoEntity>> = dao.obtenerTodos()

    suspend fun insertar(producto: ProductoEntity) = dao.insertar(producto)

    suspend fun actualizar(producto: ProductoEntity) = dao.actualizar(producto)

    suspend fun eliminar(producto: ProductoEntity) = dao.eliminar(producto)

    suspend fun obtenerPorId(id: Long): ProductoEntity? = dao.obtenerPorId(id)

    // true si otro producto ya usa ese código (evita duplicados)
    suspend fun existeCodigo(codigo: String, idActual: Long): Boolean =
        dao.contarCodigo(codigo, idActual) > 0

    // Productos activos bajo el mínimo de un proveedor (para el correo de reposición)
    suspend fun obtenerBajoMinimoPorProveedor(proveedorId: Long): List<ProductoEntity> =
        dao.obtenerBajoMinimoPorProveedor(proveedorId)

    suspend fun contar(): Int = dao.contar()
}
