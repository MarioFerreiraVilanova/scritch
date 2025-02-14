package com.scritch.app.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.scritch.app.categories.Category
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterialApi::class)
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
                ListItem (
                    text = {
                        Text("Logout")
                    },
                    icon = {
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
        style = MaterialTheme.typography.subtitle2,
        modifier = Modifier.padding(16.dp)
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun CategorySettingsItem(
    category: Category,
    modifier: Modifier = Modifier,
) {
    ListItem(
        modifier = modifier,
        icon = {
            Icon(
                imageVector = when (category) {
                    Category.Medium -> Icons.Default.Edit
                    Category.Support -> Icons.Default.Notifications
                },
                contentDescription = null,
            )
        },
        text = {
            Text(
                when (category) {
                    Category.Medium -> "Mediums"
                    Category.Support -> "Supports"
                }
            )
        },
    )
}