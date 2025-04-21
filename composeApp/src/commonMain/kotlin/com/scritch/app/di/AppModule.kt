package com.scritch.app.di

import com.scritch.app.analytics.AnalyticsRepository
import com.scritch.app.app.AppViewModel
import com.scritch.app.auth.AuthenticationRepository
import com.scritch.app.categories.CategoryRepository
import com.scritch.app.categories.LoadUserOptionsUseCase
import com.scritch.app.home.HomeViewModel
import com.scritch.app.landing.LandingViewModel
import com.scritch.app.splash.SplashViewModel
import com.scritch.app.userdata.LoadUserDataUseCase
import com.scritch.app.userdata.UserDataRepository
import com.scritch.app.wizard.WizardScreenViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    singleOf(::AnalyticsRepository)
    singleOf(::AuthenticationRepository)
    singleOf(::CategoryRepository)
    singleOf(::UserDataRepository)

    viewModelOf(::AppViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::LandingViewModel)
    viewModelOf(::SplashViewModel)
    viewModelOf(::WizardScreenViewModel)

    factoryOf(::LoadUserDataUseCase)
    factoryOf(::LoadUserOptionsUseCase)
}