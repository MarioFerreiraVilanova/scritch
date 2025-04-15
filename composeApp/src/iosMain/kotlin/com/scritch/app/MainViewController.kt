package com.scritch.app

import androidx.compose.ui.window.ComposeUIViewController
import com.scritch.app.app.App
import com.scritch.app.di.initKoin
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.initialize

fun MainViewController() = ComposeUIViewController(
    configure = {
        Firebase.initialize()
        //initKoin()
    }
) { App() }