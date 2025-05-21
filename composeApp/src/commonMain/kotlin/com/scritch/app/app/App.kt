package com.scritch.app.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.scritch.app.navigation.AppNavGraph
import com.scritch.app.theme.scritchColorScheme
import com.scritch.app.theme.scritchShapes
import com.scritch.app.theme.scritchTypography
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    /*KoinApplication(
        application = {
            modules(Koin.modules())
        }
    ){*/
    MaterialTheme(
        colorScheme = scritchColorScheme,
        typography = scritchTypography(),
        shapes = scritchShapes,
    ) {
        AppNavGraph()
    }
    // }
}