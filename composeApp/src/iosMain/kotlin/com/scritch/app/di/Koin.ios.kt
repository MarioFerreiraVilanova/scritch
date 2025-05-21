package com.scritch.app.di

import com.scritch.app.util.EmailClient
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val targetModule = module {
    singleOf(::EmailClient)
}