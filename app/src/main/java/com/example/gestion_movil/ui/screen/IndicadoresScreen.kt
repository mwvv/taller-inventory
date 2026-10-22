package com.example.gestion_movil.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gestion_movil.viewmodel.IndicadoresViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IndicadoresScreen(
    navController: NavController,
    viewModel: IndicadoresViewModel
) {
    val estado by viewModel.estado.collectAsState()
    val movimientos by viewModel.movimientos.collectAsState(initial = emptyList())

    // Todos los cálculos los hace el ViewModel; la pantalla solo los muestra
    val delPeriodo = viewModel.filtrarPorPeriodo(movimientos, estado.periodo)
    val resumen = viewModel.resumen(delPeriodo)
    val ranking = viewModel.productosConMasMovimiento(delPeriodo)
    val maximo = ranking.firstOrNull()?.unidades ?: 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Indicadores") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Filtro de período
            item {
                Text("Período", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    viewModel.periodos.forEach { periodo ->
                        FilterChip(
                            selected = estado.periodo == periodo,
                            onClick = { viewModel.onPeriodoChange(periodo) },
                            label = { Text(periodo) }
                        )
                    }
                }
            }

            // Ventas y consumos del período
            item {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Ventas y consumos (${estado.periodo})", style = MaterialTheme.typography.titleMedium)
                        Text("Unidades vendidas: ${resumen.unidadesVendidas}")
                        Text("Unidades usadas en servicios: ${resumen.unidadesConsumidas}")
                        Text("Unidades ingresadas: ${resumen.unidadesIngresadas}")
                        Text("Movimientos registrados: ${resumen.cantidadMovimientos}")
                    }
                }
            }

            item {
                Text(
                    "Productos con mayor movimiento",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    "Unidades vendidas + usadas en servicios",
                    style = MaterialTheme.typography.bodySmall
                )
                if (ranking.isEmpty()) {
                    Text("No hay ventas ni consumos en este período.")
                }
            }

            // Ranking: número, nombre, unidades y una barra proporcional
            itemsIndexed(ranking) { posicion, producto ->
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        "${posicion + 1}. ${producto.nombre}: ${producto.unidades} unidades",
                        fontWeight = if (posicion == 0) FontWeight.Bold else FontWeight.Normal
                    )
                    LinearProgressIndicator(
                        progress = { viewModel.proporcion(producto.unidades, maximo) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
