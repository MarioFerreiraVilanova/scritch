package app.minimal.fasting.fasting.status

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.minimal.fasting.common.now
import app.minimal.fasting.common.ui.LoadingScreen
import app.minimal.fasting.theme.MinimalTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FastingStatusScreen(
    modifier: Modifier = Modifier,
    viewModel: FastingStatusViewModel = koinViewModel<FastingStatusViewModel>(),
    onNeedsSetup: () -> Unit,
    onStartFasting: () -> Unit,
    onFinishFasting: () -> Unit,
) {
    val viewState by viewModel.viewState.collectAsState()

    when (viewState) {
        FastingStatusViewState.Loading -> LoadingScreen(
            modifier = modifier,
        )

        is FastingStatusViewState.Loaded -> Loaded(
            viewState = viewState as FastingStatusViewState.Loaded,
            onStartFasting = onStartFasting,
            onFinishFasting = onFinishFasting,
            modifier = modifier,
        )

        FastingStatusViewState.NeedsSetup -> onNeedsSetup()
    }
}

@Composable
private fun Loaded(
    viewState: FastingStatusViewState.Loaded,
    onStartFasting: () -> Unit,
    onFinishFasting: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val text = when (viewState.window) {
        is DayWindow.EatingViewState -> {
            viewState.window.startingTime.let {
                if (it.date == now().date) {
                    if (it < now()) {
                        "Next fast starts at ${it.hour}:${it.minute}"
                    } else {
                        "Time to fast!"
                    }
                } else {
                    "Next fast starts on ${it.dayOfWeek} at ${it.hour}:${it.minute}"
                }
            }
        }

        is DayWindow.FastingViewState -> {
            // TODO calculate for how long the user has been fasting
            // or maybe do that in the view model and put it in the view state
            "You've been fasting for 12h and 45m"
        }
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                MinimalTheme.color.surface04,
            ),
    ) {

    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = text,
            style = MinimalTheme.typography.h3,
            color = MinimalTheme.color.typography2,
        )

        when (viewState.window) {
            is DayWindow.EatingViewState -> Button(
                content = {
                    Text("Start fasting")
                },
                onClick = onStartFasting,
            )

            is DayWindow.FastingViewState -> Button(
                content = {
                    Text("Finish fasting")
                },
                onClick = onFinishFasting,
            )
        }

    }
}