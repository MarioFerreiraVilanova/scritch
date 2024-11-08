package app.minimal.fasting.di

import app.minimal.fasting.app.AppViewModel
import app.minimal.fasting.auth.AuthenticationRepository
import app.minimal.fasting.fasting.repository.FastingRepository
import app.minimal.fasting.fasting.start.StartFastingViewModel
import app.minimal.fasting.fasting.status.FastingStatusViewModel
import app.minimal.fasting.fasting.wizard.FastingWizardViewModel
import app.minimal.fasting.landing.LandingViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::AppViewModel)
    viewModelOf(::LandingViewModel)
    viewModelOf(::FastingStatusViewModel)
    viewModelOf(::FastingWizardViewModel)
    viewModelOf(::StartFastingViewModel)
    single { FastingRepository() }
    single { AuthenticationRepository() }
}