package com.example.gestion_movil.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.gestion_movil.model.formatearFecha
import com.example.gestion_movil.viewmodel.MovimientoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(
    navController: NavController,
    viewModel: MovimientoViewModel
) {
    val movimientos by viewModel.movimientos.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de movimientos") },
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (movimientos.isEmpty()) {
                item { Text("Todavía no hay movimientos registrados") }
            }

            items(movimientos) { movimiento ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Text("${movimiento.tipo} - ${movimiento.productoNombre}", fontWeight = FontWeight.Bold)
                        Text("Fecha: ${formatearFecha(movimiento.fecha)}")
                        Text("Cantidad: ${movimiento.cantidad}")
                        Text("Existencia: ${movimiento.existenciaAnterior} -> ${movimiento.existenciaResultante}")
                        Text("Registró: ${movimiento.usuario}")
                        if (movimiento.motivo.isNotBlank()) Text("Motivo: ${movimiento.motivo}")
                        if (movimiento.observacion.isNotBlank()) Text("Observación: ${movimiento.observacion}")
                    }
                }
            }
        }
    }
}
