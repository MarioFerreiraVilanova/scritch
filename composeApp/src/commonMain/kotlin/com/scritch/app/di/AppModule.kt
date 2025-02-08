package com.scritch.app.di

import com.scritch.app.app.AppViewModel
import com.scritch.app.auth.AuthenticationRepository
import com.scritch.app.landing.LandingViewModel
import com.scritch.app.wizard.WizardScreenViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::AppViewModel)
    viewModelOf(::LandingViewModel)
    viewModelOf(::WizardScreenViewModel)
    single { AuthenticationRepository() }
}