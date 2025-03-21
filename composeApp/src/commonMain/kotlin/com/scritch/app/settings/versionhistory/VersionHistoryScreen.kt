package com.scritch.app.settings.versionhistory

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VersionHistoryScreen(
    onBackPress: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val items = generateVersionHistory()
    Scaffold (
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text("Version history")
                },
                navigationIcon = {
                    IconButton(
                        content = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = "Back to settings",
                            )
                        },
                        onClick = onBackPress
                    )
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.consumeWindowInsets(paddingValues),
            contentPadding = paddingValues,
        ) {
            items(
                items = items,
            ){ item ->
                ListItem(
                    headlineContent = {
                        Text(item.first)
                    },
                    supportingContent = {
                        Text(item.second)
                    }
                )
            }
        }
    }
}

private fun generateVersionHistory(): List<Pair<String, String>> = listOf(
    "0.8" to "New onboarding screen, fixed titles on setting screens, new formatting for tips",
    "0.7" to "Themed the app black, styled prompt and added an app icon, fixed logout"
)