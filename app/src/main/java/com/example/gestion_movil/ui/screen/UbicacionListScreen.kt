package com.example.gestion_movil.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gestion_movil.model.data.local.UbicacionEntity
import com.example.gestion_movil.model.formatearFecha
import com.example.gestion_movil.viewmodel.ProductoViewModel
import com.example.gestion_movil.viewmodel.UbicacionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UbicacionListScreen(
    navController: NavController,
    viewModel: UbicacionViewModel,
    productoViewModel: ProductoViewModel,
    puedeAdministrar: Boolean = true        // false = operador (solo consulta)
) {
    val ubicaciones by viewModel.ubicaciones.collectAsState(initial = emptyList())
    val productos by productoViewModel.productos.collectAsState(initial = emptyList())

    // Ubicación que se quiere eliminar (para la confirmación)
    var aEliminar by remember { mutableStateOf<UbicacionEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ubicaciones") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            // Solo el propietario registra ubicaciones
            if (puedeAdministrar) {
                FloatingActionButton(onClick = {
                    viewModel.nuevo()
                    navController.navigate("ubicacion_form")
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Registrar ubicación")
                }
            }
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (ubicaciones.isEmpty()) {
                item { Text(if (puedeAdministrar) "No hay ubicaciones. Use + para registrar una." else "No hay ubicaciones registradas.") }
            }

            items(ubicaciones) { ubicacion ->
                // Productos guardados en esta ubicación
                val enUbicacion = productos.filter { it.ubicacionId == ubicacion.id }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = puedeAdministrar) {
                            viewModel.cargar(ubicacion)
                            navController.navigate("ubicacion_form")
                        }
                ) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(ubicacion.codigo, fontWeight = FontWeight.Bold)
                            Text(viewModel.texto(ubicacion))
                            Text("Estado: ${ubicacion.estado}")
                            Text("Actualizada: ${formatearFecha(ubicacion.fechaActualizacion)}")
                            Text("Productos: " + if (enUbicacion.isEmpty()) "ninguno" else enUbicacion.joinToString { it.nombre })
                        }
                        if (puedeAdministrar) {
                            IconButton(onClick = { aEliminar = ubicacion }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar ubicación")
                            }
                        }
                    }
                }
            }
        }

        // Confirmación antes de eliminar
        aEliminar?.let { ubicacion ->
            AlertDialog(
                onDismissRequest = { aEliminar = null },
                title = { Text("Eliminar ubicación") },
                text = { Text("¿Seguro que desea eliminar la ubicación ${ubicacion.codigo}?") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.eliminar(ubicacion)
                        aEliminar = null
                    }) {
                        Text("Eliminar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { aEliminar = null }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}
