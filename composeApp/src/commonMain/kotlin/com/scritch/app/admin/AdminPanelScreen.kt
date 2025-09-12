package com.scritch.app.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import scritch.composeapp.generated.resources.Res
import scritch.composeapp.generated.resources.admin_panel
import scritch.composeapp.generated.resources.back
import scritch.composeapp.generated.resources.jam_management
import scritch.composeapp.generated.resources.create_new_jam
import scritch.composeapp.generated.resources.create_new_jam_description
import scritch.composeapp.generated.resources.moderation_tools
import scritch.composeapp.generated.resources.moderation_queue
import scritch.composeapp.generated.resources.review_submissions_manual_approval_description

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    onBackPress: () -> Unit,
    onGoToModerationQueue: () -> Unit,
    onGoToCreateJam: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(Res.string.admin_panel))
                },
                navigationIcon = {
                    IconButton(
                        content = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = stringResource(Res.string.back),
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
        ) {
            item {
                SectionTitle(stringResource(Res.string.jam_management))
            }

            item {
                ListItem(
                    headlineContent = {
                        Text(stringResource(Res.string.create_new_jam))
                    },
                    supportingContent = {
                        Text(
                            text = stringResource(Res.string.create_new_jam_description),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                        )
                    },
                    modifier = Modifier.clickable {
                        onGoToCreateJam()
                    }
                )
            }

            item {
                SectionTitle(stringResource(Res.string.moderation_tools))
            }

            item {
                ListItem(
                    headlineContent = {
                        Text(stringResource(Res.string.moderation_queue))
                    },
                    supportingContent = {
                        Text(
                            text = stringResource(Res.string.review_submissions_manual_approval_description),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.Gavel,
                            contentDescription = null,
                        )
                    },
                    modifier = Modifier.clickable {
                        onGoToModerationQueue()
                    }
                )
            }

            // Future admin tools can be added here
            // item {
            //     ListItem(
            //         headlineContent = {
            //             Text("User Management")
            //         },
            //         supportingContent = {
            //             Text("Manage user accounts and permissions")
            //         },
            //         leadingContent = {
            //             Icon(Icons.Default.People, contentDescription = null)
            //         },
            //         modifier = Modifier.clickable { /* TODO */ }
            //     )
            // }
        }
    }
}

@Composable
private fun SectionTitle(
    title: String,
) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier.padding(16.dp)
    )
}