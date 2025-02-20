package com.scritch.app.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.scritch.app.categories.Category
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onLogOut: () -> Unit, // TODO remove this and use events
    onBackPress: () -> Unit,
    onCategoryPress: (Category) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text("Settings")
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
        },
        bottomBar = {
            Text(
                text = "Version 0.3",
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )
        }
    ) {
        LazyColumn {
            item {
                SectionTitle("Prompt Settings")
            }

            items(
                items = Category.entries.toTypedArray()
            ) { category ->
                CategorySettingsItem(
                    category = category,
                    modifier = Modifier.clickable {
                        onCategoryPress(category)
                    },
                )
            }

            item {
                SectionTitle("Other")
            }

            item {
                ListItem(
                    headlineContent = {
                        Text("Logout")
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                        )
                    },
                    modifier = Modifier.clickable {
                        onLogOut()
                    }
                )
            }
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

@Composable
private fun CategorySettingsItem(
    category: Category,
    modifier: Modifier = Modifier,
) {
    ListItem(
        modifier = modifier,
        leadingContent = {
            Icon(
                imageVector = when (category) {
                    Category.Medium -> Icons.Default.Edit
                    Category.Support -> Icons.Default.Notifications
                },
                contentDescription = null,
            )
        },
        headlineContent = {
            Text(
                when (category) {
                    Category.Medium -> "Mediums"
                    Category.Support -> "Supports"
                }
            )
        },
    )
}