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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gestion_movil.model.data.local.UbicacionEntity
import com.example.gestion_movil.model.formatearFecha
import com.example.gestion_movil.ui.theme.GestionMovilTheme
import com.example.gestion_movil.viewmodel.ProductoViewModel
import com.example.gestion_movil.viewmodel.UbicacionViewModel

@Composable
fun UbicacionListScreen(
    navController: NavController,
    viewModel: UbicacionViewModel,
    productoViewModel: ProductoViewModel,
    puedeAdministrar: Boolean = true
) {
    val ubicaciones by viewModel.ubicaciones.collectAsState(initial = emptyList())
    val productos by productoViewModel.productos.collectAsState(initial = emptyList())

    UbicacionListContent(
        ubicaciones = ubicaciones,
        puedeAdministrar = puedeAdministrar,
        textoUbicacion = { ubicacion -> viewModel.texto(ubicacion) },
        productosEnUbicacion = { ubicacion ->
            productos.filter { it.ubicacionId == ubicacion.id }.map { it.nombre }
        },
        onNuevo = {
            viewModel.nuevo()
            navController.navigate("ubicacion_form")
        },
        onEditar = { ubicacion ->
            viewModel.cargar(ubicacion)
            navController.navigate("ubicacion_form")
        },
        onEliminar = viewModel::eliminar,
        onBack = { navController.popBackStack() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UbicacionListContent(
    ubicaciones: List<UbicacionEntity>,
    puedeAdministrar: Boolean,
    textoUbicacion: (UbicacionEntity) -> String,
    productosEnUbicacion: (UbicacionEntity) -> List<String>,
    onNuevo: () -> Unit,
    onEditar: (UbicacionEntity) -> Unit,
    onEliminar: (UbicacionEntity) -> Unit,
    onBack: () -> Unit
) {
    var aEliminar by remember { mutableStateOf<UbicacionEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ubicaciones") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            if (puedeAdministrar) {
                FloatingActionButton(onClick = onNuevo) {
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
                val enUbicacion = productosEnUbicacion(ubicacion)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = puedeAdministrar) { onEditar(ubicacion) }
                ) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(ubicacion.codigo, fontWeight = FontWeight.Bold)
                            Text(textoUbicacion(ubicacion))
                            Text("Estado: ${ubicacion.estado}")
                            Text("Actualizada: ${formatearFecha(ubicacion.fechaActualizacion)}")
                            Text("Productos: " + if (enUbicacion.isEmpty()) "ninguno" else enUbicacion.joinToString())
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

        aEliminar?.let { ubicacion ->
            AlertDialog(
                onDismissRequest = { aEliminar = null },
                title = { Text("Eliminar ubicación") },
                text = { Text("¿Seguro que desea eliminar la ubicación ${ubicacion.codigo}?") },
                confirmButton = {
                    TextButton(onClick = {
                        onEliminar(ubicacion)
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
fun UbicacionListContentPreview() {
    val ubicacion = UbicacionEntity(
        id = 1, codigo = "A-2-3", zona = "Bodega A", estante = "2", repisa = "3",
        posicion = "Izquierda", descripcion = "Lubricantes", estado = "Disponible",
        fechaActualizacion = 1_700_000_000_000
    )
    GestionMovilTheme {
        UbicacionListContent(
            ubicaciones = listOf(ubicacion),
            puedeAdministrar = true,
            textoUbicacion = { "Bodega A · Estante 2 · Repisa 3" },
            productosEnUbicacion = { listOf("Aceite 5W-30", "Filtro de aire") },
            onNuevo = {},
            onEditar = {},
            onEliminar = {},
            onBack = {}
        )
    }
}
