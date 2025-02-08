package com.scritch.app.landing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LandingScreen(
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LandingViewModel = koinViewModel<LandingViewModel>(),
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar (
                title = {},
                actions = {
                    IconButton({}) {
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
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(
                        text = "Welcome to Scritch",
                        style = MaterialTheme.typography.h3,
                    )
                    Text(
                        text = "The app that will help you get inspirations your drawing and get " +
                                "you good habits to draw every day",
                        style = MaterialTheme.typography.body1,
                    )
                    Text(
                        text = "We will help you configure your app, it will take a few seconds.",
                        style = MaterialTheme.typography.body1,
                        fontWeight = FontWeight.Bold,
                    )
                }

            }
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd,
                ) {
                    Button(
                        onClick = onContinue,
                        content = {
                            Text("Continue")
                        }
                    )
                }
            }
        }
    }
}