package app.minimal.fasting.di

import app.minimal.fasting.app.AppViewModel
import app.minimal.fasting.landing.LandingViewModel
import app.minimal.fasting.home.HomeViewModel
import app.minimal.fasting.fasting.FastingRepository
import app.minimal.fasting.fasting.ui.FastingStatusViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::AppViewModel)
    viewModelOf(::LandingViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::FastingStatusViewModel)
    single { FastingRepository() }
}