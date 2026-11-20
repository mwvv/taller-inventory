package com.example.gestion_movil.ui.screen

import android.content.ActivityNotFoundException
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gestion_movil.model.CorreoUiState
import com.example.gestion_movil.ui.theme.GestionMovilTheme
import com.example.gestion_movil.viewmodel.ReposicionViewModel

@Composable
fun CorreoScreen(
    navController: NavController,
    viewModel: ReposicionViewModel
) {
    val correo by viewModel.correo.collectAsState()
    val context = LocalContext.current

    CorreoContent(
        correo = correo,
        onDestinatarioChange = viewModel::onDestinatarioChange,
        onAsuntoChange = viewModel::onAsuntoChange,
        onCuerpoChange = viewModel::onCuerpoChange,
        onValidar = { viewModel.validarCorreo() },
        onConfirmarAbrir = {
            try {
                context.startActivity(viewModel.crearIntentCorreo())
                viewModel.marcarPreparada()
            } catch (e: ActivityNotFoundException) {
                viewModel.onErrorCorreo("No hay una aplicación de correo instalada")
            }
        },
        onBack = { navController.popBackStack() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CorreoContent(
    correo: CorreoUiState,
    onDestinatarioChange: (String) -> Unit,
    onAsuntoChange: (String) -> Unit,
    onCuerpoChange: (String) -> Unit,
    onValidar: () -> Boolean,
    onConfirmarAbrir: () -> Unit,
    onBack: () -> Unit
) {
    var mostrarDialogo by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Correo al proveedor") },
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

            Text("Revise y modifique el borrador. La app NO envía el correo: solo lo abre en su aplicación de correo.")

            OutlinedTextField(
                value = correo.destinatario,
                onValueChange = onDestinatarioChange,
                label = { Text("Destinatario") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = correo.asunto,
                onValueChange = onAsuntoChange,
                label = { Text("Asunto") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = correo.cuerpo,
                onValueChange = onCuerpoChange,
                label = { Text("Mensaje") },
                minLines = 10,
                modifier = Modifier.fillMaxWidth()
            )

            correo.error?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    if (onValidar()) {
                        mostrarDialogo = true
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Abrir en la app de correo")
            }
        }

        if (mostrarDialogo) {
            AlertDialog(
                onDismissRequest = { mostrarDialogo = false },
                title = { Text("Preparar solicitud") },
                text = { Text("Se abrirá su aplicación de correo con el borrador para ${correo.destinatario}. Usted decide si lo envía.") },
                confirmButton = {
                    TextButton(onClick = {
                        mostrarDialogo = false
                        onConfirmarAbrir()
                    }) {
                        Text("Abrir correo")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarDialogo = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CorreoContentPreview() {
    GestionMovilTheme {
        CorreoContent(
            correo = CorreoUiState(
                proveedorId = 1,
                destinatario = "ventas@andes-ficticio.cl",
                asunto = "Solicitud de reposición",
                cuerpo = "Estimados,\n\nSolicitamos cotización de los siguientes productos:\n- Aceite 5W-30 (código AC-001): 8 unidades\n- Filtro de aire (código FA-010): 5 unidades\n\nSaludos.",
                error = null
            ),
            onDestinatarioChange = {},
            onAsuntoChange = {},
            onCuerpoChange = {},
            onValidar = { true },
            onConfirmarAbrir = {},
            onBack = {}
        )
    }
}
