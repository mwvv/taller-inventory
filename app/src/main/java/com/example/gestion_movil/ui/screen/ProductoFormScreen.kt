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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gestion_movil.model.ProductoUiState
import com.example.gestion_movil.model.data.local.ProveedorEntity
import com.example.gestion_movil.model.data.local.UbicacionEntity
import com.example.gestion_movil.ui.theme.GestionMovilTheme
import com.example.gestion_movil.viewmodel.ProductoViewModel
import com.example.gestion_movil.viewmodel.ProveedorViewModel
import com.example.gestion_movil.viewmodel.UbicacionViewModel

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

    ProductoFormContent(
        estado = estado,
        proveedores = proveedores,
        ubicaciones = ubicaciones,
        estadosProducto = viewModel.estadosProducto,
        textoUbicacion = { ubicacionViewModel.texto(it) },
        onCodigoChange = viewModel::onCodigoChange,
        onNombreChange = viewModel::onNombreChange,
        onDescripcionChange = viewModel::onDescripcionChange,
        onCategoriaChange = viewModel::onCategoriaChange,
        onMarcaChange = viewModel::onMarcaChange,
        onUnidadMedidaChange = viewModel::onUnidadMedidaChange,
        onCantidadChange = viewModel::onCantidadChange,
        onExistenciaMinimaChange = viewModel::onExistenciaMinimaChange,
        onExistenciaObjetivoChange = viewModel::onExistenciaObjetivoChange,
        onPrecioCompraChange = viewModel::onPrecioCompraChange,
        onPrecioVentaChange = viewModel::onPrecioVentaChange,
        onEstadoChange = viewModel::onEstadoChange,
        onProveedorChange = viewModel::onProveedorChange,
        onUbicacionChange = viewModel::onUbicacionChange,
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
fun ProductoFormContent(
    estado: ProductoUiState,
    proveedores: List<ProveedorEntity>,
    ubicaciones: List<UbicacionEntity>,
    estadosProducto: List<String>,
    textoUbicacion: (UbicacionEntity) -> String,
    onCodigoChange: (String) -> Unit,
    onNombreChange: (String) -> Unit,
    onDescripcionChange: (String) -> Unit,
    onCategoriaChange: (String) -> Unit,
    onMarcaChange: (String) -> Unit,
    onUnidadMedidaChange: (String) -> Unit,
    onCantidadChange: (String) -> Unit,
    onExistenciaMinimaChange: (String) -> Unit,
    onExistenciaObjetivoChange: (String) -> Unit,
    onPrecioCompraChange: (String) -> Unit,
    onPrecioVentaChange: (String) -> Unit,
    onEstadoChange: (String) -> Unit,
    onProveedorChange: (Long?) -> Unit,
    onUbicacionChange: (Long?) -> Unit,
    onGuardar: () -> Unit,
    onBack: () -> Unit
) {
    val esNuevo = estado.id == 0L
    val numeros = KeyboardOptions(keyboardType = KeyboardType.Number)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (esNuevo) "Registrar producto" else "Modificar producto") },
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
                label = { Text("Código interno") },
                isError = estado.errores.codigo != null,
                supportingText = {
                    estado.errores.codigo?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = estado.nombre,
                onValueChange = onNombreChange,
                label = { Text("Nombre") },
                isError = estado.errores.nombre != null,
                supportingText = {
                    estado.errores.nombre?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = estado.descripcion,
                onValueChange = onDescripcionChange,
                label = { Text("Descripción breve") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = estado.categoria,
                onValueChange = onCategoriaChange,
                label = { Text("Categoría (ej: Lubricantes, Filtros)") },
                isError = estado.errores.categoria != null,
                supportingText = {
                    estado.errores.categoria?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = estado.marca,
                onValueChange = onMarcaChange,
                label = { Text("Marca") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = estado.unidadMedida,
                onValueChange = onUnidadMedidaChange,
                label = { Text("Unidad de medida (ej: Unidad, Litro)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = estado.cantidad,
                onValueChange = onCantidadChange,
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

            OutlinedTextField(
                value = estado.existenciaMinima,
                onValueChange = onExistenciaMinimaChange,
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

            OutlinedTextField(
                value = estado.existenciaObjetivo,
                onValueChange = onExistenciaObjetivoChange,
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

            OutlinedTextField(
                value = estado.precioCompra,
                onValueChange = onPrecioCompraChange,
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

            OutlinedTextField(
                value = estado.precioVenta,
                onValueChange = onPrecioVentaChange,
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

            Text("Estado", style = MaterialTheme.typography.titleSmall)
            estadosProducto.forEach { opcion ->
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

            Text("Proveedor principal", style = MaterialTheme.typography.titleSmall)
            Row(
                modifier = Modifier.fillMaxWidth().clickable { onProveedorChange(null) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = estado.proveedorId == null,
                    onClick = { onProveedorChange(null) }
                )
                Text("Sin proveedor")
            }
            proveedores.forEach { proveedor ->
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { onProveedorChange(proveedor.id) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = estado.proveedorId == proveedor.id,
                        onClick = { onProveedorChange(proveedor.id) }
                    )
                    Text(proveedor.nombre)
                }
            }

            Text("Ubicación en la bodega", style = MaterialTheme.typography.titleSmall)
            Row(
                modifier = Modifier.fillMaxWidth().clickable { onUbicacionChange(null) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = estado.ubicacionId == null,
                    onClick = { onUbicacionChange(null) }
                )
                Text("Sin ubicación")
            }
            ubicaciones.forEach { ubicacion ->
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { onUbicacionChange(ubicacion.id) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = estado.ubicacionId == ubicacion.id,
                        onClick = { onUbicacionChange(ubicacion.id) }
                    )
                    Text("${ubicacion.codigo}: ${textoUbicacion(ubicacion)}")
                }
            }

            Button(
                onClick = onGuardar,
                enabled = !estado.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (estado.isLoading) "Guardando" else "Guardar")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductoFormContentPreview() {
    GestionMovilTheme {
        ProductoFormContent(
            estado = ProductoUiState(
                codigo = "AC-001",
                nombre = "Aceite 5W-30",
                descripcion = "Lubricante sintético",
                categoria = "Lubricantes",
                marca = "Genérico",
                unidadMedida = "Litro",
                cantidad = "10",
                existenciaMinima = "5",
                existenciaObjetivo = "12",
                precioCompra = "3500",
                precioVenta = "5990",
                estado = "Activo"
            ),
            proveedores = listOf(
                ProveedorEntity(1, "PR-001", "Repuestos Andes", "Juan", "a@b.cl", "", "Filtros", true, "")
            ),
            ubicaciones = listOf(
                UbicacionEntity(1, "A-2-3", "Bodega A", "2", "3", "Izq", "", "Disponible", 1_700_000_000_000)
            ),
            estadosProducto = listOf("Activo", "Inactivo", "Descontinuado"),
            textoUbicacion = { "Bodega A · Estante 2 · Repisa 3" },
            onCodigoChange = {},
            onNombreChange = {},
            onDescripcionChange = {},
            onCategoriaChange = {},
            onMarcaChange = {},
            onUnidadMedidaChange = {},
            onCantidadChange = {},
            onExistenciaMinimaChange = {},
            onExistenciaObjetivoChange = {},
            onPrecioCompraChange = {},
            onPrecioVentaChange = {},
            onEstadoChange = {},
            onProveedorChange = {},
            onUbicacionChange = {},
            onGuardar = {},
            onBack = {}
        )
    }
}
