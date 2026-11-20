package com.example.gestion_movil.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gestion_movil.model.UbicacionUiState
import com.example.gestion_movil.ui.theme.GestionMovilTheme
import com.example.gestion_movil.viewmodel.UbicacionViewModel

@Composable
fun UbicacionFormScreen(
    navController: NavController,
    viewModel: UbicacionViewModel
) {
    val estado by viewModel.estado.collectAsState()

    UbicacionFormContent(
        estado = estado,
        estadosUbicacion = viewModel.estadosUbicacion,
        onCodigoChange = viewModel::onCodigoChange,
        onZonaChange = viewModel::onZonaChange,
        onEstanteChange = viewModel::onEstanteChange,
        onRepisaChange = viewModel::onRepisaChange,
        onPosicionChange = viewModel::onPosicionChange,
        onDescripcionChange = viewModel::onDescripcionChange,
        onEstadoChange = viewModel::onEstadoChange,
        onGuardar = {
            if (viewModel.validarFormulario()) {
                viewModel.guardar { navController.popBackStack() }
            }
        },
        onBack = { navController.popBackStack() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UbicacionFormContent(
    estado: UbicacionUiState,
    estadosUbicacion: List<String>,
    onCodigoChange: (String) -> Unit,
    onZonaChange: (String) -> Unit,
    onEstanteChange: (String) -> Unit,
    onRepisaChange: (String) -> Unit,
    onPosicionChange: (String) -> Unit,
    onDescripcionChange: (String) -> Unit,
    onEstadoChange: (String) -> Unit,
    onGuardar: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (estado.id == 0L) "Registrar ubicación" else "Actualizar ubicación") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Cancelar")
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            OutlinedTextField(
                value = estado.codigo,
                onValueChange = onCodigoChange,
                label = { Text("Código (ej: A-2-3)") },
                isError = estado.errores.codigo != null,
                supportingText = {
                    estado.errores.codigo?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = estado.zona,
                onValueChange = onZonaChange,
                label = { Text("Zona o sector (ej: Bodega A)") },
                isError = estado.errores.zona != null,
                supportingText = {
                    estado.errores.zona?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = estado.estante,
                onValueChange = onEstanteChange,
                label = { Text("Estante") },
                isError = estado.errores.estante != null,
                supportingText = {
                    estado.errores.estante?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = estado.repisa,
                onValueChange = onRepisaChange,
                label = { Text("Repisa") },
                isError = estado.errores.repisa != null,
                supportingText = {
                    estado.errores.repisa?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = estado.posicion,
                onValueChange = onPosicionChange,
                label = { Text("Contenedor o posición (ej: izquierda)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = estado.descripcion,
                onValueChange = onDescripcionChange,
                label = { Text("Descripción de referencia") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Estado", style = MaterialTheme.typography.titleSmall)
            estadosUbicacion.forEach { opcion ->
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { onEstadoChange(opcion) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = estado.estado == opcion,
                        onClick = { onEstadoChange(opcion) }
                    )
                    Text(opcion)
                }
            }

            Button(
                onClick = onGuardar,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UbicacionFormContentPreview() {
    GestionMovilTheme {
        UbicacionFormContent(
            estado = UbicacionUiState(
                codigo = "A-2-3",
                zona = "Bodega A",
                estante = "2",
                repisa = "3",
                posicion = "Izquierda",
                descripcion = "Lubricantes y filtros",
                estado = "Disponible"
            ),
            estadosUbicacion = listOf("Disponible", "Ocupada", "En reorganización", "Fuera de uso"),
            onCodigoChange = {},
            onZonaChange = {},
            onEstanteChange = {},
            onRepisaChange = {},
            onPosicionChange = {},
            onDescripcionChange = {},
            onEstadoChange = {},
            onGuardar = {},
            onBack = {}
        )
    }
}
