package com.scritch.app.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color

val scritchColorScheme = darkColorScheme(
    primary = Color(0xFFEED201),
    onPrimary = Color.Black,
    background = Color.Black,
    surface = Color.Black,
    surfaceContainer = Color(0xFF1A1919),
    onSurface = Color.White,
)

@Composable
fun NavigationBarItemColors(): NavigationBarItemColors {
    val colorScheme = MaterialTheme.colorScheme
    return remember {
        NavigationBarItemColors(
            selectedIconColor = colorScheme.primary,
            selectedTextColor = colorScheme.primary,
            selectedIndicatorColor = Color.Transparent,
            unselectedIconColor = colorScheme.onSurface,
            unselectedTextColor = colorScheme.onSurface,
            disabledIconColor = colorScheme.onSurface.copy(alpha = 0.5f),
            disabledTextColor = colorScheme.onSurface.copy(alpha = 0.5f),
        )
    }
}