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
    onSurface = Color.White,
    surface = Color.Black,
    surfaceContainer = Color(0xFF1A1919),
    surfaceVariant = Color(0xFF1C1C1C),
    surfaceDim = Color(0xFF121212),
    error = Color(0xFFDA3301),
    onError = Color.White,
    outline = Color(0xFF535353),
    onBackground = Color.White,
    onSurfaceVariant = Color(0xFFC2C2C2),
    inverseSurface = Color.White,
    inverseOnSurface = Color.Black,
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