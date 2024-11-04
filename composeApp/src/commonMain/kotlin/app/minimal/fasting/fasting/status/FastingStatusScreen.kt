package app.minimal.fasting.fasting.status

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.minimal.fasting.common.ui.LoadingScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FastingStatusScreen(
    modifier: Modifier = Modifier,
    viewModel: FastingStatusViewModel = koinViewModel<FastingStatusViewModel>(),
    onNeedsSetup: () -> Unit,
) {
    val viewState by viewModel.viewState.collectAsState()

    when (viewState) {
        FastingStatusViewState.Loading -> LoadingScreen(
            modifier = modifier,
        )

        is FastingStatusViewState.Loaded -> Loaded(
            modifier = modifier,
        )

        FastingStatusViewState.NeedsSetup -> onNeedsSetup()
    }
}

@Composable
private fun Loaded(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = "Fasting status screen",
        )
    }
}