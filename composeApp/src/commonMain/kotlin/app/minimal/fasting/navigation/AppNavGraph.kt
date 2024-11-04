package app.minimal.fasting.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import app.minimal.fasting.app.AppViewModel
import app.minimal.fasting.app.AppViewState
import app.minimal.fasting.fasting.status.ui.FastingStatusScreen
import app.minimal.fasting.fasting.wizard.ui.FastingWizardScreen
import app.minimal.fasting.home.HomeScreen
import app.minimal.fasting.landing.LandingScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppNavGraph(
    viewModel: AppViewModel = koinViewModel<AppViewModel>(),
    navController: NavHostController = rememberNavController(),
){
    val viewState by viewModel.appViewState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = when (viewState){
            is AppViewState.Unauthenticated -> Unauthenticated
            is AppViewState.Authenticated -> Authenticated
        }
    ) {
        unauthenticatedSubGraph()
        authenticatedSubGraph()
    }
}

private fun NavGraphBuilder.unauthenticatedSubGraph() =
    navigation<Unauthenticated> (
        startDestination = Unauthenticated.LandingScreen,
    ) {
        composable<Unauthenticated.LandingScreen> {
            LandingScreen()
        }
    }

private fun NavGraphBuilder.authenticatedSubGraph() =
    navigation <Authenticated> (
        startDestination = Authenticated.FastingStatus,
    ){
        composable<Authenticated.HomeScreen> {
            HomeScreen()
        }
        composable<Authenticated.FastingStatus> {
            FastingStatusScreen()
        }
        composable<Authenticated.FastingWizard> {
            FastingWizardScreen()
        }
    }