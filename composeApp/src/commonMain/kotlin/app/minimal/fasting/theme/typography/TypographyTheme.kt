package app.minimal.fasting.theme.typography

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import minimalfasting.composeapp.generated.resources.Res
import minimalfasting.composeapp.generated.resources.bevietnampro_semibold
import org.jetbrains.compose.resources.Font

data class TypographyTheme (
    val bigText: TextStyle = TextStyle(),

)

@Composable
fun typographyTheme(): TypographyTheme {
    val beVietnam = FontFamily(
        Font(Res.font.bevietnampro_semibold)
    )
    return TypographyTheme(
        bigText = TextStyle(
            fontFamily = beVietnam,
            fontSize = 96.sp,
            lineHeight = 112.sp,
            letterSpacing = (-1.5).sp
        ),

    )
}