package app.minimal.fasting

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.minimal.fasting.home.HomeScreen
import app.minimal.fasting.landing.LandingScreen
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
            startDestination = when (viewState.user){
                null -> Screen.Landing.name
                else -> Screen.Home.name
            }
        ) {
            composable( route = Screen.Landing.name ){
                LandingScreen()
            }

            composable( route = Screen.Home.name ){
                HomeScreen()
            }
        }
    }
}