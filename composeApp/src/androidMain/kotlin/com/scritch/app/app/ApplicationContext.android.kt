package com.scritch.app.app

import android.content.Context
import com.scritch.app.MyApp

actual object ApplicationContext {
    private lateinit var application: MyApp

    fun setUp(context: Context) {
        application = context as MyApp
    }

    fun get(): Context {
        if (::application.isInitialized.not()) throw Exception("Application context isn't initialized")
        return application.applicationContext
    }
}