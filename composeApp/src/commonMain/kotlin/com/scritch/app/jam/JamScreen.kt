package com.scritch.app.jam

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.scritch.app.prompt.Prompt
import com.scritch.app.prompt.TipsSheet
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import scritch.composeapp.generated.resources.Res
import scritch.composeapp.generated.resources.weekly_jam_description
import scritch.composeapp.generated.resources.weekly_jam_not_available

@Composable
fun JamScreen(
    modifier: Modifier = Modifier,
    viewModel: JamViewModel = koinViewModel(),
) {
    val viewState by viewModel.viewState.collectAsState()
    Scaffold(
        modifier = modifier,
    ) { contentPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .consumeWindowInsets(contentPadding)
                .padding(contentPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Text(
                    text = stringResource(Res.string.weekly_jam_description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
            viewState.endDate?.let { endDate ->
                item {
                    Text(
                        text = "The jam will end on ${endDate.dayOfWeek} at ${endDate.hour}:${endDate.minute}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }
            item {
                HorizontalDivider()
            }
            item {
                when (viewState.loadingState) {
                    LoadingState.LOADING -> {
                        CircularProgressIndicator()
                    }

                    LoadingState.NO_JAM -> {
                        Text(
                            text = stringResource(Res.string.weekly_jam_not_available),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }

                    LoadingState.LOADED,
                    LoadingState.REFRESHING -> {
                        Prompt(
                            viewState = viewState.promptViewState,
                            onCategoryClick = viewModel::onCategoryClick,
                        )
                    }
                }
            }
        }
    }

    TipsSheet(
        viewState = viewState.promptViewState,
        onTipDisplayed = viewModel::onTipDisplayed,
    )
}