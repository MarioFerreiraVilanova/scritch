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
                    Text("Admin Panel")
                },
                navigationIcon = {
                    IconButton(
                        content = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = "Back",
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
                SectionTitle("Jam Management")
            }

            item {
                ListItem(
                    headlineContent = {
                        Text("Create New Jam")
                    },
                    supportingContent = {
                        Text(
                            text = "Create a new weekly jam with custom prompts and dates",
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
                SectionTitle("Moderation Tools")
            }

            item {
                ListItem(
                    headlineContent = {
                        Text("Moderation Queue")
                    },
                    supportingContent = {
                        Text(
                            text = "Review submissions requiring manual approval",
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