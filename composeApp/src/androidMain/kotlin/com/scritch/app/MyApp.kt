package com.scritch.app

import android.app.Application
import org.koin.core.component.KoinComponent

class MyApp : Application(), KoinComponent {
    override fun onCreate() {
        super.onCreate()
    }
}