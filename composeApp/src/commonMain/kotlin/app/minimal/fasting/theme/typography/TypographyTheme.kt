package app.minimal.fasting.theme.typography

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import minimalfasting.composeapp.generated.resources.Res
import minimalfasting.composeapp.generated.resources.bevietnampro_semibold
import org.jetbrains.compose.resources.Font

data class TypographyTheme (
    val h1: TextStyle = TextStyle(),
    val h2: TextStyle = TextStyle(),
    val h3: TextStyle = TextStyle(),
    val h4: TextStyle = TextStyle(),
    val h5: TextStyle = TextStyle(),
    val h6: TextStyle = TextStyle(),
)

@Composable
fun typographyTheme(): TypographyTheme {
    val beVietnam = FontFamily(
        Font(Res.font.bevietnampro_semibold)
    )
    MaterialTheme.typography
    return TypographyTheme(
        h1 = TextStyle(
            fontFamily = beVietnam,
            fontWeight = FontWeight.Light,
            fontSize = 96.sp,
            lineHeight = 112.sp,
            letterSpacing = (-1.5).sp
        ),
        h2 = TextStyle(
            fontFamily = beVietnam,
            fontWeight = FontWeight.Light,
            fontSize = 60.sp,
            lineHeight = 72.sp,
            letterSpacing = (-0.5).sp
        ),
        h3 = TextStyle(
            fontFamily = beVietnam,
            fontWeight = FontWeight.Normal,
            fontSize = 48.sp,
            lineHeight = 56.sp,
            letterSpacing = 0.sp
        ),
        h4 = TextStyle(
            fontFamily = beVietnam,
            fontWeight = FontWeight.Normal,
            fontSize = 34.sp,
            lineHeight = 36.sp,
            letterSpacing = 0.25.sp
        ),
        h5 = TextStyle(
            fontFamily = beVietnam,
            fontWeight = FontWeight.Normal,
            fontSize = 24.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.sp
        ),
        h6 = TextStyle(
            fontFamily = beVietnam,
            fontWeight = FontWeight.Medium,
            fontSize = 20.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.15.sp
        )

    )
}