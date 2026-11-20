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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gestion_movil.model.data.local.ProductoEntity
import com.example.gestion_movil.model.data.local.ProveedorEntity
import com.example.gestion_movil.model.formatearFecha
import com.example.gestion_movil.ui.theme.GestionMovilTheme
import com.example.gestion_movil.viewmodel.ProductoViewModel
import com.example.gestion_movil.viewmodel.ProveedorViewModel
import com.example.gestion_movil.viewmodel.ReposicionViewModel

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

    val grupos = viewModel.agruparPorProveedor(productos)

    ReposicionContent(
        grupos = grupos,
        proveedores = proveedores,
        estadoSolicitud = { proveedorId -> viewModel.estadoSolicitud(solicitudes, proveedorId) },
        cantidadSugerida = { producto -> viewModel.cantidadSugerida(producto) },
        estadoStock = { producto -> productoViewModel.estadoStock(producto) },
        onCambiarEstado = { proveedorId -> viewModel.cambiarEstado(proveedorId) },
        onPrepararCorreo = { proveedorId ->
            viewModel.prepararCorreo(proveedorId)
            navController.navigate("correo")
        },
        onBack = { navController.popBackStack() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReposicionContent(
    grupos: Map<Long?, List<ProductoEntity>>,
    proveedores: List<ProveedorEntity>,
    estadoSolicitud: (Long) -> String,
    cantidadSugerida: (ProductoEntity) -> Int,
    estadoStock: (ProductoEntity) -> String,
    onCambiarEstado: (Long) -> Unit,
    onPrepararCorreo: (Long) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reposición") },
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
                        Text("Estado de la solicitud: ${estadoSolicitud(proveedor.id)}")
                    }
                }

                items(lista) { producto ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(10.dp)) {
                            Text("${producto.nombre} (${producto.codigo})", fontWeight = FontWeight.Bold)
                            Text("Disponible: ${producto.cantidad} - ${estadoStock(producto)}")
                            Text("Existencia mínima: ${producto.existenciaMinima}")
                            Text("Cantidad sugerida: ${cantidadSugerida(producto)} ${producto.unidadMedida}")
                        }
                    }
                }

                if (proveedor != null) {
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { onCambiarEstado(proveedor.id) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cambiar estado")
                            }
                            Button(
                                onClick = { onPrepararCorreo(proveedor.id) },
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

@Preview(showBackground = true)
@Composable
fun ReposicionContentPreview() {
    val proveedor = ProveedorEntity(
        id = 1, codigo = "PR-001", nombre = "Repuestos Andes",
        contacto = "Juan Pérez", correo = "ventas@andes-ficticio.cl",
        telefono = "+56 9 1234 5678", categorias = "Filtros, Frenos",
        activo = true, observaciones = ""
    )
    val producto = ProductoEntity(
        id = 1, codigo = "AC-001", nombre = "Aceite 5W-30",
        descripcion = "Lubricante sintético", categoria = "Lubricantes", marca = "Genérico",
        unidadMedida = "unidades", cantidad = 2, existenciaMinima = 5, existenciaObjetivo = 10,
        precioCompra = 3500, precioVenta = 5990, estado = "Activo", proveedorId = 1
    )
    GestionMovilTheme {
        ReposicionContent(
            grupos = mapOf(1L to listOf(producto)),
            proveedores = listOf(proveedor),
            estadoSolicitud = { "Pendiente" },
            cantidadSugerida = { 8 },
            estadoStock = { "Bajo el mínimo" },
            onCambiarEstado = {},
            onPrepararCorreo = {},
            onBack = {}
        )
    }
}
