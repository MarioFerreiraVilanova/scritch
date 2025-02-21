package com.scritch.app.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.scritch.app.di.appModule
import com.scritch.app.navigation.AppNavGraph
import com.scritch.app.theme.scritchColorScheme
import com.scritch.app.theme.scritchTypography
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication

@Composable
@Preview
fun App() {
    KoinApplication(
        application = {
            modules(appModule)
        }
    ){
        MaterialTheme(
            colorScheme = scritchColorScheme,
            typography = scritchTypography(),
        ) {
            AppNavGraph()
        }
    }
}