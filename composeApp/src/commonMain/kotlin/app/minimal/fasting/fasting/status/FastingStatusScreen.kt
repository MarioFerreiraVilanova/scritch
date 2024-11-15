package app.minimal.fasting.fasting.status

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import app.minimal.fasting.common.now
import app.minimal.fasting.common.toHoursAndMinutes
import app.minimal.fasting.common.toTimestamp
import app.minimal.fasting.common.ui.LoadingScreen
import app.minimal.fasting.theme.MinimalTheme
import app.minimal.fasting.theme.components.ProgressBar
import app.minimal.fasting.theme.components.TextButton
import dev.gitlive.firebase.firestore.toDuration
import kotlinx.coroutines.delay
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
    val reference = when (viewState.window) {
        is DayWindow.EatingViewState -> viewState.window.startingTime
        is DayWindow.FastingViewState -> viewState.window.startingTime
    }

    var remainingTime by remember {
        mutableStateOf(now().toTimestamp().toDuration().minus(reference.toTimestamp().toDuration()))
    }

    var isRunning by remember { mutableStateOf(false) }
    LifecycleResumeEffect(Unit) {
        isRunning = true
        onPauseOrDispose { isRunning = false }
    }

    LaunchedEffect(isRunning) {
        while (isRunning) {
            remainingTime =
                now().toTimestamp().toDuration().minus(reference.toTimestamp().toDuration())
            delay(500)
        }
    }

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
            val timePassed =
                remainingTime.toComponents { hours, minutes, seconds, _ -> "${hours}h and ${minutes}m" }
            when (remainingTime.inWholeHours) {
                else -> "you've been fasting for $timePassed."
            }
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

        Column {
            BasicText(
                modifier = Modifier.fillMaxWidth(),
                text = text,
                style = MinimalTheme.typography.h3.copy(
                    color = MinimalTheme.color.typography2,
                ),
            )

            Spacer(modifier = Modifier.height(124.dp))

            (viewState.window as? DayWindow.FastingViewState)?.let {
                FastingProgress(
                    goal = viewState.goal,
                    progressInMinutes = remainingTime.inWholeMinutes.toInt(),
                    viewState = it,
                )
            }
        }

        when (viewState.window) {
            is DayWindow.EatingViewState -> Button(
                content = {
                    Text("Start fasting")
                },
                onClick = onStartFasting,
            )

            is DayWindow.FastingViewState -> TextButton(
                label = "end fast.",
                onClick = onFinishFasting,
                modifier = Modifier.align(Alignment.End)
            )
        }

    }
}

@Composable
private fun FastingProgress(
    goal: Int,
    progressInMinutes: Int,
    viewState: DayWindow.FastingViewState,
) {
    val safetyZone = 12f/goal
    val progress = progressInMinutes / (goal.times(60))

    ProgressBar(
        progress = safetyZone,
        circleProgress = progress.toFloat(),
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = 4.dp,
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        BasicText(
            text = viewState.startingTime.toHoursAndMinutes(),
            style = MinimalTheme.typography.h6.copy(
                color = MinimalTheme.color.typography4,
            ),
        )

        BasicText(
            text = viewState.endingTime.toHoursAndMinutes(),
            style = MinimalTheme.typography.h6.copy(
                color = MinimalTheme.color.typography4,
            ),
        )
    }
}