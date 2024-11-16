package app.minimal.fasting.theme.color

import androidx.compose.ui.graphics.Color

// Oranges
private const val StravaOrange = 0xfffc5200
private const val Pumpkin = 0xfffc6100
private const val Rust = 0xffcc4200

// Dark greys
private const val Coal = 0xff242428
private const val Asphalt = 0xff494950
private const val Gravel = 0xff6d6d78

//Light greys
private const val Fog = 0xfff7f7fa
private const val Icicle = 0xfff0f0f5
private const val Silver = 0xffdfdfe8

data class ColorTheme (
    val emphasis: Color,
    val emphasis2: Color,
    val emphasis3: Color,
    val onEmphasis: Color,
    val surface01: Color,
    val surface02: Color,
    val surface03: Color,
    val typography: Color,
    val typography2: Color,
    val typography3: Color,
    val typography4: Color,
)

fun lightTheme() = ColorTheme(
    emphasis = Color(StravaOrange),
    emphasis2 = Color(Pumpkin),
    emphasis3 = Color(Rust),
    onEmphasis = Color.White,
    surface01 = Color(Silver),
    surface02 = Color(Gravel),
    surface03 = Color(Asphalt),
    typography = Color.Black,
    typography2 = Color(Coal),
    typography3 = Color(Asphalt),
    typography4 = Color(Gravel),
)