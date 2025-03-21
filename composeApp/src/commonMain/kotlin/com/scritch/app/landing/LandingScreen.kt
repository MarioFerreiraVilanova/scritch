package com.scritch.app.landing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LandingScreen(
    onGoHome: () -> Unit,
    onGoToWizard: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LandingViewModel = koinViewModel<LandingViewModel>(),
) {
    val viewState by viewModel.viewState.collectAsState()
    viewState.events.firstOrNull()?.let { event ->
        when (event) {
            LandingEvent.GoHome -> onGoHome()
            LandingEvent.GoToWizard -> onGoToWizard()
        }
        viewModel.consumeEvent(event)
    }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceContainer),
                contentAlignment = Alignment.Center,
            ) {
                Button(
                    modifier = Modifier.padding(
                        top = 16.dp,
                        bottom = 48.dp,
                    ),
                    onClick = viewModel::onSkip,
                    content = {
                        Text("Continue")
                    },
                    shape = MaterialTheme.shapes.small,
                )
            }
        }
    ) { innerPadding ->
        val descriptionStyle = MaterialTheme.typography.bodyMedium
        val highlightedStyle = descriptionStyle.copy(
            color = MaterialTheme.colorScheme.primary,
        ).toSpanStyle()
        LazyColumn(
            modifier = Modifier.fillMaxSize().consumeWindowInsets(innerPadding),
            contentPadding = PaddingValues(
                start = innerPadding.calculateStartPadding(LayoutDirection.Ltr) + 16.dp,
                top = innerPadding.calculateTopPadding() + 16.dp,
                end = innerPadding.calculateEndPadding(LayoutDirection.Ltr) + 16.dp,
                bottom = innerPadding.calculateBottomPadding() + 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            item {
                Text(
                    modifier = Modifier.padding(top = 32.dp),
                    text = "Looking for inspiration and develop new skills?",
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onBackground,
                )

            }
            item {
                Text(
                    style = descriptionStyle,
                    text = buildAnnotatedString {
                        append("Scritch is an app designed to inspire your drawings and ")
                        withStyle(
                            style = highlightedStyle,
                        ) {
                            append("help you develop new skills ")
                        }
                        append("by challenging you with creative constraints that ")
                        withStyle(
                            style = highlightedStyle,
                        ) {
                            append("push you beyond your comfort zone")
                        }
                        append(".")
                    }
                )
            }
            item {
                Text(
                    style = descriptionStyle,
                    text = buildAnnotatedString {
                        append(
                            "The challenges mainly involve using different art mediums and " +
                                    "supports. Don’t have or want to use some? No worries, "
                        )
                        withStyle(
                            style = highlightedStyle,
                        ) {
                            append("you can customize everything in the settings!")
                        }
                    }
                )
            }
        }
    }
}