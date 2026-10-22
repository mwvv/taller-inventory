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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gestion_movil.viewmodel.ProductoViewModel
import com.example.gestion_movil.viewmodel.ProveedorViewModel
import com.example.gestion_movil.viewmodel.UbicacionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoFormScreen(
    navController: NavController,
    viewModel: ProductoViewModel,
    proveedorViewModel: ProveedorViewModel,
    ubicacionViewModel: UbicacionViewModel
) {
    val estado by viewModel.estado.collectAsState()
    val proveedores by proveedorViewModel.proveedores.collectAsState(initial = emptyList())
    val ubicaciones by ubicacionViewModel.ubicaciones.collectAsState(initial = emptyList())
    val esNuevo = estado.id == 0L
    val numeros = KeyboardOptions(keyboardType = KeyboardType.Number)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (esNuevo) "Registrar producto" else "Modificar producto") },
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
                label = { Text("Código interno") },
                isError = estado.errores.codigo != null,
                supportingText = {
                    estado.errores.codigo?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Campo nombre
            OutlinedTextField(
                value = estado.nombre,
                onValueChange = viewModel::onNombreChange,
                label = { Text("Nombre") },
                isError = estado.errores.nombre != null,
                supportingText = {
                    estado.errores.nombre?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Campo descripción
            OutlinedTextField(
                value = estado.descripcion,
                onValueChange = viewModel::onDescripcionChange,
                label = { Text("Descripción breve") },
                modifier = Modifier.fillMaxWidth()
            )

            // Campo categoría
            OutlinedTextField(
                value = estado.categoria,
                onValueChange = viewModel::onCategoriaChange,
                label = { Text("Categoría (ej: Lubricantes, Filtros)") },
                isError = estado.errores.categoria != null,
                supportingText = {
                    estado.errores.categoria?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Campo marca
            OutlinedTextField(
                value = estado.marca,
                onValueChange = viewModel::onMarcaChange,
                label = { Text("Marca") },
                modifier = Modifier.fillMaxWidth()
            )

            // Campo unidad de medida
            OutlinedTextField(
                value = estado.unidadMedida,
                onValueChange = viewModel::onUnidadMedidaChange,
                label = { Text("Unidad de medida (ej: Unidad, Litro)") },
                modifier = Modifier.fillMaxWidth()
            )

            // Campo cantidad: solo al crear, después cambia con movimientos
            OutlinedTextField(
                value = estado.cantidad,
                onValueChange = viewModel::onCantidadChange,
                label = { Text(if (esNuevo) "Cantidad inicial" else "Cantidad disponible (se cambia con movimientos)") },
                enabled = esNuevo,
                keyboardOptions = numeros,
                isError = estado.errores.cantidad != null,
                supportingText = {
                    estado.errores.cantidad?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Campo existencia mínima
            OutlinedTextField(
                value = estado.existenciaMinima,
                onValueChange = viewModel::onExistenciaMinimaChange,
                label = { Text("Existencia mínima") },
                keyboardOptions = numeros,
                isError = estado.errores.existenciaMinima != null,
                supportingText = {
                    estado.errores.existenciaMinima?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Campo existencia objetivo
            OutlinedTextField(
                value = estado.existenciaObjetivo,
                onValueChange = viewModel::onExistenciaObjetivoChange,
                label = { Text("Existencia objetivo (para reponer)") },
                keyboardOptions = numeros,
                isError = estado.errores.existenciaObjetivo != null,
                supportingText = {
                    estado.errores.existenciaObjetivo?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Campo precio de compra
            OutlinedTextField(
                value = estado.precioCompra,
                onValueChange = viewModel::onPrecioCompraChange,
                label = { Text("Precio de compra referencial") },
                keyboardOptions = numeros,
                isError = estado.errores.precioCompra != null,
                supportingText = {
                    estado.errores.precioCompra?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Campo precio de venta
            OutlinedTextField(
                value = estado.precioVenta,
                onValueChange = viewModel::onPrecioVentaChange,
                label = { Text("Precio de venta referencial") },
                keyboardOptions = numeros,
                isError = estado.errores.precioVenta != null,
                supportingText = {
                    estado.errores.precioVenta?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Estado del producto
            Text("Estado", style = MaterialTheme.typography.titleSmall)
            viewModel.estadosProducto.forEach { opcion ->
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

            // Proveedor principal
            Text("Proveedor principal", style = MaterialTheme.typography.titleSmall)
            Row(
                    // Toda la fila se puede tocar, no solo el círculo
                    modifier = Modifier.fillMaxWidth().clickable { viewModel.onProveedorChange(null) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                RadioButton(
                    selected = estado.proveedorId == null,
                    onClick = { viewModel.onProveedorChange(null) }
                )
                Text("Sin proveedor")
            }
            proveedores.forEach { proveedor ->
                Row(
                    // Toda la fila se puede tocar, no solo el círculo
                    modifier = Modifier.fillMaxWidth().clickable { viewModel.onProveedorChange(proveedor.id) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = estado.proveedorId == proveedor.id,
                        onClick = { viewModel.onProveedorChange(proveedor.id) }
                    )
                    Text(proveedor.nombre)
                }
            }

            // Ubicación en la bodega
            Text("Ubicación en la bodega", style = MaterialTheme.typography.titleSmall)
            Row(
                    // Toda la fila se puede tocar, no solo el círculo
                    modifier = Modifier.fillMaxWidth().clickable { viewModel.onUbicacionChange(null) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                RadioButton(
                    selected = estado.ubicacionId == null,
                    onClick = { viewModel.onUbicacionChange(null) }
                )
                Text("Sin ubicación")
            }
            ubicaciones.forEach { ubicacion ->
                Row(
                    // Toda la fila se puede tocar, no solo el círculo
                    modifier = Modifier.fillMaxWidth().clickable { viewModel.onUbicacionChange(ubicacion.id) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = estado.ubicacionId == ubicacion.id,
                        onClick = { viewModel.onUbicacionChange(ubicacion.id) }
                    )
                    Text("${ubicacion.codigo}: ${ubicacionViewModel.texto(ubicacion)}")
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
                enabled = !estado.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (estado.isLoading) "Guardando" else "Guardar")
            }
        }
    }
}
