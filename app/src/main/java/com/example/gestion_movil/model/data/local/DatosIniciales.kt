package com.example.gestion_movil.model.data.local

object DatosIniciales {

    fun proveedor(): ProveedorEntity = ProveedorEntity(
        id = 1,
        codigo = "P001",
        nombre = "Repuestos Andes",
        contacto = "Ana Ficticia",
        correo = "ventas@andes-ficticio.cl",
        telefono = "",
        categorias = "",
        activo = true,
        observaciones = ""
    )

    fun ubicacion(ahora: Long): UbicacionEntity = UbicacionEntity(
        id = 1,
        codigo = "A-2-3",
        zona = "Bodega A",
        estante = "2",
        repisa = "3",
        posicion = "izquierda",
        descripcion = "",
        estado = "Disponible",
        fechaActualizacion = ahora
    )


    fun productos(ahora: Long): List<ProductoEntity> = listOf(
        ProductoEntity(
            codigo = "F001", nombre = "Filtro de aceite", descripcion = "", categoria = "Filtros",
            marca = "Bosch", unidadMedida = "Unidad", cantidad = 2, existenciaMinima = 3,
            existenciaObjetivo = 10, precioCompra = 0, precioVenta = 0, estado = "Activo",
            fechaUltimoIngreso = ahora, proveedorId = 1, ubicacionId = 1, fechaCambioUbicacion = ahora
        ),
        ProductoEntity(
            codigo = "L010", nombre = "Aceite 10W40 1L", descripcion = "", categoria = "Lubricantes",
            marca = "", unidadMedida = "Unidad", cantidad = 12, existenciaMinima = 4,
            existenciaObjetivo = 20, precioCompra = 0, precioVenta = 0, estado = "Activo",
            fechaUltimoIngreso = ahora
        ),
        ProductoEntity(
            codigo = "B020", nombre = "Batería 12V 60Ah", descripcion = "Batería libre de mantención",
            categoria = "Eléctrico", marca = "Bosch", unidadMedida = "Unidad", cantidad = 6,
            existenciaMinima = 2, existenciaObjetivo = 10, precioCompra = 65000, precioVenta = 89990,
            estado = "Activo", fechaUltimoIngreso = ahora, proveedorId = 1, ubicacionId = 1,
            fechaCambioUbicacion = ahora
        ),
        ProductoEntity(
            codigo = "P030", nombre = "Pastillas de freno", descripcion = "Juego delantero",
            categoria = "Frenos", marca = "Brembo", unidadMedida = "Juego", cantidad = 8,
            existenciaMinima = 4, existenciaObjetivo = 16, precioCompra = 18000, precioVenta = 27990,
            estado = "Activo", fechaUltimoIngreso = ahora, proveedorId = 1
        ),
        ProductoEntity(
            codigo = "R040", nombre = "Refrigerante 1L", descripcion = "Refrigerante verde concentrado",
            categoria = "Líquidos", marca = "Prestone", unidadMedida = "Unidad", cantidad = 3,
            existenciaMinima = 5, existenciaObjetivo = 15, precioCompra = 4500, precioVenta = 7990,
            estado = "Activo", fechaUltimoIngreso = ahora, proveedorId = 1
        )
    )
}
