package com.scritch.app.di

import com.scritch.app.analytics.AnalyticsRepository
import com.scritch.app.app.AppViewModel
import com.scritch.app.auth.AuthenticationRepository
import com.scritch.app.categories.CategoryRepository
import com.scritch.app.categories.LoadUserOptionsUseCase
import com.scritch.app.jam.data.JamRepository
import com.scritch.app.jam.JamViewModel
import com.scritch.app.solomode.SoloViewModel
import com.scritch.app.landing.LandingViewModel
import com.scritch.app.settings.about.AboutViewModel
import com.scritch.app.splash.SplashViewModel
import com.scritch.app.userdata.LoadUserDataUseCase
import com.scritch.app.userdata.UserDataRepository
import com.scritch.app.userprofile.UserProfileRepository
import com.scritch.app.wizard.WizardScreenViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    singleOf(::AnalyticsRepository)
    singleOf(::AuthenticationRepository)
    singleOf(::CategoryRepository)
    singleOf(::JamRepository)
    singleOf(::UserDataRepository)
    singleOf(::UserProfileRepository)

    viewModelOf(::AboutViewModel)
    viewModelOf(::AppViewModel)
    viewModelOf(::JamViewModel)
    viewModelOf(::LandingViewModel)
    viewModelOf(::SoloViewModel)
    viewModelOf(::SplashViewModel)
    viewModelOf(::WizardScreenViewModel)

    factoryOf(::LoadUserDataUseCase)
    factoryOf(::LoadUserOptionsUseCase)
}