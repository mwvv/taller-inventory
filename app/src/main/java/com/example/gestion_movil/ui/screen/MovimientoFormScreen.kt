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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gestion_movil.model.MovimientoUiState
import com.example.gestion_movil.model.data.local.ProductoEntity
import com.example.gestion_movil.ui.theme.GestionMovilTheme
import com.example.gestion_movil.viewmodel.MovimientoViewModel
import com.example.gestion_movil.viewmodel.ProductoViewModel

@Composable
fun MovimientoFormScreen(
    navController: NavController,
    username: String,
    viewModel: MovimientoViewModel,
    productoViewModel: ProductoViewModel,
    tiposPermitidos: List<String> = viewModel.tipos
) {
    val estado by viewModel.estado.collectAsState()
    val productos by productoViewModel.productos.collectAsState(initial = emptyList())
    val producto = productos.find { it.id == estado.productoId }

    MovimientoFormContent(
        estado = estado,
        producto = producto,
        tiposPermitidos = tiposPermitidos,
        descripcionTipo = { tipo -> viewModel.descripcionTipo(tipo) },
        calcularResultante = { tipo, actual, cantidad -> viewModel.calcularResultante(tipo, actual, cantidad) },
        onTipoChange = viewModel::onTipoChange,
        onCantidadChange = viewModel::onCantidadChange,
        onMotivoChange = viewModel::onMotivoChange,
        onObservacionChange = viewModel::onObservacionChange,
        onRegistrar = {
            if (producto != null && viewModel.validarFormulario(producto.cantidad)) {
                viewModel.guardar(username) { navController.popBackStack() }
            }
        },
        onBack = { navController.popBackStack() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovimientoFormContent(
    estado: MovimientoUiState,
    producto: ProductoEntity?,
    tiposPermitidos: List<String>,
    descripcionTipo: (String) -> String,
    calcularResultante: (String, Int, Int) -> Int,
    onTipoChange: (String) -> Unit,
    onCantidadChange: (String) -> Unit,
    onMotivoChange: (String) -> Unit,
    onObservacionChange: (String) -> Unit,
    onRegistrar: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar movimiento") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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

            Text("Tipo de movimiento", style = MaterialTheme.typography.titleSmall)
            tiposPermitidos.forEach { tipo ->
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { onTipoChange(tipo) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = estado.tipo == tipo,
                        onClick = { onTipoChange(tipo) }
                    )
                    Column {
                        Text(tipo, fontWeight = FontWeight.Bold)
                        Text(descripcionTipo(tipo), style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            OutlinedTextField(
                value = estado.cantidad,
                onValueChange = onCantidadChange,
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

            estado.cantidad.toIntOrNull()?.let { cantidad ->
                val resultante = calcularResultante(estado.tipo, producto.cantidad, cantidad)
                Text(
                    if (resultante < 0) "Existencia resultante: no alcanza el stock"
                    else "Existencia resultante: $resultante"
                )
            }

            OutlinedTextField(
                value = estado.motivo,
                onValueChange = onMotivoChange,
                label = { Text(if (estado.tipo == "Ajuste") "Motivo (obligatorio)" else "Motivo") },
                isError = estado.errores.motivo != null,
                supportingText = {
                    estado.errores.motivo?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = estado.observacion,
                onValueChange = onObservacionChange,
                label = { Text("Observación (opcional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = onRegistrar,
                enabled = !estado.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (estado.isLoading) "Registrando" else "Registrar")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MovimientoFormContentPreview() {
    val producto = ProductoEntity(
        id = 1, codigo = "AC-001", nombre = "Aceite 5W-30",
        descripcion = "Lubricante sintético", categoria = "Lubricantes", marca = "Genérico",
        unidadMedida = "unidades", cantidad = 8, existenciaMinima = 5, existenciaObjetivo = 10,
        precioCompra = 3500, precioVenta = 5990, estado = "Activo"
    )
    GestionMovilTheme {
        MovimientoFormContent(
            estado = MovimientoUiState(productoId = 1, tipo = "Venta", cantidad = "2", motivo = "Venta mostrador"),
            producto = producto,
            tiposPermitidos = listOf("Ingreso", "Venta", "Consumo en servicio", "Ajuste", "Devolución"),
            descripcionTipo = { tipo ->
                when (tipo) {
                    "Ingreso" -> "Entrada por compra o recepción"
                    "Venta" -> "Salida por venta al cliente"
                    else -> "Movimiento de inventario"
                }
            },
            calcularResultante = { _, actual, cantidad -> actual - cantidad },
            onTipoChange = {},
            onCantidadChange = {},
            onMotivoChange = {},
            onObservacionChange = {},
            onRegistrar = {},
            onBack = {}
        )
    }
}
