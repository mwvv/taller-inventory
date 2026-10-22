package com.example.gestion_movil.model

// Estado de la pantalla de indicadores
data class IndicadoresUiState(
    val periodo: String = "7 días"          // Hoy / 7 días / 30 días / Todo
)

// Totales de unidades movidas en el período
data class ResumenPeriodo(
    val unidadesVendidas: Int = 0,
    val unidadesConsumidas: Int = 0,        // Consumo en servicio mecánico
    val unidadesIngresadas: Int = 0,
    val cantidadMovimientos: Int = 0
)

// Un producto del ranking de mayor movimiento
data class ProductoMovido(
    val nombre: String,
    val unidades: Int                       // Unidades vendidas + consumidas
)
