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
import com.scritch.app.wizard.WizardScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppNavGraph(
    viewModel: AppViewModel = koinViewModel<AppViewModel>(),
    navController: NavHostController = rememberNavController(),
) {
    val viewState by viewModel.appViewState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = when (viewState) {
            AppViewState.StatingApp ->
            is AppViewState.Unauthenticated -> Unauthenticated
            is AppViewState.Authenticated -> Authenticated
        }
    ) {
        unauthenticatedSubGraph()
        authenticatedSubGraph(
            viewState = viewState,
            navController = navController,
        )
    }
}

private fun NavGraphBuilder.unauthenticatedSubGraph() =
    navigation<Unauthenticated>(
        startDestination = Unauthenticated.LandingScreen,
    ) {
        composable<Unauthenticated.LandingScreen> {
            LandingScreen()
        }
    }

private fun NavGraphBuilder.authenticatedSubGraph(
    navController: NavHostController,
    viewState: AppViewState,
) =
    navigation<Authenticated>(
        startDestination = if ((viewState as? AppViewState.Authenticated)?.needsInitialSetup == true) {
            Authenticated.WizardMediumSelection.stepOne()
        } else {
            Authenticated.Home
        },
    ) {
        composable<Authenticated.Home> {
            HomeScreen()
        }

        composable<Authenticated.WizardMediumSelection> {
            WizardScreen(
                onBackClick = { navController.popBackStack() },
                onContinue = { currentStep ->
                    Authenticated.WizardMediumSelection.nextStep(currentStep)?.let {
                        navController.navigate(it)
                    } ?: navController.navigate(Authenticated.Home) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }