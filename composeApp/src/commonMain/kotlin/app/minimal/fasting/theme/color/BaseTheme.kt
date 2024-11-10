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
    val primary01: Color,
    val primary02: Color,
    val primary03: Color,
    val surface01: Color,
    val surface02: Color,
    val surface03: Color,
    val surface04: Color,
    val neutral01: Color,
    val neutral02: Color,
    val neutral03: Color,
    val neutral04: Color,
)

fun lightTheme() = ColorTheme(
    primary01 = Color(Pumpkin),
    primary02 = Color(StravaOrange),
    primary03 = Color(Rust),
    surface01 = Color.White,
    surface02 = Color(Fog),
    surface03 = Color(Icicle),
    surface04 = Color(Silver),
    neutral01 = Color.Black,
    neutral02 = Color(Coal),
    neutral03 = Color(Asphalt),
    neutral04 = Color(Gravel),
)