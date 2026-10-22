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
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gestion_movil.model.formatearFecha
import com.example.gestion_movil.viewmodel.MovimientoViewModel
import com.example.gestion_movil.viewmodel.ProductoViewModel
import com.example.gestion_movil.viewmodel.ProveedorViewModel
import com.example.gestion_movil.viewmodel.UbicacionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoDetalleScreen(
    navController: NavController,
    username: String,
    viewModel: ProductoViewModel,
    movimientoViewModel: MovimientoViewModel,
    proveedorViewModel: ProveedorViewModel,
    ubicacionViewModel: UbicacionViewModel,
    puedeAdministrar: Boolean = true,       // Modificar y eliminar (solo propietario)
    puedeVerPrecios: Boolean = true         // Precios = información sensible (solo propietario)
) {
    val estado by viewModel.estado.collectAsState()
    val productos by viewModel.productos.collectAsState(initial = emptyList())
    val movimientos by movimientoViewModel.movimientos.collectAsState(initial = emptyList())
    val proveedores by proveedorViewModel.proveedores.collectAsState(initial = emptyList())
    val ubicaciones by ubicacionViewModel.ubicaciones.collectAsState(initial = emptyList())

    val producto = productos.find { it.id == estado.seleccionadoId }

    // Confirmación antes de eliminar
    var mostrarDialogo by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del producto") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->

        if (producto == null) {
            Text("Producto no encontrado", modifier = Modifier.padding(innerPadding).padding(16.dp))
            return@Scaffold
        }

        val historial = movimientoViewModel.filtrarPorProducto(movimientos, producto.id)

        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            item {
                Text(producto.nombre, style = MaterialTheme.typography.headlineSmall)
                Text("Código: ${producto.codigo}")
                Text("Descripción: ${producto.descripcion}")
                Text("Categoría: ${producto.categoria}   Marca: ${producto.marca}")
                Text(
                    text = "Existencia: ${producto.cantidad} ${producto.unidadMedida} - ${viewModel.estadoStock(producto)}",
                    fontWeight = FontWeight.Bold
                )
                Text("Existencia mínima: ${producto.existenciaMinima}   Objetivo: ${producto.existenciaObjetivo}")
                if (puedeVerPrecios) {
                    Text("Precio compra: $${producto.precioCompra}   Precio venta: $${producto.precioVenta}")
                }
                Text("Estado: ${producto.estado}")
                Text("Proveedor: ${proveedorViewModel.nombre(proveedores, producto.proveedorId)}")
                Text("Ubicación: ${ubicacionViewModel.texto(ubicacionViewModel.buscar(ubicaciones, producto.ubicacionId))}")
                Text("Cambio de ubicación: ${formatearFecha(producto.fechaCambioUbicacion)}")
                Text("Último ingreso: ${formatearFecha(producto.fechaUltimoIngreso)}")
                Text("Última salida: ${formatearFecha(producto.fechaUltimaSalida)}")
            }

            item {
                Button(
                    onClick = {
                        movimientoViewModel.iniciar(producto.id)
                        navController.navigate("movimiento/$username")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Registrar movimiento")
                }

                // Modificar y eliminar: solo el propietario
                if (puedeAdministrar) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = {
                                viewModel.cargar(producto)
                                navController.navigate("producto_form")
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Modificar")
                        }

                        OutlinedButton(
                            onClick = { mostrarDialogo = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Eliminar")
                        }
                    }
                }
            }

            item {
                HorizontalDivider()
                Text("Historial de movimientos", style = MaterialTheme.typography.titleMedium)
                if (historial.isEmpty()) Text("Sin movimientos registrados")
            }

            items(historial) { movimiento ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(10.dp)) {
                        Text("${movimiento.tipo}: ${movimiento.cantidad}", fontWeight = FontWeight.Bold)
                        Text(formatearFecha(movimiento.fecha))
                        Text("Existencia: ${movimiento.existenciaAnterior} -> ${movimiento.existenciaResultante}")
                        Text("Registró: ${movimiento.usuario}")
                        if (movimiento.motivo.isNotBlank()) Text("Motivo: ${movimiento.motivo}")
                    }
                }
            }
        }

        // Confirmación antes de eliminar
        if (mostrarDialogo) {
            AlertDialog(
                onDismissRequest = { mostrarDialogo = false },
                title = { Text("Eliminar producto") },
                text = { Text("¿Seguro que desea eliminar \"${producto.nombre}\"? Esta acción no se puede deshacer.") },
                confirmButton = {
                    TextButton(onClick = {
                        mostrarDialogo = false
                        viewModel.eliminar(producto)
                        navController.popBackStack()
                    }) {
                        Text("Eliminar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarDialogo = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}
