package com.scritch.app.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.scritch.app.categories.Category
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import scritch.composeapp.generated.resources.Res
import scritch.composeapp.generated.resources.ellipsis
import scritch.composeapp.generated.resources.logout
import scritch.composeapp.generated.resources.pen
import scritch.composeapp.generated.resources.rectangle_vertical_history

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onLogOut: () -> Unit, // TODO remove this and use events
    onBackPress: () -> Unit,
    onCategoryPress: (Category) -> Unit,
    onGoToVersionHistory: () -> Unit,
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
                                contentDescription = "Back to home",
                            )
                        },
                        onClick = onBackPress
                    )
                }
            )
        },
        bottomBar = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth().navigationBarsPadding()
            ){
                TextButton(
                    onClick = onGoToVersionHistory,
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    Text(
                        text = getAppVersionWithBuildNumber(),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.consumeWindowInsets(innerPadding),
            contentPadding = innerPadding,
        ) {
            item {
                SectionTitle("Prompt Settings")
            }

            item {
                CategorySettingsItem(
                    category = Category.Medium,
                    modifier = Modifier.clickable {
                        onCategoryPress(Category.Medium)
                    },
                )
            }

            item {
                CategorySettingsItem(
                    category = Category.Support,
                    modifier = Modifier.clickable {
                        onCategoryPress(Category.Support)
                    },
                )
            }

            item {
                CategorySettingsItem(
                    category = Category.Constraint,
                    modifier = Modifier.clickable {
                        onCategoryPress(Category.Constraint)
                    },
                )
            }

            /*item {
                SectionTitle("Other")
            }

            item {
                ListItem(
                    headlineContent = {
                        Text("Logout")
                    },
                    leadingContent = {
                        Icon(
                            imageVector = vectorResource(Res.drawable.logout),
                            contentDescription = null,
                        )
                    },
                    modifier = Modifier.clickable {
                        onLogOut()
                    }
                )
            }*/
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
                    Category.Medium -> vectorResource(Res.drawable.pen)
                    Category.Support -> vectorResource(Res.drawable.rectangle_vertical_history)
                    Category.Topic -> Icons.Default.Person
                    Category.Constraint -> vectorResource(Res.drawable.ellipsis)
                },
                contentDescription = null,
            )
        },
        headlineContent = {
            Text(
                when (category) {
                    Category.Medium -> "Art mediums"
                    Category.Support -> "Supports"
                    Category.Topic -> "Topics"
                    Category.Constraint -> "Constraints"
                }
            )
        },
    )
}