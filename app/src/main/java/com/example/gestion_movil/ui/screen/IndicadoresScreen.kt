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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gestion_movil.model.ProductoMovido
import com.example.gestion_movil.model.ResumenPeriodo
import com.example.gestion_movil.ui.theme.GestionMovilTheme
import com.example.gestion_movil.viewmodel.IndicadoresViewModel

@Composable
fun IndicadoresScreen(
    navController: NavController,
    viewModel: IndicadoresViewModel
) {
    val estado by viewModel.estado.collectAsState()
    val movimientos by viewModel.movimientos.collectAsState(initial = emptyList())

    val delPeriodo = viewModel.filtrarPorPeriodo(movimientos, estado.periodo)
    val resumen = viewModel.resumen(delPeriodo)
    val ranking = viewModel.productosConMasMovimiento(delPeriodo)
    val maximo = ranking.firstOrNull()?.unidades ?: 0

    IndicadoresContent(
        periodos = viewModel.periodos,
        periodoSeleccionado = estado.periodo,
        resumen = resumen,
        ranking = ranking,
        proporcion = { unidades -> viewModel.proporcion(unidades, maximo) },
        onPeriodoChange = viewModel::onPeriodoChange,
        onBack = { navController.popBackStack() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IndicadoresContent(
    periodos: List<String>,
    periodoSeleccionado: String,
    resumen: ResumenPeriodo,
    ranking: List<ProductoMovido>,
    proporcion: (Int) -> Float,
    onPeriodoChange: (String) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Indicadores") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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

            item {
                Text("Período", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    periodos.forEach { periodo ->
                        FilterChip(
                            selected = periodoSeleccionado == periodo,
                            onClick = { onPeriodoChange(periodo) },
                            label = { Text(periodo) }
                        )
                    }
                }
            }

            item {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Ventas y consumos ($periodoSeleccionado)", style = MaterialTheme.typography.titleMedium)
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

            itemsIndexed(ranking) { posicion, producto ->
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        "${posicion + 1}. ${producto.nombre}: ${producto.unidades} unidades",
                        fontWeight = if (posicion == 0) FontWeight.Bold else FontWeight.Normal
                    )
                    LinearProgressIndicator(
                        progress = { proporcion(producto.unidades) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun IndicadoresContentPreview() {
    val ranking = listOf(
        ProductoMovido(nombre = "Aceite 5W-30", unidades = 24),
        ProductoMovido(nombre = "Filtro de aire", unidades = 15),
        ProductoMovido(nombre = "Pastillas de freno", unidades = 9)
    )
    val maximo = ranking.first().unidades
    GestionMovilTheme {
        IndicadoresContent(
            periodos = listOf("Hoy", "7 días", "30 días", "Todo"),
            periodoSeleccionado = "30 días",
            resumen = ResumenPeriodo(
                unidadesVendidas = 39,
                unidadesConsumidas = 9,
                unidadesIngresadas = 60,
                cantidadMovimientos = 18
            ),
            ranking = ranking,
            proporcion = { unidades -> if (maximo == 0) 0f else unidades.toFloat() / maximo },
            onPeriodoChange = {},
            onBack = {}
        )
    }
}
