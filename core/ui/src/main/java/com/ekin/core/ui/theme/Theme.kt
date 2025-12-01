package com.ekin.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Modern beyaz-yeşil palet
private val LightColors = lightColorScheme(
    primary = Color(0xFF2F7D4A),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFF6EC07B),
    onSecondary = Color(0xFF0F1F11),
    tertiary = Color(0xFFB4E0C3),
    background = Color(0xFFF6F8F6),
    onBackground = Color(0xFF1F2A1F),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1F2A1F),
    surfaceVariant = Color(0xFFE3EFE5),
    outline = Color(0xFF94B49F)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF79D28D),
    onPrimary = Color(0xFF0C280F),
    secondary = Color(0xFF4AA266),
    onSecondary = Color(0xFF031407),
    tertiary = Color(0xFF234230),
    background = Color(0xFF101A12),
    onBackground = Color(0xFFE0F0E3),
    surface = Color(0xFF162118),
    onSurface = Color(0xFFE0F0E3),
    surfaceVariant = Color(0xFF2C3A2F),
    outline = Color(0xFF6AA37C)
)

@Composable
fun EkinTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}


