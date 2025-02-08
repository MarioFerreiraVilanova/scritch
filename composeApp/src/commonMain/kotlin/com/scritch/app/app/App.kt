package com.scritch.app.app

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import com.scritch.app.navigation.AppNavGraph
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        AppNavGraph()
    }
}