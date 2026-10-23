package com.example.gestion_movil.view

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gestion_movil.viewmodel.ProductoViewModel

// Menú principal de la app con los módulos del caso
@Composable
fun DrawerMenu(
    username: String,
    navController: NavController,
    productoViewModel: ProductoViewModel,
    rolTexto: String,                       // "Propietario" u "Operador"
    puedeAdministrarCompras: Boolean,       // Proveedores, reposición e indicadores (solo propietario)
    onCerrarSesion: () -> Unit
) {
    val productos by productoViewModel.productos.collectAsState(initial = emptyList())
    // Aviso interno de existencias bajas
    val bajoMinimo = productoViewModel.contarBajoMinimo(productos)

    Column(modifier = Modifier.fillMaxSize()) {

        // Encabezado con el usuario que inició sesión
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(MaterialTheme.colorScheme.primary)
        ) {
            Text(
                text = "Gestión Móvil Taller\nUsuario: $username ($rolTexto)",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            )
        }

        if (bajoMinimo > 0) {
            // Animación: el aviso "late" suavemente (crece y vuelve) para llamar la atención
            val latido = rememberInfiniteTransition(label = "latidoAviso")
            val escala by latido.animateFloat(
                initialValue = 1f,
                targetValue = 1.05f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 800),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "escalaAviso"
            )
            NavigationDrawerItem(
                modifier = Modifier.scale(escala),
                label = { Text("Aviso: $bajoMinimo producto(s) agotados o bajo el mínimo", fontWeight = FontWeight.Bold) },
                selected = true,
                // El propietario va a reposición; el operador solo puede ver los productos
                onClick = {
                    if (puedeAdministrarCompras) navController.navigate("reposicion")
                    else navController.navigate("productos/$username")
                },
                icon = { Icon(Icons.Default.Warning, contentDescription = "Aviso de existencias bajas") }
            )
        }

        LazyColumn(modifier = Modifier.weight(1f)) {
            item { OpcionMenu("Productos", Icons.Default.Inventory) { navController.navigate("productos/$username") } }
            item { OpcionMenu("Historial de movimientos", Icons.Default.History) { navController.navigate("historial") } }
            item { OpcionMenu("Ubicaciones", Icons.Default.Place) { navController.navigate("ubicaciones") } }
            // Opciones solo para el propietario
            if (puedeAdministrarCompras) {
                item { OpcionMenu("Proveedores", Icons.Default.LocalShipping) { navController.navigate("proveedores") } }
                item { OpcionMenu("Reposición", Icons.Default.ShoppingCart) { navController.navigate("reposicion") } }
                item { OpcionMenu("Indicadores", Icons.Default.BarChart) { navController.navigate("indicadores") } }
            }
            item { OpcionMenu("Escanear QR", Icons.Default.QrCodeScanner) { navController.navigate("qr/$username") } }
            item {
                OpcionMenu("Cerrar sesión", Icons.Default.Lock) {
                    onCerrarSesion()
                    navController.navigate("login") {
                        popUpTo(navController.graph.id) { inclusive = true } // no volver al menú con "atrás"
                        launchSingleTop = true
                    }
                }
            }
        }
    }
}

// Una opción del menú (texto + ícono)
@Composable
private fun OpcionMenu(texto: String, icono: ImageVector, onClick: () -> Unit) {
    NavigationDrawerItem(
        label = { Text(texto) },
        selected = false,
        onClick = onClick,
        icon = { Icon(icono, contentDescription = texto) }
    )
}
