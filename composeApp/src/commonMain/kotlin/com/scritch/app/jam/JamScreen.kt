package com.scritch.app.jam

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Try
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.scritch.app.prompt.Prompt
import com.scritch.app.prompt.TipsSheet
import com.scritch.app.uicomponents.Button
import com.scritch.app.util.dayOfWeekString
import io.github.ismoy.imagepickerkmp.ImagePickerConfig
import io.github.ismoy.imagepickerkmp.ImagePickerLauncher
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import scritch.composeapp.generated.resources.Res
import scritch.composeapp.generated.resources.camera
import scritch.composeapp.generated.resources.clock
import scritch.composeapp.generated.resources.share_your_work
import scritch.composeapp.generated.resources.weekly_jam_description
import scritch.composeapp.generated.resources.weekly_jam_end_time
import scritch.composeapp.generated.resources.weekly_jam_not_available

@Composable
fun JamScreen(
    modifier: Modifier = Modifier,
    viewModel: JamViewModel = koinViewModel(),
) {
    val viewState by viewModel.viewState.collectAsState()


    Scaffold(
        modifier = modifier,
    ) { contentPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .consumeWindowInsets(contentPadding)
                .padding(contentPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = stringResource(Res.string.weekly_jam_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            viewState.endDate?.let { endDate ->

                val dayOfWeek = dayOfWeekString(day = endDate.dayOfWeek)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.clock),
                        contentDescription = null,
                    )
                    Text(
                        text = stringResource(
                            Res.string.weekly_jam_end_time,
                            dayOfWeek,
                            "${endDate.hour}:${endDate.minute}"
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }
            HorizontalDivider()
            when (viewState.loadingState) {
                LoadingState.LOADING -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }

                LoadingState.NO_JAM -> {
                    Text(
                        text = stringResource(Res.string.weekly_jam_not_available),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }

                LoadingState.LOADED,
                LoadingState.REFRESHING -> {
                    Prompt(
                        viewState = viewState.promptViewState,
                        onCategoryClick = viewModel::onCategoryClick,
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            if (viewState.loadingState == LoadingState.LOADED) {
                SubmissionState(
                    viewState = viewState.submissionState,
                    onSubmitWork = viewModel::onSubmitWork,
                    onRetry = viewModel::onRetryUpload,
                    onShowPreview = viewModel::onShowPreview,
                )
            }
        }
    }

    if (viewState.showCamera) {
        ImagePickerLauncher(
            config = ImagePickerConfig(
                onPhotoCaptured = viewModel::onImageCaptured,
                onError = viewModel::onImageCaptureError,
                onDismiss = viewModel::onImageCaptureDismiss,
            )
        )
    }

    TipsSheet(
        viewState = viewState.promptViewState,
        onTipDisplayed = viewModel::onTipDisplayed,
    )

    when (viewState.dialog) {
        JamScreenDialog.SubmissionPreview -> {
            (viewState.submissionState as? SubmissionViewState.Submitted)?.let { submission ->
                SubmissionPreviewDialog(
                    viewState = submission,
                    onDismissRequest = {
                        viewModel.onDismissDialog(JamScreenDialog.SubmissionPreview)
                    },
                    onRetakeSubmission = viewModel::onSubmitWork,
                    onRemoveSubmission = viewModel::onRemoveSubmission,
                )
            }
        }
        JamScreenDialog.SubmissionDeleteConfirmation -> SubmissionDeleteConfirmationDialog(
            onDismissRequest = {
                viewModel.onDismissDialog(JamScreenDialog.SubmissionDeleteConfirmation)
            },
            onConfirm = viewModel::onRemoveSubmission,
        )
        null -> {}
    }
}

@Composable
private fun SubmissionState(
    viewState: SubmissionViewState,
    onSubmitWork: () -> Unit,
    onRetry: () -> Unit,
    onShowPreview: () -> Unit,
) {
    val animatedProgress by animateFloatAsState(
        targetValue = when (viewState) {
            is SubmissionViewState.ImageTakenLocally -> {
                when (viewState.uploadStatus) {
                    is SubmissionUploadState.Uploading -> viewState.uploadStatus.progress ?: 0f
                    SubmissionUploadState.Success -> 1f
                    else -> 0f
                }
            }

            else -> 0f
        }
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(
            8.dp,
            Alignment.CenterHorizontally
        ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        when (viewState) {
            is SubmissionViewState.ImageTakenLocally -> {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    when (viewState.uploadStatus) {
                        is SubmissionUploadState.Error -> FilledIconButton(
                            onClick = onRetry,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Try,
                                contentDescription = null,
                            )
                        }

                        SubmissionUploadState.Success -> CircularProgressIndicator()
                        is SubmissionUploadState.Uploading -> CircularProgressIndicator(
                            progress = {
                                animatedProgress
                            },
                        )
                    }
                    Text(
                        text = when (viewState.uploadStatus) {
                            is SubmissionUploadState.Error -> "Upload failed"
                            SubmissionUploadState.Success -> "Upload complete."
                            is SubmissionUploadState.Uploading -> "Uploading your image..."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }

            SubmissionViewState.NotSubmitted -> {
                Button(
                    onClick = onSubmitWork
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.camera),
                        contentDescription = null,
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(text = stringResource(Res.string.share_your_work))
                }
            }

            is SubmissionViewState.Submitted -> {
                TextButton(
                    onClick = onShowPreview
                ) {
                    Text(
                        text = "Edit your participation",
                    )
                    Spacer(
                        Modifier.width(8.dp)
                    )
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                    )
                }
            }
        }
    }
}

@Composable
private fun SubmissionPreviewDialog(
    viewState: SubmissionViewState.Submitted,
    onDismissRequest: () -> Unit,
    onRetakeSubmission: () -> Unit,
    onRemoveSubmission: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false, // so we can control size on large screens
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
        )
    ) {
        // Full-screen box just to center the card
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surfaceContainer,
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 560.dp)                 // cap width for tablets
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState()), // safety if content gets tall
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    KamelImage(
                        resource = { asyncPainterResource(viewState.imageUrl) },
                        contentDescription = "Submission preview",
                        contentScale = ContentScale.FillWidth,        // don’t fill; keep aspect
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(
                                min = 160.dp,
                                max = 420.dp
                            ), // cap height so button stays visible
                        onLoading = { progress ->
                            // progress in [0f..1f] or null
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 160.dp, max = 420.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(
                                    8.dp,
                                    Alignment.CenterVertically
                                )
                            ) {
                                val animatedProgress by animateFloatAsState(progress)
                                CircularProgressIndicator(
                                    progress = {
                                        animatedProgress
                                    }
                                )
                                Text(
                                    text = "Loading...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onBackground,
                                )
                            }
                        }
                    )

                    TextButton(onClick = onRetakeSubmission) {
                        Text("Take a different picture")
                        Spacer(
                            Modifier.width(8.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.Repeat,
                            contentDescription = null,
                        )
                    }
                    TextButton(onClick = onRemoveSubmission) {
                        Text(
                            text = "Delete",
                            color = MaterialTheme.colorScheme.error,
                        )
                        Spacer(
                            Modifier.width(8.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SubmissionDeleteConfirmationDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = "Delete submission")
        },
        text = {
            Text(text = "Are you sure you want to delete your submission?")
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = "Yes, delete it")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = "Cancel")
            }
        }
    )
}

