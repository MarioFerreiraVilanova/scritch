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
        titleLarge = titleLarge.copy(fontFamily = fontFamily, fontWeight = FontWeight.Bold),
        titleMedium = titleMedium.copy(fontFamily = fontFamily, fontWeight = FontWeight.Bold),
        titleSmall = titleSmall.copy(fontFamily = fontFamily, fontWeight = FontWeight.Bold),
        bodyLarge = bodyLarge.copy(fontFamily = fontFamily, fontWeight = FontWeight.Bold),
        bodyMedium = bodyMedium.copy(fontFamily = fontFamily, fontWeight = FontWeight.Bold),
        bodySmall = bodySmall.copy(fontFamily = fontFamily, fontWeight = FontWeight.Bold),
        labelLarge = labelLarge.copy(fontFamily = fontFamily, fontWeight = FontWeight.Bold),
        labelMedium = labelMedium.copy(fontFamily = fontFamily, fontWeight = FontWeight.Bold),
        labelSmall = labelSmall.copy(fontFamily = fontFamily, fontWeight = FontWeight.Bold)
    )
}