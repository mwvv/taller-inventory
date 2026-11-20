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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gestion_movil.model.ProductoUiState
import com.example.gestion_movil.model.data.local.ProductoEntity
import com.example.gestion_movil.model.data.local.ProveedorEntity
import com.example.gestion_movil.ui.theme.GestionMovilTheme
import com.example.gestion_movil.viewmodel.ProductoViewModel
import com.example.gestion_movil.viewmodel.ProveedorViewModel
import com.example.gestion_movil.viewmodel.UbicacionViewModel

@Composable
fun ListaProductosScreen(
    navController: NavController,
    username: String,
    viewModel: ProductoViewModel,
    proveedorViewModel: ProveedorViewModel,
    ubicacionViewModel: UbicacionViewModel,
    puedeAdministrar: Boolean = true
) {
    val estado by viewModel.estado.collectAsState()
    val productos by viewModel.productos.collectAsState(initial = emptyList())
    val proveedores by proveedorViewModel.proveedores.collectAsState(initial = emptyList())
    val ubicaciones by ubicacionViewModel.ubicaciones.collectAsState(initial = emptyList())

    val visibles = viewModel.filtrar(productos, estado)

    ListaProductosContent(
        estado = estado,
        visibles = visibles,
        categorias = viewModel.categorias(productos),
        proveedores = proveedores,
        puedeAdministrar = puedeAdministrar,
        estadoStock = { producto -> viewModel.estadoStock(producto) },
        textoUbicacion = { ubicacionId ->
            ubicacionViewModel.texto(ubicacionViewModel.buscar(ubicaciones, ubicacionId))
        },
        onBusquedaChange = viewModel::onBusquedaChange,
        onFiltroCategoria = viewModel::onFiltroCategoriaChange,
        onFiltroProveedor = viewModel::onFiltroProveedorChange,
        onEscanearQr = { navController.navigate("qr/$username") },
        onNuevo = {
            viewModel.nuevo()
            navController.navigate("producto_form")
        },
        onSeleccionar = { producto ->
            viewModel.seleccionar(producto.id)
            navController.navigate("producto_detalle/$username")
        },
        onEditar = { producto ->
            viewModel.cargar(producto)
            navController.navigate("producto_form")
        },
        onBack = { navController.popBackStack() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaProductosContent(
    estado: ProductoUiState,
    visibles: List<ProductoEntity>,
    categorias: List<String>,
    proveedores: List<ProveedorEntity>,
    puedeAdministrar: Boolean,
    estadoStock: (ProductoEntity) -> String,
    textoUbicacion: (Long?) -> String,
    onBusquedaChange: (String) -> Unit,
    onFiltroCategoria: (String?) -> Unit,
    onFiltroProveedor: (Long?) -> Unit,
    onEscanearQr: () -> Unit,
    onNuevo: () -> Unit,
    onSeleccionar: (ProductoEntity) -> Unit,
    onEditar: (ProductoEntity) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Productos") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = onEscanearQr) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = "Escanear QR de producto")
                    }
                }
            )
        },
        floatingActionButton = {
            if (puedeAdministrar) {
                FloatingActionButton(onClick = onNuevo) {
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

            OutlinedTextField(
                value = estado.busqueda,
                onValueChange = onBusquedaChange,
                label = { Text("Buscar por nombre o código") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Text("Categoría", style = MaterialTheme.typography.labelLarge)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = estado.filtroCategoria == null,
                        onClick = { onFiltroCategoria(null) },
                        label = { Text("Todas") }
                    )
                }
                items(categorias) { categoria ->
                    FilterChip(
                        selected = estado.filtroCategoria == categoria,
                        onClick = { onFiltroCategoria(categoria) },
                        label = { Text(categoria) }
                    )
                }
            }

            Text("Proveedor", style = MaterialTheme.typography.labelLarge)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = estado.filtroProveedorId == null,
                        onClick = { onFiltroProveedor(null) },
                        label = { Text("Todos") }
                    )
                }
                items(proveedores) { proveedor ->
                    FilterChip(
                        selected = estado.filtroProveedorId == proveedor.id,
                        onClick = { onFiltroProveedor(proveedor.id) },
                        label = { Text(proveedor.nombre) }
                    )
                }
            }

            if (visibles.isEmpty()) {
                Text(if (puedeAdministrar) "No hay productos para mostrar. Use + para registrar uno." else "No hay productos para mostrar.")
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(visibles, key = { it.id }) { producto ->
                    val bajoMinimo = estadoStock(producto) != "Disponible"
                    val colorTarjeta by animateColorAsState(
                        targetValue = if (bajoMinimo) MaterialTheme.colorScheme.errorContainer
                        else MaterialTheme.colorScheme.surfaceVariant,
                        animationSpec = tween(durationMillis = 600),
                        label = "colorStock"
                    )
                    Card(
                        colors = CardDefaults.cardColors(containerColor = colorTarjeta),
                        modifier = Modifier
                            .animateItem()
                            .fillMaxWidth()
                            .clickable { onSeleccionar(producto) }
                    ) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(
                                    text = "${producto.nombre} (${producto.codigo})",
                                    fontWeight = FontWeight.Bold
                                )
                                Text("Existencia: ${producto.cantidad} ${producto.unidadMedida} - ${estadoStock(producto)}")
                                Text("Categoría: ${producto.categoria}")
                                Text("Ubicación: ${textoUbicacion(producto.ubicacionId)}")
                            }
                            if (puedeAdministrar) {
                                IconButton(onClick = { onEditar(producto) }) {
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

@Preview(showBackground = true)
@Composable
fun ListaProductosContentPreview() {
    val productos = listOf(
        ProductoEntity(
            id = 1, codigo = "AC-001", nombre = "Aceite 5W-30",
            descripcion = "", categoria = "Lubricantes", marca = "Genérico",
            unidadMedida = "unidades", cantidad = 8, existenciaMinima = 5, existenciaObjetivo = 10,
            precioCompra = 3500, precioVenta = 5990, estado = "Activo", ubicacionId = 1
        ),
        ProductoEntity(
            id = 2, codigo = "FA-010", nombre = "Filtro de aire",
            descripcion = "", categoria = "Filtros", marca = "Genérico",
            unidadMedida = "unidades", cantidad = 2, existenciaMinima = 5, existenciaObjetivo = 12,
            precioCompra = 2500, precioVenta = 4500, estado = "Activo", ubicacionId = 2
        )
    )
    GestionMovilTheme {
        ListaProductosContent(
            estado = ProductoUiState(),
            visibles = productos,
            categorias = listOf("Lubricantes", "Filtros"),
            proveedores = listOf(
                ProveedorEntity(1, "PR-001", "Repuestos Andes", "Juan", "a@b.cl", "", "Filtros", true, "")
            ),
            puedeAdministrar = true,
            estadoStock = { producto -> if (producto.cantidad <= producto.existenciaMinima) "Bajo el mínimo" else "Disponible" },
            textoUbicacion = { if (it == 1L) "Bodega A · Estante 2" else "Bodega A · Estante 3" },
            onBusquedaChange = {},
            onFiltroCategoria = {},
            onFiltroProveedor = {},
            onEscanearQr = {},
            onNuevo = {},
            onSeleccionar = {},
            onEditar = {},
            onBack = {}
        )
    }
}
