package app.minimal.fasting.fasting.status.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.minimal.fasting.common.ui.LoadingScreen
import app.minimal.fasting.fasting.status.FastingStatusViewModel
import app.minimal.fasting.fasting.status.FastingStatusViewState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FastingStatusScreen(
    modifier: Modifier = Modifier,
    viewModel: FastingStatusViewModel = koinViewModel<FastingStatusViewModel>()
) {
    val viewState by viewModel.viewState.collectAsState()

    when (viewState){
        FastingStatusViewState.Loading -> LoadingScreen()
        is FastingStatusViewState.Loaded -> Loaded()
    }
}

@Composable
private fun Loaded(
    modifier: Modifier = Modifier,
){
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = "Fasting status screen",
        )
    }
}