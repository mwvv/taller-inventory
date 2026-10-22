package com.example.gestion_movil.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = AzulTaller,
    secondary = NaranjaTaller,
    tertiary = GrisAcero
)

private val DarkColorScheme = darkColorScheme(
    primary = AzulTallerClaro,
    secondary = NaranjaTallerClaro,
    tertiary = GrisAceroClaro
)

// Tema de la app: siempre usa los colores del taller (sin colores dinámicos del sistema)
@Composable
fun GestionMovilTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
