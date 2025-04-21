package com.scritch.app.settings.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.scritch.app.settings.getAppVersionWithBuildNumber
import org.jetbrains.compose.resources.vectorResource
import scritch.composeapp.generated.resources.Res
import scritch.composeapp.generated.resources.scritch_logo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onBackPress: () -> Unit,
    onGoToVersionHistory: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text("About")
                },
                navigationIcon = {
                    IconButton(
                        content = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = "Back to home",
                            )
                        },
                        onClick = onBackPress
                    )
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                space = 8.dp,
                alignment = Alignment.CenterVertically,
            ),
            modifier = Modifier.consumeWindowInsets(innerPadding).fillMaxSize(),
        ) {
            item {
                Image(
                    modifier = Modifier.size(150.dp),
                    imageVector = vectorResource(Res.drawable.scritch_logo),
                    contentDescription = null,
                )
            }
            item {
                Text(
                    text = "Scritch",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            item {
                Spacer(
                    modifier = Modifier.height(16.dp)
                )
            }
            item {
                Text(
                    text = "Concept & Design: Coraline Janvier",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            item {
                Text(
                    text = "Development: Mario Ferreira Vilanova",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            item {
                Spacer(
                    modifier = Modifier.height(12.dp)
                )
            }
            item {
                TextButton(
                    onClick = onGoToVersionHistory,
                ) {
                    Text(
                        text = "Version: ${getAppVersionWithBuildNumber()}",
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}