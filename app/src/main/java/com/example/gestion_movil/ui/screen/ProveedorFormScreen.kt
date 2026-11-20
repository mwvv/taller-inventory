package com.example.gestion_movil.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gestion_movil.model.ProveedorUiState
import com.example.gestion_movil.ui.theme.GestionMovilTheme
import com.example.gestion_movil.viewmodel.ProveedorViewModel

@Composable
fun ProveedorFormScreen(
    navController: NavController,
    viewModel: ProveedorViewModel
) {
    val estado by viewModel.estado.collectAsState()

    ProveedorFormContent(
        estado = estado,
        onCodigoChange = viewModel::onCodigoChange,
        onNombreChange = viewModel::onNombreChange,
        onContactoChange = viewModel::onContactoChange,
        onCorreoChange = viewModel::onCorreoChange,
        onTelefonoChange = viewModel::onTelefonoChange,
        onCategoriasChange = viewModel::onCategoriasChange,
        onObservacionesChange = viewModel::onObservacionesChange,
        onActivoChange = viewModel::onActivoChange,
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
fun ProveedorFormContent(
    estado: ProveedorUiState,
    onCodigoChange: (String) -> Unit,
    onNombreChange: (String) -> Unit,
    onContactoChange: (String) -> Unit,
    onCorreoChange: (String) -> Unit,
    onTelefonoChange: (String) -> Unit,
    onCategoriasChange: (String) -> Unit,
    onObservacionesChange: (String) -> Unit,
    onActivoChange: (Boolean) -> Unit,
    onGuardar: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (estado.id == 0L) "Registrar proveedor" else "Modificar proveedor") },
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

            Text("Use solo datos ficticios", style = MaterialTheme.typography.bodySmall)

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
                label = { Text("Nombre comercial") },
                isError = estado.errores.nombre != null,
                supportingText = {
                    estado.errores.nombre?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = estado.contacto,
                onValueChange = onContactoChange,
                label = { Text("Contacto") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = estado.correo,
                onValueChange = onCorreoChange,
                label = { Text("Correo electrónico") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = estado.errores.correo != null,
                supportingText = {
                    estado.errores.correo?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = estado.telefono,
                onValueChange = onTelefonoChange,
                label = { Text("Teléfono (opcional)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = estado.categorias,
                onValueChange = onCategoriasChange,
                label = { Text("Productos o categorías que suministra") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = estado.observaciones,
                onValueChange = onObservacionesChange,
                label = { Text("Observaciones / condiciones referenciales") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = estado.activo,
                    onCheckedChange = onActivoChange
                )

                Spacer(Modifier.width(8.dp))

                Text("Proveedor activo")
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
fun ProveedorFormContentPreview() {
    GestionMovilTheme {
        ProveedorFormContent(
            estado = ProveedorUiState(
                codigo = "PR-001",
                nombre = "Repuestos Andes",
                contacto = "Juan Pérez",
                correo = "ventas@andes-ficticio.cl",
                telefono = "+56 9 1234 5678",
                categorias = "Filtros, Frenos, Eléctrico",
                observaciones = "Entrega en 48 horas",
                activo = true
            ),
            onCodigoChange = {},
            onNombreChange = {},
            onContactoChange = {},
            onCorreoChange = {},
            onTelefonoChange = {},
            onCategoriasChange = {},
            onObservacionesChange = {},
            onActivoChange = {},
            onGuardar = {},
            onBack = {}
        )
    }
}
