package com.scritch.app.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Image
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
import scritch.composeapp.generated.resources.manage_existing_jams
import scritch.composeapp.generated.resources.manage_existing_jams_description
import scritch.composeapp.generated.resources.moderation_tools
import scritch.composeapp.generated.resources.moderation_queue
import scritch.composeapp.generated.resources.review_submissions_manual_approval_description
import scritch.composeapp.generated.resources.system_tools
import scritch.composeapp.generated.resources.batch_generate_thumbnails
import scritch.composeapp.generated.resources.batch_generate_thumbnails_description
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    onBackPress: () -> Unit,
    onGoToModerationQueue: () -> Unit,
    onGoToCreateJam: () -> Unit,
    onGoToJamManagement: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AdminPanelViewModel = koinViewModel(),
) {
    val viewState by viewModel.viewState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Show success/error messages
    LaunchedEffect(viewState.message, viewState.error) {
        viewState.message?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearMessage()
        }
        viewState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                ListItem(
                    headlineContent = {
                        Text(stringResource(Res.string.manage_existing_jams))
                    },
                    supportingContent = {
                        Text(
                            text = stringResource(Res.string.manage_existing_jams_description),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.List,
                            contentDescription = null,
                        )
                    },
                    modifier = Modifier.clickable {
                        onGoToJamManagement()
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

            item {
                SectionTitle(stringResource(Res.string.system_tools))
            }

            item {
                ListItem(
                    headlineContent = {
                        Text(stringResource(Res.string.batch_generate_thumbnails))
                    },
                    supportingContent = {
                        Text(
                            text = stringResource(Res.string.batch_generate_thumbnails_description),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                    leadingContent = {
                        if (viewState.isGeneratingThumbnails) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = null,
                            )
                        }
                    },
                    modifier = Modifier.clickable(enabled = !viewState.isGeneratingThumbnails) {
                        viewModel.batchGenerateThumbnails()
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