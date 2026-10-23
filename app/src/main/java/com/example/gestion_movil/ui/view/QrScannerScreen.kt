package com.example.gestion_movil.ui.view


import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.gestion_movil.model.data.local.ProductoEntity
import com.example.gestion_movil.ui.viewmodel.QrViewModel
import com.example.gestion_movil.utils.QrScanner

@Composable
fun QrScannerScreen(
    viewModel: QrViewModel,
    hasCameraPermission: Boolean,
    onRequestPermission: () -> Unit,
    // Productos del inventario, para buscar el que tenga el código leído
    productos: List<ProductoEntity> = emptyList(),
    onVerProducto: (Long) -> Unit = {},
    // Si el código no existe, el propietario puede registrar el producto desde aquí
    puedeRegistrar: Boolean = false,
    onRegistrarProducto: (String) -> Unit = {}
) {
    val qrResult by viewModel.qrResult.observeAsState()
    val context = LocalContext.current
    var isScanning by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (!hasCameraPermission) {
            Text(
                "Permiso de cámara requerido",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "Para escanear códigos QR, necesitamos acceso a la cámara",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = onRequestPermission
            ) {
                Text("Conceder permiso de cámara")
            }
        } else {
            // Animación: el paso de "escaneando" a "resultado" (y de vuelta) se hace
            // con un fundido y un leve zoom en vez de cambiar de golpe
            AnimatedContent(
                targetState = qrResult,
                transitionSpec = { (fadeIn() + scaleIn(initialScale = 0.9f)) togetherWith fadeOut() },
                label = "resultadoQr"
            ) { resultado ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (resultado == null && isScanning) {
                        Text(
                            "Escanea un código QR",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Usar el nuevo scanner con CameraX
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp)
                                .clipToBounds() // la imagen de la cámara no se sale del recuadro
                        ) {
                            QrScanner(
                                onQrCodeScanned = { qrContent ->
                                    // Procesar el QR detectado
                                    viewModel.onQrDetected(qrContent)
                                    isScanning = false
                                    Toast.makeText(context, "QR detectado!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.fillMaxSize()
                            )

                            // Overlay para ayudar al escaneo
                            Surface(
                                modifier = Modifier
                                    .size(250.dp)
                                    .align(Alignment.Center),
                                color = Color.Transparent,
                                shape = MaterialTheme.shapes.medium,
                                border = BorderStroke(
                                    2.dp,
                                    MaterialTheme.colorScheme.primary
                                )
                            ) {}
                        }

                        Spacer(Modifier.height(16.dp))
                        Text(
                            "Enfoca el código QR en el marco central",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "La cámara debería activarse automáticamente",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else if (resultado != null) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                " QR Detectado:",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Text(
                                    resultado.content,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }

                            // Producto del inventario con el código leído
                            val producto = viewModel.productoDelQr(productos)
                            if (producto != null) {
                                Text(
                                    "Producto: ${producto.nombre} (${producto.codigo})",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    "Categoría: ${producto.categoria}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    "Cantidad disponible: ${producto.cantidad} ${producto.unidadMedida}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    "Existencia mínima: ${producto.existenciaMinima}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(Modifier.height(8.dp))
                                OutlinedButton(
                                    onClick = { onVerProducto(producto.id) },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Ver producto")
                                }
                            } else {
                                Text(
                                    "No hay un producto registrado con este código",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.height(8.dp))
                                if (puedeRegistrar) {
                                    // Abre el formulario con el código del QR ya escrito
                                    OutlinedButton(
                                        onClick = { onRegistrarProducto(resultado.content) },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Registrar producto con este código")
                                    }
                                } else {
                                    Text(
                                        "Pida al propietario que lo registre",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    viewModel.clearResult()
                                    isScanning = true
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Escanear otro código QR")
                            }
                        }
                    }
                }
            }
        }
    }
}
