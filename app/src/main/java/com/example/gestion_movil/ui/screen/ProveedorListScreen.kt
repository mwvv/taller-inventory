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
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gestion_movil.model.data.local.ProveedorEntity
import com.example.gestion_movil.ui.theme.GestionMovilTheme
import com.example.gestion_movil.viewmodel.ProductoViewModel
import com.example.gestion_movil.viewmodel.ProveedorViewModel

@Composable
fun ProveedorListScreen(
    navController: NavController,
    viewModel: ProveedorViewModel,
    productoViewModel: ProductoViewModel
) {
    val proveedores by viewModel.proveedores.collectAsState(initial = emptyList())
    val productos by productoViewModel.productos.collectAsState(initial = emptyList())
    val sincronizando by viewModel.sincronizando.collectAsState()
    val mensajeApi by viewModel.mensajeApi.collectAsState()

    ProveedorListContent(
        proveedores = proveedores,
        contarAsociados = { proveedor -> productos.count { it.proveedorId == proveedor.id } },
        sincronizando = sincronizando,
        mensajeApi = mensajeApi,
        onMensajeApiMostrado = viewModel::mensajeApiMostrado,
        onSincronizar = viewModel::sincronizarDesdeApi,
        onNuevo = {
            viewModel.nuevo()
            navController.navigate("proveedor_form")
        },
        onEditar = { proveedor ->
            viewModel.cargar(proveedor)
            navController.navigate("proveedor_form")
        },
        onEliminar = viewModel::eliminar,
        onBack = { navController.popBackStack() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProveedorListContent(
    proveedores: List<ProveedorEntity>,
    contarAsociados: (ProveedorEntity) -> Int,
    sincronizando: Boolean,
    mensajeApi: String?,
    onMensajeApiMostrado: () -> Unit,
    onSincronizar: () -> Unit,
    onNuevo: () -> Unit,
    onEditar: (ProveedorEntity) -> Unit,
    onEliminar: (ProveedorEntity) -> Unit,
    onBack: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(mensajeApi) {
        mensajeApi?.let {
            snackbarHostState.showSnackbar(it)
            onMensajeApiMostrado()
        }
    }

    var aEliminar by remember { mutableStateOf<ProveedorEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Proveedores") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (sincronizando) {
                        CircularProgressIndicator(Modifier.padding(12.dp).size(24.dp), strokeWidth = 2.dp)
                    } else {
                        IconButton(onClick = onSincronizar) {
                            Icon(Icons.Default.CloudDownload, contentDescription = "Sincronizar con API REST")
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = onNuevo) {
                Icon(Icons.Default.Add, contentDescription = "Registrar proveedor")
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
            if (proveedores.isEmpty()) {
                item { Text("No hay proveedores. Use + para registrar uno (datos ficticios).") }
            }

            items(proveedores) { proveedor ->
                val asociados = contarAsociados(proveedor)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onEditar(proveedor) }
                ) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("${proveedor.nombre} (${proveedor.codigo})", fontWeight = FontWeight.Bold)
                            Text("Contacto: ${proveedor.contacto}")
                            Text("Correo: ${proveedor.correo}")
                            if (proveedor.telefono.isNotBlank()) Text("Teléfono: ${proveedor.telefono}")
                            Text("Suministra: ${proveedor.categorias}")
                            Text("Estado: " + if (proveedor.activo) "Activo" else "Inactivo")
                            Text("Productos asociados: $asociados")
                        }
                        IconButton(onClick = { aEliminar = proveedor }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar proveedor")
                        }
                    }
                }
            }
        }

        aEliminar?.let { proveedor ->
            AlertDialog(
                onDismissRequest = { aEliminar = null },
                title = { Text("Eliminar proveedor") },
                text = { Text("¿Seguro que desea eliminar a ${proveedor.nombre}?") },
                confirmButton = {
                    TextButton(onClick = {
                        onEliminar(proveedor)
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

@Preview(showBackground = true)
@Composable
fun ProveedorListContentPreview() {
    GestionMovilTheme {
        ProveedorListContent(
            proveedores = listOf(
                ProveedorEntity(
                    id = 1, codigo = "PR-001", nombre = "Repuestos Andes",
                    contacto = "Juan Pérez", correo = "ventas@andes-ficticio.cl",
                    telefono = "+56 9 1234 5678", categorias = "Filtros, Frenos",
                    activo = true, observaciones = ""
                ),
                ProveedorEntity(
                    id = 2, codigo = "PR-002", nombre = "Lubricantes Sur",
                    contacto = "María Soto", correo = "contacto@lubsur-ficticio.cl",
                    telefono = "", categorias = "Lubricantes",
                    activo = false, observaciones = ""
                )
            ),
            contarAsociados = { 3 },
            sincronizando = false,
            mensajeApi = null,
            onMensajeApiMostrado = {},
            onSincronizar = {},
            onNuevo = {},
            onEditar = {},
            onEliminar = {},
            onBack = {}
        )
    }
}
