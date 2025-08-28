package com.scritch.app.jam.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.uri.Uri
import androidx.core.uri.UriUtils
import com.scritch.app.jam.SubmissionUploadState
import com.scritch.app.jam.SubmissionViewState
import com.scritch.app.uicomponents.Button
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import com.scritch.app.theme.scritchColorScheme
import com.scritch.app.theme.scritchShapes
import com.scritch.app.theme.scritchTypography
import org.jetbrains.compose.ui.tooling.preview.Preview
import scritch.composeapp.generated.resources.Res
import scritch.composeapp.generated.resources.camera
import scritch.composeapp.generated.resources.cancel
import scritch.composeapp.generated.resources.retry
import scritch.composeapp.generated.resources.share_your_work
import scritch.composeapp.generated.resources.upload_complete
import scritch.composeapp.generated.resources.upload_failed
import scritch.composeapp.generated.resources.uploading_your_image

@Composable
fun SubmissionActions(
    viewState: SubmissionViewState,
    onSubmitWork: () -> Unit,
    onRetry: () -> Unit,
    onCancelUpload: () -> Unit,
    modifier: Modifier = Modifier,
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
        modifier = modifier,
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
                        is SubmissionUploadState.Error -> Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            FilledTonalIconButton(
                                onClick = onCancelUpload,
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = stringResource(Res.string.cancel),
                                )
                            }
                            FilledIconButton(
                                onClick = onRetry,
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Repeat,
                                    contentDescription = stringResource(Res.string.retry),
                                )
                            }
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
                            is SubmissionUploadState.Error -> stringResource(Res.string.upload_failed)
                            SubmissionUploadState.Success -> stringResource(Res.string.upload_complete)
                            is SubmissionUploadState.Uploading -> stringResource(Res.string.uploading_your_image)
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

            is SubmissionViewState.Submitted -> {}
        }
    }
}

@Preview
@Composable
private fun SubmissionActionsNotSubmittedPreview() {
    MaterialTheme(
        colorScheme = scritchColorScheme,
        typography = scritchTypography(),
        shapes = scritchShapes,
    ) {
        SubmissionActions(
            viewState = SubmissionViewState.NotSubmitted,
            onSubmitWork = {},
            onRetry = {},
            onCancelUpload = {},
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        )
    }
}

@Preview
@Composable
private fun SubmissionActionsUploadingPreview() {
    MaterialTheme(
        colorScheme = scritchColorScheme,
        typography = scritchTypography(),
        shapes = scritchShapes,
    ) {
        SubmissionActions(
            viewState = SubmissionViewState.ImageTakenLocally(
                imageUri = UriUtils.parse("content://example.jpg"),
                uploadStatus = SubmissionUploadState.Uploading(0.5f)
            ),
            onSubmitWork = {},
            onRetry = {},
            onCancelUpload = {},
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        )
    }
}

@Preview
@Composable
private fun SubmissionActionsErrorPreview() {
    MaterialTheme(
        colorScheme = scritchColorScheme,
        typography = scritchTypography(),
        shapes = scritchShapes,
    ) {
        SubmissionActions(
            viewState = SubmissionViewState.ImageTakenLocally(
                imageUri =  UriUtils.parse("content://example.jpg"),
                uploadStatus = SubmissionUploadState.Error(Exception("Upload failed"))
            ),
            onSubmitWork = {},
            onRetry = {},
            onCancelUpload = {},
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        )
    }
}