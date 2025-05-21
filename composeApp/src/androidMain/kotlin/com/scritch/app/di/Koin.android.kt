package com.scritch.app.di

import org.koin.core.module.Module

actual object Koin {
    actual fun modules(): List<Module> {
        return listOf(appModule)
    }
}