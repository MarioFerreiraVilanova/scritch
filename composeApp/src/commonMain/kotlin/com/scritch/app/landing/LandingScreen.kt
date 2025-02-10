package com.scritch.app.landing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.scritch.app.uicomponents.PageHeader
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
        when (event){
            LandingEvent.GoHome -> onGoHome()
            LandingEvent.GoToWizard -> onGoToWizard()
        }
        viewModel.consumeEvent(event)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    IconButton(
                        onClick = viewModel::onSkip,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = "menu items"
                        )
                    }
                }
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            item {
                PageHeader(
                    title = "Welcome to Scritch",
                    description = buildAnnotatedString {
                        append(
                            "The app that will help you get inspirations your drawing and get " +
                                    "you good habits to draw every day"
                        )
                        append("\n\n")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("We will help you configure your app, it will take a few seconds.")
                        }
                    }
                )

            }
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd,
                ) {
                    Button(
                        onClick = viewModel::onContinue,
                        content = {
                            Text("Continue")
                        }
                    )
                }
            }
        }
    }
}