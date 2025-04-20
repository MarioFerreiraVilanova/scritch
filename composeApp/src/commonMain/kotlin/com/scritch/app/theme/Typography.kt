package com.scritch.app.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.Font
import scritch.composeapp.generated.resources.Res
import scritch.composeapp.generated.resources.rem_variable_font_weight

@Composable
fun scritchTypography() = Typography().run {
    val fontFamily = FontFamily(
        Font(Res.font.rem_variable_font_weight)
    )
    copy(
        displayLarge = displayLarge.copy(fontFamily = fontFamily, fontWeight = FontWeight.Black),
        displayMedium = displayMedium.copy(fontFamily = fontFamily, fontWeight = FontWeight.Black),
        displaySmall = displaySmall.copy(fontFamily = fontFamily, fontWeight = FontWeight.Black),
        headlineLarge = headlineLarge.copy(fontFamily = fontFamily, fontWeight = FontWeight.Black),
        headlineMedium = headlineMedium.copy(fontFamily = fontFamily, fontWeight = FontWeight.Black),
        headlineSmall = headlineSmall.copy(fontFamily = fontFamily, fontWeight = FontWeight.Black),
        titleLarge = titleLarge.copy(fontFamily = fontFamily, fontWeight = FontWeight.Medium),
        titleMedium = titleMedium.copy(fontFamily = fontFamily, fontWeight = FontWeight.Medium),
        titleSmall = titleSmall.copy(fontFamily = fontFamily, fontWeight = FontWeight.Medium),
        bodyLarge = bodyLarge.copy(fontFamily = fontFamily, fontWeight = FontWeight.Medium),
        bodyMedium = bodyMedium.copy(fontFamily = fontFamily, fontWeight = FontWeight.Medium),
        bodySmall = bodySmall.copy(fontFamily = fontFamily, fontWeight = FontWeight.Medium),
        labelLarge = labelLarge.copy(fontFamily = fontFamily, fontWeight = FontWeight.Medium),
        labelMedium = labelMedium.copy(fontFamily = fontFamily, fontWeight = FontWeight.Medium),
        labelSmall = labelSmall.copy(fontFamily = fontFamily, fontWeight = FontWeight.Medium)
    )
}