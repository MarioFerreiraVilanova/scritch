package com.scritch.app.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Info
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
import com.scritch.app.categories.Category
import com.scritch.app.categories.toTitle
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import scritch.composeapp.generated.resources.Res
import scritch.composeapp.generated.resources.about
import scritch.composeapp.generated.resources.back_to_home
import scritch.composeapp.generated.resources.ellipsis
import scritch.composeapp.generated.resources.other
import scritch.composeapp.generated.resources.pen
import scritch.composeapp.generated.resources.prompt_settings
import scritch.composeapp.generated.resources.rectangle_vertical_history
import scritch.composeapp.generated.resources.settings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onLogOut: () -> Unit, // TODO remove this and use events
    onBackPress: () -> Unit,
    onCategoryPress: (Category) -> Unit,
    onGoToAbout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(Res.string.settings))
                },
                navigationIcon = {
                    IconButton(
                        content = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = stringResource(Res.string.back_to_home),
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
                SectionTitle(stringResource(Res.string.prompt_settings))
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

            item {
                SectionTitle(stringResource(Res.string.other))
            }

            item {
                ListItem(
                    headlineContent = {
                        Text(stringResource(Res.string.about))
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null,
                        )
                    },
                    modifier = Modifier.clickable {
                        onGoToAbout()
                    }
                )
            }

            /*item {
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
            Text( category.toTitle(plural = true))
        },
    )
}