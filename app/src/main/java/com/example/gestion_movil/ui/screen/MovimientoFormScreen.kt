package com.example.gestion_movil.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gestion_movil.viewmodel.MovimientoViewModel
import com.example.gestion_movil.viewmodel.ProductoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovimientoFormScreen(
    navController: NavController,
    username: String,
    viewModel: MovimientoViewModel,
    productoViewModel: ProductoViewModel,
    tiposPermitidos: List<String> = viewModel.tipos     // El operador no ve Ajuste ni Devolución
) {
    val estado by viewModel.estado.collectAsState()
    val productos by productoViewModel.productos.collectAsState(initial = emptyList())
    val producto = productos.find { it.id == estado.productoId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar movimiento") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Cancelar")
                    }
                }
            )
        }
    ) { innerPadding ->

        if (producto == null) {
            Text("Producto no encontrado", modifier = Modifier.padding(innerPadding).padding(16.dp))
            return@Scaffold
        }

        Column(
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text(producto.nombre, style = MaterialTheme.typography.titleLarge)
            Text("Existencia actual: ${producto.cantidad} ${producto.unidadMedida}")

            // Tipo de movimiento (cada uno con su explicación en texto)
            Text("Tipo de movimiento", style = MaterialTheme.typography.titleSmall)
            tiposPermitidos.forEach { tipo ->
                Row(
                    // Toda la fila se puede tocar, no solo el círculo
                    modifier = Modifier.fillMaxWidth().clickable { viewModel.onTipoChange(tipo) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = estado.tipo == tipo,
                        onClick = { viewModel.onTipoChange(tipo) }
                    )
                    Column {
                        Text(tipo, fontWeight = FontWeight.Bold)
                        Text(viewModel.descripcionTipo(tipo), style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            // Campo cantidad
            OutlinedTextField(
                value = estado.cantidad,
                onValueChange = viewModel::onCantidadChange,
                label = { Text(if (estado.tipo == "Ajuste") "Cantidad contada (nueva existencia)" else "Cantidad") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = estado.errores.cantidad != null,
                supportingText = {
                    estado.errores.cantidad?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Vista previa de la existencia resultante
            estado.cantidad.toIntOrNull()?.let { cantidad ->
                val resultante = viewModel.calcularResultante(estado.tipo, producto.cantidad, cantidad)
                Text(
                    if (resultante < 0) "Existencia resultante: no alcanza el stock"
                    else "Existencia resultante: $resultante"
                )
            }

            // Campo motivo
            OutlinedTextField(
                value = estado.motivo,
                onValueChange = viewModel::onMotivoChange,
                label = { Text(if (estado.tipo == "Ajuste") "Motivo (obligatorio)" else "Motivo") },
                isError = estado.errores.motivo != null,
                supportingText = {
                    estado.errores.motivo?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Campo observación
            OutlinedTextField(
                value = estado.observacion,
                onValueChange = viewModel::onObservacionChange,
                label = { Text("Observación (opcional)") },
                modifier = Modifier.fillMaxWidth()
            )

            // Botón: registrar
            Button(
                onClick = {
                    if (viewModel.validarFormulario(producto.cantidad)) {
                        viewModel.guardar(username) {
                            navController.popBackStack()
                        }
                    }
                },
                enabled = !estado.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (estado.isLoading) "Registrando" else "Registrar")
            }
        }
    }
}
