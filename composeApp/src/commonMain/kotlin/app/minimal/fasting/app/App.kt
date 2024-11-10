package app.minimal.fasting.app

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import app.minimal.fasting.navigation.AppNavGraph
import app.minimal.fasting.theme.MinimalTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        MinimalTheme {
            AppNavGraph()
        }
    }
}