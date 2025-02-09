package com.scritch.app.app

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import com.scritch.app.di.appModule
import com.scritch.app.navigation.AppNavGraph
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication
import org.koin.core.KoinApplication

@Composable
@Preview
fun App() {
    KoinApplication(
        application = {
            modules(appModule)
        }
    ){
        MaterialTheme {
            AppNavGraph()
        }
    }
}