package com.example.gestion_movil.ui.screen

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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.gestion_movil.viewmodel.ProductoViewModel
import com.example.gestion_movil.viewmodel.ProveedorViewModel
import com.example.gestion_movil.viewmodel.ReposicionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReposicionScreen(
    navController: NavController,
    viewModel: ReposicionViewModel,
    productoViewModel: ProductoViewModel,
    proveedorViewModel: ProveedorViewModel
) {
    val productos by productoViewModel.productos.collectAsState(initial = emptyList())
    val proveedores by proveedorViewModel.proveedores.collectAsState(initial = emptyList())
    val solicitudes by viewModel.solicitudes.collectAsState(initial = emptyList())

    // Necesidades de compra agrupadas por proveedor (lo calcula el ViewModel)
    val grupos = viewModel.agruparPorProveedor(productos)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reposición") },
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
            item {
                Text("Productos pendientes de compra", style = MaterialTheme.typography.titleMedium)
                Text("Generado: ${formatearFecha(System.currentTimeMillis())}")
                Text("Sugerida = existencia objetivo - cantidad disponible", style = MaterialTheme.typography.bodySmall)
                if (grupos.isEmpty()) Text("No hay productos agotados ni bajo el mínimo.")
            }

            grupos.forEach { (proveedorId, lista) ->
                val proveedor = proveedores.find { it.id == proveedorId }

                item {
                    HorizontalDivider()
                    Text(
                        text = "Proveedor: ${proveedor?.nombre ?: "Sin proveedor asignado"}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (proveedor != null) {
                        Text("Estado de la solicitud: ${viewModel.estadoSolicitud(solicitudes, proveedor.id)}")
                    }
                }

                items(lista) { producto ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(10.dp)) {
                            Text("${producto.nombre} (${producto.codigo})", fontWeight = FontWeight.Bold)
                            Text("Disponible: ${producto.cantidad} - ${productoViewModel.estadoStock(producto)}")
                            Text("Existencia mínima: ${producto.existenciaMinima}")
                            Text("Cantidad sugerida: ${viewModel.cantidadSugerida(producto)} ${producto.unidadMedida}")
                        }
                    }
                }

                if (proveedor != null) {
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { viewModel.cambiarEstado(proveedor.id) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cambiar estado")
                            }
                            Button(
                                onClick = {
                                    viewModel.prepararCorreo(proveedor.id)
                                    navController.navigate("correo")
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Preparar correo")
                            }
                        }
                    }
                }
            }
        }
    }
}
