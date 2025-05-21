package com.scritch.app

import android.app.Application
import com.scritch.app.app.ApplicationContext
import org.koin.core.component.KoinComponent

class MyApp : Application(), KoinComponent {
    override fun onCreate() {
        super.onCreate()
        ApplicationContext.setUp(applicationContext)
    }
}