package com.scritch.app.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.scritch.app.navigation.AppNavGraph
import com.scritch.app.theme.scritchColorScheme
import com.scritch.app.theme.scritchShapes
import com.scritch.app.theme.scritchTypography
import io.kamel.core.config.KamelConfig
import io.kamel.core.config.takeFrom
import io.kamel.image.config.Default
import io.kamel.image.config.LocalKamelConfig
import io.kamel.image.config.imageBitmapDecoder
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    /*KoinApplication(
        application = {
            modules(Koin.modules())
        }
    ){*/

    // Configure Kamel for optimized image loading
    val kamelConfig = KamelConfig {
        takeFrom(KamelConfig.Default)
        // Configure memory cache for better performance
        imageBitmapCacheSize = 50 // Limit cache to 50 images
        imageBitmapDecoder()
    }

    CompositionLocalProvider(LocalKamelConfig provides kamelConfig) {
        MaterialTheme(
            colorScheme = scritchColorScheme,
            typography = scritchTypography(),
            shapes = scritchShapes,
        ) {
            AppNavGraph()
        }
    }
    // }
}