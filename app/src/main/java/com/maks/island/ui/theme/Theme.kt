package com.maks.island.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val PremiumDark = darkColorScheme(
    primary = Color(0xFFAEC6FF),
    secondary = Color(0xFFD0BCFF),
    surface = Color(0xFF0E1015),
    surfaceVariant = Color(0xFF1B1F28),
)

private val PremiumLight = lightColorScheme(
    primary = Color(0xFF3E63DD),
    secondary = Color(0xFF7B57C9),
    surface = Color(0xFFF6F7FB),
    surfaceVariant = Color(0xFFE8ECF7),
)

@Composable
fun MaksIslandTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    pureBlack: Boolean = false,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val colors = when {
        pureBlack && darkTheme -> PremiumDark.copy(surface = Color.Black, background = Color.Black)
        dynamicColor && darkTheme -> dynamicDarkColorScheme(context)
        dynamicColor && !darkTheme -> dynamicLightColorScheme(context)
        darkTheme -> PremiumDark
        else -> PremiumLight
    }
    MaterialTheme(colorScheme = colors, typography = Typography, content = content)
}
