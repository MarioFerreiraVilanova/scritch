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
import org.jetbrains.compose.resources.stringResource
import scritch.composeapp.generated.resources.Res
import scritch.composeapp.generated.resources.back_to_settings
import scritch.composeapp.generated.resources.version_history
import scritch.composeapp.generated.resources.version_notes_0_10
import scritch.composeapp.generated.resources.version_notes_0_11
import scritch.composeapp.generated.resources.version_notes_0_12
import scritch.composeapp.generated.resources.version_notes_0_13
import scritch.composeapp.generated.resources.version_notes_0_14
import scritch.composeapp.generated.resources.version_notes_0_15
import scritch.composeapp.generated.resources.version_notes_0_18
import scritch.composeapp.generated.resources.version_notes_0_7
import scritch.composeapp.generated.resources.version_notes_0_8
import scritch.composeapp.generated.resources.version_notes_0_9

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
                    Text(stringResource(Res.string.version_history))
                },
                navigationIcon = {
                    IconButton(
                        content = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = stringResource(Res.string.back_to_settings),
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

@Composable
private fun generateVersionHistory(): List<Pair<String, String>> = listOf(
    "0.18" to stringResource(Res.string.version_notes_0_18),
    "0.15" to stringResource(Res.string.version_notes_0_15),
    "0.14" to stringResource(Res.string.version_notes_0_14),
    "0.13" to stringResource(Res.string.version_notes_0_13),
    "0.12" to stringResource(Res.string.version_notes_0_12),
    "0.11" to stringResource(Res.string.version_notes_0_11),
    "0.10" to stringResource(Res.string.version_notes_0_10),
    "0.9" to stringResource(Res.string.version_notes_0_9),
    "0.8" to stringResource(Res.string.version_notes_0_8),
    "0.7" to stringResource(Res.string.version_notes_0_7),
)