package com.scritch.app

import androidx.compose.ui.window.ComposeUIViewController
import com.scritch.app.app.App
import com.scritch.app.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) { App() }