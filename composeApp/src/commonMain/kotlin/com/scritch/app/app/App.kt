package com.scritch.app.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.scritch.app.di.appModule
import com.scritch.app.navigation.AppNavGraph
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
        MaterialTheme {
            AppNavGraph()
        }
    }
}