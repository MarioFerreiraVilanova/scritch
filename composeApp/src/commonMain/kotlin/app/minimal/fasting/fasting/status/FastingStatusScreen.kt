package app.minimal.fasting.fasting.status

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.minimal.fasting.common.now
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
            viewState = viewState as FastingStatusViewState.Loaded,
            modifier = modifier,
        )

        FastingStatusViewState.NeedsSetup -> onNeedsSetup()
    }
}

@Composable
private fun Loaded(
    viewState: FastingStatusViewState.Loaded,
    modifier: Modifier = Modifier,
) {
    val text = when (viewState.window){
        is DayWindow.EatingViewState -> {
            viewState.window.fastStartingTime.let {
                if (it.date == now().date){
                    "Next fast starts at ${it.hour}:${it.minute}"
                } else {
                    "Next fast starts on ${it.dayOfWeek} at ${it.hour}:${it.minute}"
                }
            }
        }
        is DayWindow.FastingViewState -> "TODO"
    }
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            text = text,
            style = MaterialTheme.typography.h2
        )
    }
}