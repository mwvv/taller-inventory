package com.example.gestion_movil

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.*
import com.example.gestion_movil.navigation.AppNav
import com.example.gestion_movil.ui.theme.GestionMovilTheme
import com.example.gestion_movil.ui.viewmodel.QrViewModel
import com.example.gestion_movil.utils.CameraPermissionHelper
import com.example.gestion_movil.utils.NotificacionHelper
import com.example.gestion_movil.viewmodel.SesionViewModel

class MainActivity : ComponentActivity() {

    private val qrViewModel: QrViewModel by viewModels()

    // Sesión del usuario (perfil y bloqueo por inactividad)
    private val sesionViewModel: SesionViewModel by viewModels()

    // Estado para controlar si tenemos permiso
    private var hasCameraPermission by mutableStateOf(false)

    // Registro para solicitar permisos
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (isGranted) {
            Toast.makeText(this, "Permiso de cámara concedido", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Se necesita permiso de cámara para escanear QR", Toast.LENGTH_LONG).show()
        }
    }

    // Registro para solicitar el permiso de notificaciones (avisos de stock bajo)
    private val requestNotificationLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(this, "Sin permiso, no se mostrarán avisos de stock bajo", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Verificar permiso inicial
        hasCameraPermission = CameraPermissionHelper.hasCameraPermission(this)

        // Canal de notificaciones y permiso para mostrarlas (Android 13+)
        NotificacionHelper.crearCanal(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            !NotificacionHelper.hasNotificationPermission(this)
        ) {
            requestNotificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            GestionMovilTheme {
                // La pantalla de QR se abre desde el menú; aquí se le entregan
                // el ViewModel, el estado del permiso y cómo solicitarlo
                AppNav(
                    qrViewModel = qrViewModel,
                    hasCameraPermission = hasCameraPermission,
                    onRequestPermission = {
                        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                    },
                    sesionViewModel = sesionViewModel
                )
            }
        }

        // Observa los resultados del QR
        qrViewModel.qrResult.observe(this) { qrResult ->
            qrResult?.let { result ->
                Toast.makeText(this, "Código QR Detectado: ${result.content}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Cada toque en la pantalla reinicia el tiempo de inactividad
    override fun onUserInteraction() {
        super.onUserInteraction()
        sesionViewModel.registrarActividad()
    }

    override fun onResume() {
        super.onResume()
        // Actualizar estado del permiso cuando la app se reanuda
        hasCameraPermission = CameraPermissionHelper.hasCameraPermission(this)
    }
}
