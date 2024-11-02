package app.minimal.fasting

import androidx.compose.ui.window.ComposeUIViewController
import app.minimal.fasting.app.App
import app.minimal.fasting.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) { App() }