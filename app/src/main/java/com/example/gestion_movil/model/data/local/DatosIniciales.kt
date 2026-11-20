package com.example.gestion_movil.model.data.local

// Datos de ejemplo que se cargan cuando la base está vacía.
// Cubren los casos que se prueban en la app: producto sano, bajo el mínimo, agotado,
// sin proveedor, sin ubicación, inactivo, proveedor inactivo y ubicación no disponible.
// Los ids son fijos porque los productos y movimientos hacen referencia a ellos.
object DatosIniciales {

    fun proveedores(): List<ProveedorEntity> = listOf(
        ProveedorEntity(
            id = 1, codigo = "P001", nombre = "Repuestos Andes", contacto = "Ana Ficticia",
            correo = "ventas@andes-ficticio.cl", telefono = "", categorias = "Filtros, Frenos, Eléctrico",
            activo = true, observaciones = ""
        ),
        ProveedorEntity(
            id = 2, codigo = "P002", nombre = "Lubricantes del Sur", contacto = "Luis Ejemplo",
            correo = "pedidos@lubsur-ficticio.cl", telefono = "+56 9 1111 2222",
            categorias = "Lubricantes, Líquidos", activo = true, observaciones = "Entrega en 48 horas"
        ),
        ProveedorEntity(
            id = 3, codigo = "P003", nombre = "AutoPartes Norte", contacto = "Marta Demo",
            correo = "contacto@norte-ficticio.cl", telefono = "", categorias = "Eléctrico",
            activo = false, observaciones = "Proveedor inactivo (para probar el filtro)"
        )
    )

    fun ubicaciones(ahora: Long): List<UbicacionEntity> = listOf(
        UbicacionEntity(
            id = 1, codigo = "A-2-3", zona = "Bodega A", estante = "2", repisa = "3",
            posicion = "izquierda", descripcion = "", estado = "Disponible", fechaActualizacion = ahora
        ),
        UbicacionEntity(
            id = 2, codigo = "A-1-1", zona = "Bodega A", estante = "1", repisa = "1",
            posicion = "derecha", descripcion = "Lubricantes", estado = "Ocupada", fechaActualizacion = ahora
        ),
        UbicacionEntity(
            id = 3, codigo = "B-1-2", zona = "Bodega B", estante = "1", repisa = "2",
            posicion = "centro", descripcion = "", estado = "Fuera de uso", fechaActualizacion = ahora
        )
    )

    // Se insertan en este orden: reciben los ids 1..8 en una base vacía
    fun productos(ahora: Long): List<ProductoEntity> = listOf(
        // 1: bajo el mínimo (2 < 3)
        ProductoEntity(
            codigo = "F001", nombre = "Filtro de aceite", descripcion = "", categoria = "Filtros",
            marca = "Bosch", unidadMedida = "Unidad", cantidad = 2, existenciaMinima = 3,
            existenciaObjetivo = 10, precioCompra = 3500, precioVenta = 5990, estado = "Activo",
            fechaUltimoIngreso = ahora, proveedorId = 1, ubicacionId = 1, fechaCambioUbicacion = ahora
        ),
        // 2: sano, sin proveedor ni ubicación
        ProductoEntity(
            codigo = "L010", nombre = "Aceite 10W40 1L", descripcion = "", categoria = "Lubricantes",
            marca = "", unidadMedida = "Unidad", cantidad = 12, existenciaMinima = 4,
            existenciaObjetivo = 20, precioCompra = 0, precioVenta = 0, estado = "Activo",
            fechaUltimoIngreso = ahora
        ),
        // 3: sano
        ProductoEntity(
            codigo = "B020", nombre = "Batería 12V 60Ah", descripcion = "Batería libre de mantención",
            categoria = "Eléctrico", marca = "Bosch", unidadMedida = "Unidad", cantidad = 6,
            existenciaMinima = 2, existenciaObjetivo = 10, precioCompra = 65000, precioVenta = 89990,
            estado = "Activo", fechaUltimoIngreso = ahora, proveedorId = 1, ubicacionId = 1,
            fechaCambioUbicacion = ahora
        ),
        // 4: sano, con proveedor y sin ubicación
        ProductoEntity(
            codigo = "P030", nombre = "Pastillas de freno", descripcion = "Juego delantero",
            categoria = "Frenos", marca = "Brembo", unidadMedida = "Juego", cantidad = 8,
            existenciaMinima = 4, existenciaObjetivo = 16, precioCompra = 18000, precioVenta = 27990,
            estado = "Activo", fechaUltimoIngreso = ahora, proveedorId = 1
        ),
        // 5: bajo el mínimo (3 < 5), proveedor 2
        ProductoEntity(
            codigo = "R040", nombre = "Refrigerante 1L", descripcion = "Refrigerante verde concentrado",
            categoria = "Líquidos", marca = "Prestone", unidadMedida = "Unidad", cantidad = 3,
            existenciaMinima = 5, existenciaObjetivo = 15, precioCompra = 4500, precioVenta = 7990,
            estado = "Activo", fechaUltimoIngreso = ahora, proveedorId = 2
        ),
        // 6: agotado (0), proveedor 2
        ProductoEntity(
            codigo = "L011", nombre = "Aceite 5W30 sintético 1L", descripcion = "",
            categoria = "Lubricantes", marca = "Castrol", unidadMedida = "Unidad", cantidad = 0,
            existenciaMinima = 6, existenciaObjetivo = 24, precioCompra = 7200, precioVenta = 11990,
            estado = "Activo", fechaUltimoIngreso = ahora, fechaUltimaSalida = ahora,
            proveedorId = 2, ubicacionId = 2, fechaCambioUbicacion = ahora
        ),
        // 7: agotado, sin proveedor (cae en el grupo "sin proveedor" de reposición)
        ProductoEntity(
            codigo = "E050", nombre = "Ampolleta H7", descripcion = "", categoria = "Eléctrico",
            marca = "Philips", unidadMedida = "Unidad", cantidad = 0, existenciaMinima = 4,
            existenciaObjetivo = 12, precioCompra = 2500, precioVenta = 4990, estado = "Activo",
            fechaUltimoIngreso = ahora
        ),
        // 8: descontinuado, bajo el mínimo (no debe aparecer en reposición)
        ProductoEntity(
            codigo = "D060", nombre = "Correa de distribución antigua", descripcion = "Modelo retirado",
            categoria = "Motor", marca = "Gates", unidadMedida = "Unidad", cantidad = 1,
            existenciaMinima = 2, existenciaObjetivo = 4, precioCompra = 15000, precioVenta = 24990,
            estado = "Descontinuado", fechaUltimoIngreso = ahora
        )
    )

    // Historial de ejemplo. productoId corresponde al orden de productos()
    fun movimientos(ahora: Long): List<MovimientoEntity> {
        val hora = 60L * 60 * 1000
        return listOf(
            MovimientoEntity(
                productoId = 1, productoNombre = "Filtro de aceite", tipo = "Ingreso", cantidad = 10,
                fecha = ahora - 48 * hora, motivo = "Compra", observacion = "",
                existenciaAnterior = 0, existenciaResultante = 10, usuario = "admin"
            ),
            MovimientoEntity(
                productoId = 1, productoNombre = "Filtro de aceite", tipo = "Consumo en servicio", cantidad = 8,
                fecha = ahora - 24 * hora, motivo = "Servicio mecánico", observacion = "",
                existenciaAnterior = 10, existenciaResultante = 2, usuario = "operador"
            ),
            MovimientoEntity(
                productoId = 6, productoNombre = "Aceite 5W30 sintético 1L", tipo = "Venta", cantidad = 5,
                fecha = ahora - 2 * hora, motivo = "Venta al cliente", observacion = "",
                existenciaAnterior = 5, existenciaResultante = 0, usuario = "operador"
            )
        )
    }
}
