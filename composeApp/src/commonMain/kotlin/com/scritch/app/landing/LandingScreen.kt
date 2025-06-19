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
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import scritch.composeapp.generated.resources.Res
import scritch.composeapp.generated.resources.continue_word
import scritch.composeapp.generated.resources.landing_screen_description_1
import scritch.composeapp.generated.resources.landing_screen_description_2
import scritch.composeapp.generated.resources.landing_screen_description_3
import scritch.composeapp.generated.resources.landing_screen_description_4
import scritch.composeapp.generated.resources.landing_screen_description_5
import scritch.composeapp.generated.resources.landing_screen_header
import scritch.composeapp.generated.resources.landing_screen_second_description_1
import scritch.composeapp.generated.resources.landing_screen_second_description_2

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
                        Text(stringResource(Res.string.continue_word))
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
                    text = stringResource(Res.string.landing_screen_header),
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onBackground,
                )

            }
            item {
                Text(
                    style = descriptionStyle,
                    text = buildAnnotatedString {
                        append(stringResource(Res.string.landing_screen_description_1))
                        withStyle(
                            style = highlightedStyle,
                        ) {
                            append(stringResource(Res.string.landing_screen_description_2))
                        }
                        append(stringResource(Res.string.landing_screen_description_3))
                        withStyle(
                            style = highlightedStyle,
                        ) {
                            append(stringResource(Res.string.landing_screen_description_4))
                        }
                        append(stringResource(Res.string.landing_screen_description_5))
                    }
                )
            }
            item {
                Text(
                    style = descriptionStyle,
                    text = buildAnnotatedString {
                        append(stringResource(Res.string.landing_screen_second_description_1))
                        withStyle(
                            style = highlightedStyle,
                        ) {
                            append(stringResource(Res.string.landing_screen_second_description_2))
                        }
                    }
                )
            }
        }
    }
}