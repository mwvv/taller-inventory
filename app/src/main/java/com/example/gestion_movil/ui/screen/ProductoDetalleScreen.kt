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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gestion_movil.model.data.local.MovimientoEntity
import com.example.gestion_movil.model.data.local.ProductoEntity
import com.example.gestion_movil.model.formatearFecha
import com.example.gestion_movil.ui.theme.GestionMovilTheme
import com.example.gestion_movil.viewmodel.MovimientoViewModel
import com.example.gestion_movil.viewmodel.ProductoViewModel
import com.example.gestion_movil.viewmodel.ProveedorViewModel
import com.example.gestion_movil.viewmodel.UbicacionViewModel

@Composable
fun ProductoDetalleScreen(
    navController: NavController,
    username: String,
    viewModel: ProductoViewModel,
    movimientoViewModel: MovimientoViewModel,
    proveedorViewModel: ProveedorViewModel,
    ubicacionViewModel: UbicacionViewModel,
    puedeAdministrar: Boolean = true,
    puedeVerPrecios: Boolean = true
) {
    val estado by viewModel.estado.collectAsState()
    val productos by viewModel.productos.collectAsState(initial = emptyList())
    val movimientos by movimientoViewModel.movimientos.collectAsState(initial = emptyList())
    val proveedores by proveedorViewModel.proveedores.collectAsState(initial = emptyList())
    val ubicaciones by ubicacionViewModel.ubicaciones.collectAsState(initial = emptyList())

    val producto = productos.find { it.id == estado.seleccionadoId }
    val historial = if (producto != null) {
        movimientoViewModel.filtrarPorProducto(movimientos, producto.id)
    } else {
        emptyList()
    }

    ProductoDetalleContent(
        producto = producto,
        historial = historial,
        puedeAdministrar = puedeAdministrar,
        puedeVerPrecios = puedeVerPrecios,
        estadoStock = { viewModel.estadoStock(it) },
        nombreProveedor = { proveedorViewModel.nombre(proveedores, it) },
        textoUbicacion = { ubicacionViewModel.texto(ubicacionViewModel.buscar(ubicaciones, it)) },
        onRegistrarMovimiento = {
            if (producto != null) {
                movimientoViewModel.iniciar(producto.id)
                navController.navigate("movimiento/$username")
            }
        },
        onModificar = {
            if (producto != null) {
                viewModel.cargar(producto)
                navController.navigate("producto_form")
            }
        },
        onEliminar = {
            if (producto != null) {
                viewModel.eliminar(producto)
                navController.popBackStack()
            }
        },
        onBack = { navController.popBackStack() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoDetalleContent(
    producto: ProductoEntity?,
    historial: List<MovimientoEntity>,
    puedeAdministrar: Boolean,
    puedeVerPrecios: Boolean,
    estadoStock: (ProductoEntity) -> String,
    nombreProveedor: (Long?) -> String,
    textoUbicacion: (Long?) -> String,
    onRegistrarMovimiento: () -> Unit,
    onModificar: () -> Unit,
    onEliminar: () -> Unit,
    onBack: () -> Unit
) {
    var mostrarDialogo by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del producto") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
                    text = "Existencia: ${producto.cantidad} ${producto.unidadMedida} - ${estadoStock(producto)}",
                    fontWeight = FontWeight.Bold
                )
                Text("Existencia mínima: ${producto.existenciaMinima}   Objetivo: ${producto.existenciaObjetivo}")
                if (puedeVerPrecios) {
                    Text("Precio compra: $${producto.precioCompra}   Precio venta: $${producto.precioVenta}")
                }
                Text("Estado: ${producto.estado}")
                Text("Proveedor: ${nombreProveedor(producto.proveedorId)}")
                Text("Ubicación: ${textoUbicacion(producto.ubicacionId)}")
                Text("Cambio de ubicación: ${formatearFecha(producto.fechaCambioUbicacion)}")
                Text("Último ingreso: ${formatearFecha(producto.fechaUltimoIngreso)}")
                Text("Última salida: ${formatearFecha(producto.fechaUltimaSalida)}")
            }

            item {
                Button(
                    onClick = onRegistrarMovimiento,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Registrar movimiento")
                }

                if (puedeAdministrar) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = onModificar,
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

        if (mostrarDialogo) {
            AlertDialog(
                onDismissRequest = { mostrarDialogo = false },
                title = { Text("Eliminar producto") },
                text = { Text("¿Seguro que desea eliminar \"${producto.nombre}\"? Esta acción no se puede deshacer.") },
                confirmButton = {
                    TextButton(onClick = {
                        mostrarDialogo = false
                        onEliminar()
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

@Preview(showBackground = true)
@Composable
fun ProductoDetalleContentPreview() {
    val producto = ProductoEntity(
        id = 1, codigo = "AC-001", nombre = "Aceite 5W-30",
        descripcion = "Lubricante sintético para motor", categoria = "Lubricantes", marca = "Genérico",
        unidadMedida = "unidades", cantidad = 8, existenciaMinima = 5, existenciaObjetivo = 10,
        precioCompra = 3500, precioVenta = 5990, estado = "Activo",
        fechaUltimoIngreso = 1_700_000_000_000, proveedorId = 1, ubicacionId = 1
    )
    GestionMovilTheme {
        ProductoDetalleContent(
            producto = producto,
            historial = listOf(
                MovimientoEntity(
                    id = 1, productoId = 1, productoNombre = "Aceite 5W-30",
                    tipo = "Ingreso", cantidad = 10, fecha = 1_700_000_000_000,
                    motivo = "Compra", observacion = "",
                    existenciaAnterior = 0, existenciaResultante = 10, usuario = "admin"
                )
            ),
            puedeAdministrar = true,
            puedeVerPrecios = true,
            estadoStock = { "Disponible" },
            nombreProveedor = { "Repuestos Andes" },
            textoUbicacion = { "Bodega A · Estante 2 · Repisa 3" },
            onRegistrarMovimiento = {},
            onModificar = {},
            onEliminar = {},
            onBack = {}
        )
    }
}
