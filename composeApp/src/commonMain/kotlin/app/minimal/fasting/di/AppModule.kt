package app.minimal.fasting.di

import app.minimal.fasting.AppViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::AppViewModel)
}