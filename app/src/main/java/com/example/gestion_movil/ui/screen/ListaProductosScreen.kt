package com.example.gestion_movil.ui.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gestion_movil.viewmodel.ProductoViewModel
import com.example.gestion_movil.viewmodel.ProveedorViewModel
import com.example.gestion_movil.viewmodel.UbicacionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaProductosScreen(
    navController: NavController,
    username: String,
    viewModel: ProductoViewModel,
    proveedorViewModel: ProveedorViewModel,
    ubicacionViewModel: UbicacionViewModel,
    puedeAdministrar: Boolean = true        // false = operador (solo consulta)
) {
    val estado by viewModel.estado.collectAsState()
    val productos by viewModel.productos.collectAsState(initial = emptyList())
    val proveedores by proveedorViewModel.proveedores.collectAsState(initial = emptyList())
    val ubicaciones by ubicacionViewModel.ubicaciones.collectAsState(initial = emptyList())

    // La búsqueda y los filtros los resuelve el ViewModel
    val visibles = viewModel.filtrar(productos, estado)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Productos") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    // Leer el código QR de un producto con la cámara
                    IconButton(onClick = { navController.navigate("qr/$username") }) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = "Escanear QR de producto")
                    }
                }
            )
        },
        floatingActionButton = {
            // Solo el propietario registra productos
            if (puedeAdministrar) {
                FloatingActionButton(onClick = {
                    viewModel.nuevo()
                    navController.navigate("producto_form")
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Registrar producto")
                }
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            // Búsqueda por nombre o código
            OutlinedTextField(
                value = estado.busqueda,
                onValueChange = viewModel::onBusquedaChange,
                label = { Text("Buscar por nombre o código") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Filtro por categoría
            Text("Categoría", style = MaterialTheme.typography.labelLarge)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = estado.filtroCategoria == null,
                        onClick = { viewModel.onFiltroCategoriaChange(null) },
                        label = { Text("Todas") }
                    )
                }
                items(viewModel.categorias(productos)) { categoria ->
                    FilterChip(
                        selected = estado.filtroCategoria == categoria,
                        onClick = { viewModel.onFiltroCategoriaChange(categoria) },
                        label = { Text(categoria) }
                    )
                }
            }

            // Filtro por proveedor
            Text("Proveedor", style = MaterialTheme.typography.labelLarge)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = estado.filtroProveedorId == null,
                        onClick = { viewModel.onFiltroProveedorChange(null) },
                        label = { Text("Todos") }
                    )
                }
                items(proveedores) { proveedor ->
                    FilterChip(
                        selected = estado.filtroProveedorId == proveedor.id,
                        onClick = { viewModel.onFiltroProveedorChange(proveedor.id) },
                        label = { Text(proveedor.nombre) }
                    )
                }
            }

            if (visibles.isEmpty()) {
                Text(if (puedeAdministrar) "No hay productos para mostrar. Use + para registrar uno." else "No hay productos para mostrar.")
            }

            // Lista con datos de Room
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // key = id: así Compose sabe qué tarjeta es cuál y puede animarlas
                items(visibles, key = { it.id }) { producto ->
                    // Animación: la tarjeta cambia de color suavemente cuando la
                    // existencia queda en el mínimo o bajo él (y vuelve al normal al reponer)
                    val bajoMinimo = viewModel.estadoStock(producto) != "Disponible"
                    val colorTarjeta by animateColorAsState(
                        targetValue = if (bajoMinimo) MaterialTheme.colorScheme.errorContainer
                        else MaterialTheme.colorScheme.surfaceVariant,
                        animationSpec = tween(durationMillis = 600),
                        label = "colorStock"
                    )
                    Card(
                        colors = CardDefaults.cardColors(containerColor = colorTarjeta),
                        modifier = Modifier
                            // Animación: al buscar o filtrar, las tarjetas entran, salen
                            // y se reordenan suavemente en vez de saltar
                            .animateItem()
                            .fillMaxWidth()
                            .clickable {
                                viewModel.seleccionar(producto.id)
                                navController.navigate("producto_detalle/$username")
                            }
                    ) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(
                                    text = "${producto.nombre} (${producto.codigo})",
                                    fontWeight = FontWeight.Bold
                                )
                                Text("Existencia: ${producto.cantidad} ${producto.unidadMedida} - ${viewModel.estadoStock(producto)}")
                                Text("Categoría: ${producto.categoria}")
                                Text("Ubicación: ${ubicacionViewModel.texto(ubicacionViewModel.buscar(ubicaciones, producto.ubicacionId))}")
                            }
                            // Editar el producto directamente desde la lista (solo propietario)
                            if (puedeAdministrar) {
                                IconButton(onClick = {
                                    viewModel.cargar(producto)
                                    navController.navigate("producto_form")
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Modificar ${producto.nombre}")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
