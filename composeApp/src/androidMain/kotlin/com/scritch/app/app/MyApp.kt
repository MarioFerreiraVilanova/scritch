package com.scritch.app.app

import android.app.Application
import com.scritch.app.di.initializeKoin
import org.koin.android.ext.koin.androidContext

class MyApp: Application() {

    override fun onCreate() {
        super.onCreate()
        initializeKoin {
            androidContext(this@MyApp)
        }
    }
}