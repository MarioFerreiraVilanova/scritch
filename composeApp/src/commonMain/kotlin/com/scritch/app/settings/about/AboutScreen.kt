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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.scritch.app.settings.getAppVersionWithBuildNumber
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import scritch.composeapp.generated.resources.Res
import scritch.composeapp.generated.resources.about
import scritch.composeapp.generated.resources.app_name
import scritch.composeapp.generated.resources.back_to_home
import scritch.composeapp.generated.resources.back_to_settings
import scritch.composeapp.generated.resources.concept_and_design
import scritch.composeapp.generated.resources.development
import scritch.composeapp.generated.resources.feedback_questions
import scritch.composeapp.generated.resources.scritch_logo
import scritch.composeapp.generated.resources.send_us_an_email
import scritch.composeapp.generated.resources.version

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onBackPress: () -> Unit,
    onGoToVersionHistory: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AboutViewModel = koinViewModel()
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(Res.string.about))
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
                    text = stringResource(Res.string.app_name),
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
                    text = stringResource(Res.string.concept_and_design, "Coraline Janvier"),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            item {
                Text(
                    text = stringResource(Res.string.development, "Mario Ferreira Vilanova"),
                    style = MaterialTheme.typography.bodyLarge,
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
                    text = stringResource(Res.string.feedback_questions),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            item {
                OutlinedButton(
                    onClick = viewModel::onSendFeedback,
                ) {
                    Text(
                        text = stringResource(Res.string.send_us_an_email),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
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
                        text = stringResource(Res.string.version, getAppVersionWithBuildNumber()),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}