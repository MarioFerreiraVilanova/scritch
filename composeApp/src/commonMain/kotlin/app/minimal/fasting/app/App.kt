package app.minimal.fasting.app

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import app.minimal.fasting.home.HomeScreen
import app.minimal.fasting.landing.LandingScreen
import app.minimal.fasting.navigation.Authenticated
import app.minimal.fasting.navigation.Unauthenticated
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App(
    viewModel: AppViewModel = koinViewModel<AppViewModel>(),
    navController: NavHostController = rememberNavController()
) {
    MaterialTheme {
        val viewState by viewModel.appViewState.collectAsState()

        NavHost(
            navController = navController,
            startDestination = when (viewState){
                is AppViewState.Unauthenticated -> Unauthenticated
                is AppViewState.Authenticated -> Authenticated
            }
        ) {
            navigation<Unauthenticated> (
                startDestination = Unauthenticated.LandingScreen,
            ) {
                composable<Unauthenticated.LandingScreen> {
                    LandingScreen()
                }
            }

            navigation <Authenticated> (
                startDestination = Authenticated.HomeScreen,
            ){
                composable<Authenticated.HomeScreen> {
                    HomeScreen()
                }
            }
        }
    }
}