package app.minimal.fasting.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import app.minimal.fasting.theme.color.ColorTheme
import app.minimal.fasting.theme.color.lightTheme
import app.minimal.fasting.theme.typography.TypographyTheme
import app.minimal.fasting.theme.typography.typographyTheme

internal val LocalColors = staticCompositionLocalOf { lightTheme() }
internal val LocalTypography = staticCompositionLocalOf { TypographyTheme() }

object MinimalTheme {
    val color: ColorTheme
        @Composable
        @ReadOnlyComposable
        get() = LocalColors.current

    val typography: TypographyTheme
        @Composable
        @ReadOnlyComposable
        get() = LocalTypography.current
}

@Composable
fun MinimalTheme(
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalColors provides MinimalTheme.color,
        LocalTypography provides typographyTheme(),
    ) {
        content()
    }
}