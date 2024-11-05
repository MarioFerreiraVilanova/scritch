@file:OptIn(ExperimentalFoundationApi::class)

package app.minimal.fasting.fasting.wizard

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Button
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalTime
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FastingWizardScreen(
    modifier: Modifier = Modifier,
    viewModel: FastingWizardViewModel = koinViewModel<FastingWizardViewModel>()
) {
    val viewState by viewModel.viewState.collectAsState()

    when (viewState.currentPage) {
        WizardPage.Welcome -> Welcome(
            onNext = viewModel::onNext,
        )

        WizardPage.TimeToStart -> TimeToStart(
            startingTime = LocalTime(
                hour = viewState.fastingPrefs.startingTimeHour,
                minute = viewState.fastingPrefs.startingTimeMinute,
            ),
            onTimeSelected = { selectedTime ->
                viewModel.onNext(
                    selectedTime = selectedTime,
                )
            }
        )
        WizardPage.FastingGoal -> TODO()
        WizardPage.FastingDays -> TODO()
    }
}

@Composable
private fun Welcome(
    onNext: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Text(
            text = "Welcome to Minimal Fasting. Before you can jump into the app we need to do" +
                    " a quick setup",
            modifier = Modifier.align(Alignment.Center)
        )

        Button(
            onClick = onNext,
            content = {
                Text("Let's go")
            },
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun TimeToStart(
    startingTime: LocalTime,
    onTimeSelected: (LocalTime) -> Unit
) {
    var selectedTime by remember {
        mutableStateOf(startingTime)
    }
    val hourLazyState = rememberLazyListState(
        initialFirstVisibleItemIndex = startingTime.hour
    )
    val minuteLazyState = rememberLazyListState(
        initialFirstVisibleItemIndex = startingTime.minute
    )
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Text(
            text = "When would you like to start fasting",
            modifier = Modifier.align(Alignment.TopCenter),
        )

        Column (
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
        ){
            LazyRow (
                state = hourLazyState,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(
                    count = 24,
                ){ index ->
                    Text(
                        text = index.toString(),
                        style = MaterialTheme.typography.h1,
                        modifier = Modifier.clickable {
                            selectedTime = LocalTime(
                                hour = index,
                                minute = selectedTime.minute,
                            )
                        }.alpha(if (selectedTime.hour == index) 1f else .5f)
                    )
                }
            }
            LazyRow (
                state = minuteLazyState,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(
                    count = 60,
                ){ index ->
                    Text(
                        text = index.toString(),
                        style = MaterialTheme.typography.h1,
                        modifier = Modifier.clickable {
                            selectedTime = LocalTime(
                                hour = selectedTime.hour,
                                minute = index,
                            )
                        }.alpha(if (selectedTime.minute == index) 1f else .5f)
                    )
                }
            }
        }
        Button(
            onClick = {
                onTimeSelected(
                    LocalTime(
                        hour = selectedTime.hour,
                        minute = selectedTime.minute,
                    )
                )
            },
            content = {
                Text("Start fasting at ${selectedTime.hour}:${selectedTime.minute}")
            },
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}