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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gestion_movil.model.data.local.ProveedorEntity
import com.example.gestion_movil.viewmodel.ProductoViewModel
import com.example.gestion_movil.viewmodel.ProveedorViewModel

@OptIn(ExperimentalMaterial3Api::class)
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
    val snackbarHostState = remember { SnackbarHostState() }

    // Muestra el resultado de la API REST una sola vez
    LaunchedEffect(mensajeApi) {
        mensajeApi?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.mensajeApiMostrado()
        }
    }

    // Proveedor que se quiere eliminar (para la confirmación)
    var aEliminar by remember { mutableStateOf<ProveedorEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Proveedores") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    // Consulta la API REST de proveedores (Retrofit)
                    if (sincronizando) {
                        CircularProgressIndicator(Modifier.padding(12.dp).size(24.dp), strokeWidth = 2.dp)
                    } else {
                        IconButton(onClick = { viewModel.sincronizarDesdeApi() }) {
                            Icon(Icons.Default.CloudDownload, contentDescription = "Sincronizar con API REST")
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.nuevo()
                navController.navigate("proveedor_form")
            }) {
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
                // Productos asociados a este proveedor
                val asociados = productos.filter { it.proveedorId == proveedor.id }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.cargar(proveedor)
                            navController.navigate("proveedor_form")
                        }
                ) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("${proveedor.nombre} (${proveedor.codigo})", fontWeight = FontWeight.Bold)
                            Text("Contacto: ${proveedor.contacto}")
                            Text("Correo: ${proveedor.correo}")
                            if (proveedor.telefono.isNotBlank()) Text("Teléfono: ${proveedor.telefono}")
                            Text("Suministra: ${proveedor.categorias}")
                            Text("Estado: " + if (proveedor.activo) "Activo" else "Inactivo")
                            Text("Productos asociados: ${asociados.size}")
                        }
                        IconButton(onClick = { aEliminar = proveedor }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar proveedor")
                        }
                    }
                }
            }
        }

        // Confirmación antes de eliminar
        aEliminar?.let { proveedor ->
            AlertDialog(
                onDismissRequest = { aEliminar = null },
                title = { Text("Eliminar proveedor") },
                text = { Text("¿Seguro que desea eliminar a ${proveedor.nombre}?") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.eliminar(proveedor)
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
