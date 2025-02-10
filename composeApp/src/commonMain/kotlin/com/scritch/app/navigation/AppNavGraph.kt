package com.scritch.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.scritch.app.app.AppViewModel
import com.scritch.app.app.AppViewState
import com.scritch.app.home.HomeScreen
import com.scritch.app.landing.LandingScreen
import com.scritch.app.splash.SplashScreen
import com.scritch.app.wizard.WizardScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppNavGraph(
    viewModel: AppViewModel = koinViewModel<AppViewModel>(),
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = SplashScreen,
    ) {
        composable<SplashScreen> {
            SplashScreen(
                onGoHome = {
                    navController.goHome()
                },
                onGoToLanding = {
                    navController.goToLanding()
                }
            )
        }

        unauthenticatedSubGraph(
            navController = navController,
        )
        authenticatedSubGraph(
            navController = navController,
        )
    }
}

private fun NavGraphBuilder.unauthenticatedSubGraph(
    navController: NavHostController,
) =
    navigation<Unauthenticated>(
        startDestination = Unauthenticated.LandingScreen,
    ) {
        composable<Unauthenticated.LandingScreen> {
            LandingScreen(
                onGoHome = { navController.goHome() },
                onGoToWizard = { navController.navigate(Authenticated.WizardMediumSelection.stepOne())}
            )
        }
    }

private fun NavGraphBuilder.authenticatedSubGraph(
    navController: NavHostController,
) =
    navigation<Authenticated>(
        startDestination = Authenticated.Home,
    ) {
        composable<Authenticated.Home> {
            HomeScreen(
                onLogOut = {
                    navController.goToLanding()
                }
            )
        }

        composable<Authenticated.WizardMediumSelection> {
            WizardScreen(
                onBackClick = { navController.popBackStack() },
                onContinue = { currentStep ->
                    Authenticated.WizardMediumSelection.nextStep(currentStep)?.let {
                        navController.navigate(it)
                    } ?: navController.goHome()
                }
            )
        }
    }

// Helper functions

private fun NavHostController.goHome(){
    navigate(Authenticated.Home) {
        popUpTo(0) { inclusive = true }
    }
}

private fun NavHostController.goToLanding(){
    navigate(Unauthenticated.LandingScreen) {
        popUpTo(0) { inclusive = true }
    }
}