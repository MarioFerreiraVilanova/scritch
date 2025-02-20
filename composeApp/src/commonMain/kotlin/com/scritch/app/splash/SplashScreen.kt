package com.scritch.app.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SplashScreen(
    onGoHome: () -> Unit,
    onGoToLanding: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SplashViewModel = koinViewModel(),
){
    val viewState by viewModel.viewState.collectAsState()

    when (viewState){
        SplashViewState.Loading -> { /* Do nothing, show splash */}
        SplashViewState.ToHome -> onGoHome()
        SplashViewState.ToLanding -> onGoToLanding()
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ){
        Text("Scritch")
    }
}