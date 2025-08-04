package com.scritch.app.jam

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.scritch.app.prompt.Prompt
import com.scritch.app.prompt.TipsSheet
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun JamScreen(
    modifier: Modifier = Modifier,
    viewModel: JamViewModel = koinViewModel(),
) {
    val viewState by viewModel.viewState.collectAsState()
    Scaffold(
        modifier = modifier,
    ) { contentPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .consumeWindowInsets(contentPadding)
                .padding(contentPadding),
        ){
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp),
            ) {
                Text(
                    text = "Weekly jams are drawing challenges for all Scritch users. Updated every Sundays.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                HorizontalDivider()
                when (viewState.loadingState){
                    LoadingState.LOADING -> {
                        CircularProgressIndicator()
                    }
                    LoadingState.NO_JAM -> {
                        Text(
                            text = "No jams available",
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