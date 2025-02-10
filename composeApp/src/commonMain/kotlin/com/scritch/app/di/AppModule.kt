package com.scritch.app.di

import com.scritch.app.app.AppViewModel
import com.scritch.app.auth.AuthenticationRepository
import com.scritch.app.categories.CategoryRepository
import com.scritch.app.landing.LandingViewModel
import com.scritch.app.wizard.WizardScreenViewModel
import com.scritch.app.home.HomeViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    singleOf(::AuthenticationRepository)
    singleOf(::CategoryRepository)

    viewModelOf(::AppViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::LandingViewModel)
    viewModelOf(::WizardScreenViewModel)
}