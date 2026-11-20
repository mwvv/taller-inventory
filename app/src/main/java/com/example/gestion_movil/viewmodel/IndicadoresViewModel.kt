package com.example.gestion_movil.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.gestion_movil.data.repository.MovimientoRepository
import com.example.gestion_movil.model.IndicadoresUiState
import com.example.gestion_movil.model.ProductoMovido
import com.example.gestion_movil.model.ResumenPeriodo
import com.example.gestion_movil.model.data.local.GestionDatabase
import com.example.gestion_movil.model.data.local.MovimientoEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.util.Calendar


class IndicadoresViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = MovimientoRepository(GestionDatabase.getDatabase(app).movimientoDao())

    val movimientos: Flow<List<MovimientoEntity>> = repository.movimientos

    private val _estado = MutableStateFlow(IndicadoresUiState())

    val estado: StateFlow<IndicadoresUiState> = _estado

    val periodos = listOf("Hoy", "7 días", "30 días", "Todo")

    fun onPeriodoChange(valor: String) {
        _estado.update { it.copy(periodo = valor) }
    }

    fun filtrarPorPeriodo(lista: List<MovimientoEntity>, periodo: String): List<MovimientoEntity> {
        val ahora = System.currentTimeMillis()
        val dia = 24 * 60 * 60 * 1000L
        val desde = when (periodo) {
            "Hoy" -> inicioDeHoy()
            "7 días" -> ahora - 7 * dia
            "30 días" -> ahora - 30 * dia
            else -> 0L
        }
        return lista.filter { it.fecha >= desde }
    }

    fun resumen(lista: List<MovimientoEntity>): ResumenPeriodo {
        return ResumenPeriodo(
            unidadesVendidas = lista.filter { it.tipo == "Venta" }.sumOf { it.cantidad },
            unidadesConsumidas = lista.filter { it.tipo == "Consumo en servicio" }.sumOf { it.cantidad },
            unidadesIngresadas = lista.filter { it.tipo == "Ingreso" }.sumOf { it.cantidad },
            cantidadMovimientos = lista.size
        )
    }

    fun productosConMasMovimiento(lista: List<MovimientoEntity>, cantidad: Int = 5): List<ProductoMovido> {
        return lista
            .filter { it.tipo == "Venta" || it.tipo == "Consumo en servicio" }
            .groupBy { it.productoId }
            .map { (_, movimientos) ->
                ProductoMovido(
                    nombre = movimientos.first().productoNombre,
                    unidades = movimientos.sumOf { it.cantidad }
                )
            }
            .sortedByDescending { it.unidades }
            .take(cantidad)
    }

    fun proporcion(unidades: Int, maximo: Int): Float {
        return if (maximo == 0) 0f else unidades.toFloat() / maximo
    }

    private fun inicioDeHoy(): Long {
        val calendario = Calendar.getInstance()
        calendario.set(Calendar.HOUR_OF_DAY, 0)
        calendario.set(Calendar.MINUTE, 0)
        calendario.set(Calendar.SECOND, 0)
        calendario.set(Calendar.MILLISECOND, 0)
        return calendario.timeInMillis
    }
}
