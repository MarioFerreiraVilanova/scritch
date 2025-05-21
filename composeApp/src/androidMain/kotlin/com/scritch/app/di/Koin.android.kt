package com.scritch.app.di

import com.scritch.app.app.ContextProvider
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.Module
import org.koin.dsl.module

actual object Koin {
    actual fun modules(): List<Module> {
        return listOf(appModule, androidModule)
    }

    private val androidModule = module {
        factory { ContextProvider(androidApplication()) }
    }
}