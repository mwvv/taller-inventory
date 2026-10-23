package com.example.gestion_movil.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gestion_movil.ui.login.LoginScreen
import com.example.gestion_movil.ui.screen.CorreoScreen
import com.example.gestion_movil.ui.screen.HistorialScreen
import com.example.gestion_movil.ui.screen.MovimientoFormScreen
import com.example.gestion_movil.ui.screen.ProductoDetalleScreen
import com.example.gestion_movil.ui.screen.ProductoFormScreen
import com.example.gestion_movil.ui.screen.ListaProductosScreen
import com.example.gestion_movil.ui.screen.ProveedorFormScreen
import com.example.gestion_movil.ui.screen.ProveedorListScreen
import com.example.gestion_movil.ui.screen.ReposicionScreen
import com.example.gestion_movil.ui.screen.UbicacionFormScreen
import com.example.gestion_movil.ui.screen.UbicacionListScreen
import com.example.gestion_movil.ui.view.QrScannerScreen
import com.example.gestion_movil.ui.viewmodel.QrViewModel
import com.example.gestion_movil.view.DrawerMenu
import androidx.compose.runtime.LaunchedEffect
import com.example.gestion_movil.ui.screen.IndicadoresScreen
import com.example.gestion_movil.viewmodel.IndicadoresViewModel
import com.example.gestion_movil.viewmodel.MovimientoViewModel
import com.example.gestion_movil.viewmodel.SesionViewModel
import com.example.gestion_movil.viewmodel.ProductoViewModel
import com.example.gestion_movil.viewmodel.ProveedorViewModel
import com.example.gestion_movil.viewmodel.ReposicionViewModel
import com.example.gestion_movil.viewmodel.UbicacionViewModel

// Rutas de la app. El usuario que inició sesión viaja en la ruta ({username})
// para registrarlo como responsable de cada movimiento.
@Composable
fun AppNav(
    // Cámara QR: el ViewModel y el permiso los maneja MainActivity
    qrViewModel: QrViewModel,
    hasCameraPermission: Boolean,
    onRequestPermission: () -> Unit,
    // Sesión: perfil del usuario y bloqueo por inactividad (también desde MainActivity)
    sesionViewModel: SesionViewModel
) {
    val navController = rememberNavController()

    // Los ViewModel se crean una sola vez y se comparten entre pantallas
    val productoViewModel: ProductoViewModel = viewModel()
    val movimientoViewModel: MovimientoViewModel = viewModel()
    val ubicacionViewModel: UbicacionViewModel = viewModel()
    val proveedorViewModel: ProveedorViewModel = viewModel()
    val reposicionViewModel: ReposicionViewModel = viewModel()
    val indicadoresViewModel: IndicadoresViewModel = viewModel()

    val argumentoUsuario = listOf(navArgument("username") { type = NavType.StringType })

    // Permisos según el perfil (se recalculan cuando cambia la sesión)
    val sesion by sesionViewModel.estado.collectAsState()
    val esPropietario = sesionViewModel.esPropietario()
    val puedeAdministrarProductos = sesionViewModel.puedeAdministrarProductos()

    // Bloqueo por inactividad: vuelve al login y borra el historial de pantallas
    LaunchedEffect(sesion.bloqueadaPorInactividad) {
        if (sesion.bloqueadaPorInactividad) {
            navController.navigate("login") {
                popUpTo(navController.graph.id) { inclusive = true }
                launchSingleTop = true
            }
            sesionViewModel.bloqueoAtendido()
        }
    }

    // Animación al cambiar de pantalla: la nueva entra deslizándose desde la derecha
    // y al volver atrás la pantalla actual sale hacia la derecha
    NavHost(
        navController = navController,
        startDestination = "login",
        enterTransition = { slideInHorizontally { it } + fadeIn() },
        exitTransition = { slideOutHorizontally { -it / 3 } + fadeOut() },
        popEnterTransition = { slideInHorizontally { -it / 3 } + fadeIn() },
        popExitTransition = { slideOutHorizontally { it } + fadeOut() }
    ) {

        composable("login") {
            LoginScreen(navController = navController, sesionViewModel = sesionViewModel)
        }

        // Menú principal
        composable(route = "DrawerMenu/{username}", arguments = argumentoUsuario) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username").orEmpty()
            DrawerMenu(
                username = username,
                navController = navController,
                productoViewModel = productoViewModel,
                rolTexto = sesion.rol?.texto.orEmpty(),
                puedeAdministrarCompras = sesionViewModel.puedeAdministrarCompras(),
                onCerrarSesion = { sesionViewModel.cerrar() }
            )
        }

        // ---------- Productos ----------

        composable(route = "productos/{username}", arguments = argumentoUsuario) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username").orEmpty()
            ListaProductosScreen(navController, username, productoViewModel, proveedorViewModel, ubicacionViewModel, puedeAdministrarProductos)
        }

        composable("producto_form") {
            ProductoFormScreen(navController, productoViewModel, proveedorViewModel, ubicacionViewModel)
        }

        composable(route = "producto_detalle/{username}", arguments = argumentoUsuario) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username").orEmpty()
            ProductoDetalleScreen(
                navController, username, productoViewModel, movimientoViewModel, proveedorViewModel, ubicacionViewModel,
                puedeAdministrar = puedeAdministrarProductos,
                puedeVerPrecios = sesionViewModel.puedeVerPrecios()
            )
        }

        // ---------- Movimientos ----------

        composable(route = "movimiento/{username}", arguments = argumentoUsuario) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username").orEmpty()
            MovimientoFormScreen(
                navController, username, movimientoViewModel, productoViewModel,
                tiposPermitidos = sesionViewModel.tiposMovimientoPermitidos(movimientoViewModel.tipos)
            )
        }

        composable("historial") {
            HistorialScreen(navController, movimientoViewModel)
        }

        // ---------- Ubicaciones ----------

        composable("ubicaciones") {
            UbicacionListScreen(navController, ubicacionViewModel, productoViewModel, sesionViewModel.puedeAdministrarUbicaciones())
        }

        composable("ubicacion_form") {
            UbicacionFormScreen(navController, ubicacionViewModel)
        }

        // ---------- Proveedores ----------

        composable("proveedores") {
            ProveedorListScreen(navController, proveedorViewModel, productoViewModel)
        }

        composable("proveedor_form") {
            ProveedorFormScreen(navController, proveedorViewModel)
        }

        // ---------- Reposición y correo ----------

        composable("reposicion") {
            ReposicionScreen(navController, reposicionViewModel, productoViewModel, proveedorViewModel)
        }

        composable("correo") {
            CorreoScreen(navController, reposicionViewModel)
        }

        // ---------- Indicadores ----------

        composable("indicadores") {
            IndicadoresScreen(navController, indicadoresViewModel)
        }

        // ---------- Cámara QR ----------

        composable(route = "qr/{username}", arguments = argumentoUsuario) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username").orEmpty()
            val productos by productoViewModel.productos.collectAsState(initial = emptyList())
            Surface {
                QrScannerScreen(
                    viewModel = qrViewModel,
                    hasCameraPermission = hasCameraPermission,
                    onRequestPermission = onRequestPermission,
                    productos = productos,
                    onVerProducto = { id ->
                        productoViewModel.seleccionar(id)
                        navController.navigate("producto_detalle/$username")
                    },
                    // Solo el propietario registra productos nuevos
                    puedeRegistrar = puedeAdministrarProductos,
                    onRegistrarProducto = { codigo ->
                        productoViewModel.nuevoConCodigo(codigo)
                        navController.navigate("producto_form")
                    }
                )
            }
        }
    }
}
