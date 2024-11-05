package app.minimal.fasting.fasting.wizard

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FastingWizardScreen(
    modifier: Modifier = Modifier,
    viewModel: FastingWizardViewModel = koinViewModel<FastingWizardViewModel>()
) {
    val viewState by viewModel.viewState.collectAsState()
    val pagerState = rememberPagerState(
        initialPage = viewState.currentPage.ordinal,
        pageCount = { WizardPage.entries.size },
    )

    HorizontalPager(
        modifier = modifier,
        state = pagerState,
    ) { page ->
        when (WizardPage.entries[page]) {
            WizardPage.Welcome -> Welcome(
                onNext = viewModel::onNext,
            )

            WizardPage.TimeToStart -> TODO()
            WizardPage.FastingGoal -> TODO()
            WizardPage.FastingDays -> TODO()
        }
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
private fun TimeToStart() {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column {
            Text (
                text = "When would you like to start fasting"
            )
        }
    }
}