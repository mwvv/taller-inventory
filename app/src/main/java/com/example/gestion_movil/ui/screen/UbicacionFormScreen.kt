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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gestion_movil.viewmodel.UbicacionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UbicacionFormScreen(
    navController: NavController,
    viewModel: UbicacionViewModel
) {
    val estado by viewModel.estado.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (estado.id == 0L) "Registrar ubicación" else "Actualizar ubicación") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
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

            // Campo código
            OutlinedTextField(
                value = estado.codigo,
                onValueChange = viewModel::onCodigoChange,
                label = { Text("Código (ej: A-2-3)") },
                isError = estado.errores.codigo != null,
                supportingText = {
                    estado.errores.codigo?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Campo zona
            OutlinedTextField(
                value = estado.zona,
                onValueChange = viewModel::onZonaChange,
                label = { Text("Zona o sector (ej: Bodega A)") },
                isError = estado.errores.zona != null,
                supportingText = {
                    estado.errores.zona?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Campo estante
            OutlinedTextField(
                value = estado.estante,
                onValueChange = viewModel::onEstanteChange,
                label = { Text("Estante") },
                isError = estado.errores.estante != null,
                supportingText = {
                    estado.errores.estante?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Campo repisa
            OutlinedTextField(
                value = estado.repisa,
                onValueChange = viewModel::onRepisaChange,
                label = { Text("Repisa") },
                isError = estado.errores.repisa != null,
                supportingText = {
                    estado.errores.repisa?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Campo posición
            OutlinedTextField(
                value = estado.posicion,
                onValueChange = viewModel::onPosicionChange,
                label = { Text("Contenedor o posición (ej: izquierda)") },
                modifier = Modifier.fillMaxWidth()
            )

            // Campo descripción
            OutlinedTextField(
                value = estado.descripcion,
                onValueChange = viewModel::onDescripcionChange,
                label = { Text("Descripción de referencia") },
                modifier = Modifier.fillMaxWidth()
            )

            // Estado de la ubicación
            Text("Estado", style = MaterialTheme.typography.titleSmall)
            viewModel.estadosUbicacion.forEach { opcion ->
                Row(
                    // Toda la fila se puede tocar, no solo el círculo
                    modifier = Modifier.fillMaxWidth().clickable { viewModel.onEstadoChange(opcion) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = estado.estado == opcion,
                        onClick = { viewModel.onEstadoChange(opcion) }
                    )
                    Text(opcion)
                }
            }

            // Botón: guardar
            Button(
                onClick = {
                    if (viewModel.validarFormulario()) {
                        viewModel.guardar {
                            navController.popBackStack()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar")
            }
        }
    }
}
