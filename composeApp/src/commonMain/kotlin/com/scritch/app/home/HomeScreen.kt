package com.scritch.app.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    onGoToSettings: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val viewState by viewModel.viewState.collectAsState()

    val prompt = promptFromViewState(
        viewState = viewState,
    )

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text("Scritch")
                },
                actions = {
                    IconButton(
                        onClick = onGoToSettings,
                        content = {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                            )
                        }
                    )
                }
            )
        }
    ) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                if (prompt != null) {
                    Text(
                        text = prompt,
                        style = MaterialTheme.typography.h4,
                    )
                }
                Button(
                    content = { Text("Generate prompt") },
                    onClick = {
                        viewModel.onGeneratePrompt()
                    },
                )
            }
        }
    }
}

@Composable
private fun promptFromViewState(
    viewState: HomeScreenViewState,
): String? {
    if (viewState.medium == null && viewState.support == null) return null

    val subjectText = "Draw 5 objects"
    val mediumText = if (viewState.medium == null) {
        "with a medium of your choice"
    } else {
        "with a ${viewState.medium.name}"
    }
    val supportText = if (viewState.support == null) {
        "with the support of your choice"
    } else {
        "on a ${viewState.support.name}"
    }

    return "$subjectText, $mediumText, $supportText"
}