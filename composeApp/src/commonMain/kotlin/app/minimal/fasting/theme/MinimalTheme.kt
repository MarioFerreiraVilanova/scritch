package app.minimal.fasting.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import app.minimal.fasting.theme.color.ColorTheme
import app.minimal.fasting.theme.color.lightTheme

internal val LocalColors = staticCompositionLocalOf { lightTheme() }

object MinimalTheme {
    val color: ColorTheme
        @Composable
        @ReadOnlyComposable
        get() = LocalColors.current
}

@Composable
fun MinimalTheme(
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalColors provides MinimalTheme.color
    ) {
        content()
    }
}