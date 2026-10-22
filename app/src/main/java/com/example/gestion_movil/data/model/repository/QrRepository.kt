package com.example.gestion_movil.data.model.repository



import com.example.gestion_movil.data.model.QrResult
import com.example.gestion_movil.model.data.local.ProductoEntity

class QrRepository {
    fun processQrContent(content: String): QrResult {
        // Aquí podrías guardar o procesar el QR en una BD o API
        return QrResult(content)
    }

    // Busca el producto cuyo código interno es el contenido del QR (ej: "F001")
    fun buscarProducto(content: String, productos: List<ProductoEntity>): ProductoEntity? {
        val codigo = content.trim()
        return productos.find { it.codigo.equals(codigo, ignoreCase = true) }
    }
}
