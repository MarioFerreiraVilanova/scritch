package com.scritch.app.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.scritch.app.app.AppViewModel
import com.scritch.app.app.AppViewState
import com.scritch.app.landing.LandingScreen
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
            is AppViewState.Unauthenticated -> Unauthenticated
            is AppViewState.Authenticated -> Authenticated
        }
    ) {
        unauthenticatedSubGraph()
        authenticatedSubGraph()
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

private fun NavGraphBuilder.authenticatedSubGraph() =
    navigation<Authenticated>(
        startDestination = Authenticated.Home,
    ) {
        composable<Authenticated.Home> {
            Box(modifier = Modifier.background(Color.Red).fillMaxSize())
        }
    }