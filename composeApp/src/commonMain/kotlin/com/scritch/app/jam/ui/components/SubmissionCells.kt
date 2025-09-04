package com.scritch.app.jam.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.scritch.app.jam.JamSubmission
import com.scritch.app.jam.ModerationStatus
import com.scritch.app.jam.SubmissionUploadState
import com.scritch.app.jam.SubmissionViewState
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import scritch.composeapp.generated.resources.Res
import scritch.composeapp.generated.resources.account_circle
import scritch.composeapp.generated.resources.entry_rejected
import scritch.composeapp.generated.resources.review_pending
import scritch.composeapp.generated.resources.share_your_work
import scritch.composeapp.generated.resources.uploading_your_image
import scritch.composeapp.generated.resources.you

@Composable
fun UserSubmissionCell(
    submissionState: SubmissionViewState,
    onSubmitWork: () -> Unit,
    onModerationStatusClick: () -> Unit,
    onShowPreview: () -> Unit,
    isJamExpired: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(MaterialTheme.shapes.small)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.small
            )
            .clickable { 
                when (submissionState) {
                    is SubmissionViewState.Submitted -> onShowPreview()
                    SubmissionViewState.NotSubmitted -> if (!isJamExpired) onSubmitWork()
                    else -> {} // Do nothing for other states like uploading
                }
            },
        contentAlignment = Alignment.Center
    ) {
        when (submissionState) {
            is SubmissionViewState.Submitted -> {
                Box {
                    KamelImage(
                        resource = { asyncPainterResource(submissionState.imageUrl) },
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(MaterialTheme.shapes.small)
                    )
                    
                    // Background overlay for contrast when showing moderation status
                    if (submissionState.moderationStatus != ModerationStatus.Approved) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    color = MaterialTheme.colorScheme.background.copy(alpha = 0.4f),
                                    shape = MaterialTheme.shapes.small
                                )
                        )
                    }
                    
                    // Moderation status text overlay (hide when approved)
                    if (submissionState.moderationStatus != ModerationStatus.Approved) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .background(
                                    color = when (submissionState.moderationStatus) {
                                        ModerationStatus.Pending -> MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                                        ModerationStatus.Rejected -> MaterialTheme.colorScheme.error
                                        else -> MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                                    },
                                    shape = CircleShape,
                                )
                                .clickable { onModerationStatusClick() }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = when (submissionState.moderationStatus) {
                                    ModerationStatus.Pending -> stringResource(Res.string.review_pending)
                                    ModerationStatus.Rejected -> stringResource(Res.string.entry_rejected)
                                    else -> ""
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = when (submissionState.moderationStatus) {
                                    ModerationStatus.Pending -> MaterialTheme.colorScheme.onSurface
                                    ModerationStatus.Rejected -> MaterialTheme.colorScheme.onError
                                    else -> MaterialTheme.colorScheme.primary
                                }
                            )
                        }
                    }
                    
                    // User overlay - reusable component
                    SubmissionOverlay(
                        displayName = "", // Will show "You" due to isCurrentUser = true
                        isCurrentUser = true,
                        modifier = Modifier.align(Alignment.BottomStart)
                    )
                }
            }
            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color = if (!isJamExpired) {
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            },
                            shape = MaterialTheme.shapes.small
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        when (submissionState) {
                            is SubmissionViewState.ImageTakenLocally -> {
                                CircularProgressIndicator(
                                    progress = { 
                                        when (val uploadStatus = submissionState.uploadStatus) {
                                            is SubmissionUploadState.Uploading -> uploadStatus.progress ?: 0f
                                            SubmissionUploadState.Success -> 1f
                                            else -> 0f
                                        }
                                    },
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(48.dp),
                                    strokeWidth = 2.dp
                                )
                                Text(
                                    text = stringResource(Res.string.uploading_your_image),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    textAlign = TextAlign.Center
                                )
                            }
                            else -> {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(48.dp)
                                )
                                Text(
                                    text = stringResource(Res.string.share_your_work),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SubmissionCell(
    submission: JamSubmission,
    showImage: Boolean,
    onShowPreview: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(MaterialTheme.shapes.small)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = MaterialTheme.shapes.small
            )
            .clickable(enabled = showImage) { onShowPreview(submission.userId) }
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        if (showImage) {
            Box {
                KamelImage(
                    resource = { asyncPainterResource(submission.imageUrl) },
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(MaterialTheme.shapes.small)
                )
                
                // Nickname overlay for other users
                SubmissionOverlay(
                    displayName = submission.nickname,
                    isCurrentUser = false,
                    modifier = Modifier.align(Alignment.BottomStart)
                )
            }
        } else {
            Icon(
                imageVector = Icons.Default.VisibilityOff,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

@Composable
private fun SubmissionOverlay(
    displayName: String,
    isCurrentUser: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        // Gradient background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
        )
        
        // Icon and text row
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isCurrentUser) {
                Icon(
                    painter = painterResource(Res.drawable.account_circle),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = if (isCurrentUser) stringResource(Res.string.you) else displayName,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}