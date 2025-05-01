package com.example.faunafinder.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = CafeOscuro,           // Color principal (botones, etc.)
    onPrimary = BlancoSuave,        // Texto sobre primario
    secondary = VerdeMenosOscuro,   // Botones secundarios
    onSecondary = BlancoSuave,      // Texto sobre secundarios
    background = VerdeOscuro,       // Fondo de pantalla
    onBackground = BlancoSuave,     // Texto sobre fondo
    surface = Menta,                // Tarjetas, campos de texto, etc.
    onSurface = Carbon,        // Texto sobre surface
)

private val LightColorScheme = lightColorScheme(
    primary = VerdeLima,         // Botones
    onPrimary = Negro,           // Texto en botones
    background = VerdeMentaClaro,// Fondo de pantalla
    onBackground = Negro,        // Texto sobre fondo
    surface = Blanco,            // Campos de texto
    onSurface = Negro,           // Texto dentro de campos
    secondary = CafeClaro        // Íconos o detalles

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun FaunaFInderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}