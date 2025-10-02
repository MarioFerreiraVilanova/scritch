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
import androidx.core.uri.UriUtils
import com.scritch.app.theme.scritchColorScheme
import com.scritch.app.theme.scritchShapes
import com.scritch.app.theme.scritchTypography
import dev.gitlive.firebase.firestore.Timestamp
import org.jetbrains.compose.ui.tooling.preview.Preview
import scritch.composeapp.generated.resources.Res
import scritch.composeapp.generated.resources.account_circle
import scritch.composeapp.generated.resources.entry_rejected
import scritch.composeapp.generated.resources.no_one_contributed_yet
import scritch.composeapp.generated.resources.plus
import scritch.composeapp.generated.resources.review_pending
import scritch.composeapp.generated.resources.share_your_work
import scritch.composeapp.generated.resources.status_approved
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
                        onLoading = { progress ->
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                            ) {
                                CircularProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier.size(32.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        onFailure = { exception ->
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.errorContainer)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        },
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
                    
                    // Moderation status chip overlay (hide when approved)
                    ModerationStatusChip(
                        moderationStatus = submissionState.moderationStatus,
                        showForApproved = false,
                        onClick = onModerationStatusClick,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    
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
                                    painter = painterResource(Res.drawable.plus),
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
                    onLoading = { progress ->
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            CircularProgressIndicator(
                                progress = { progress },
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    },
                    onFailure = { exception ->
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.errorContainer)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VisibilityOff,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    },
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
fun EmptyFeedCell(
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
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(Res.string.no_one_contributed_yet),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(12.dp)
        )
    }
}

@Composable
fun ModerationStatusChip(
    moderationStatus: ModerationStatus,
    showForApproved: Boolean = false,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    if (moderationStatus != ModerationStatus.Approved || showForApproved) {
        Box(
            modifier = modifier
                .background(
                    color = when (moderationStatus) {
                        ModerationStatus.Pending -> MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                        ModerationStatus.Rejected -> MaterialTheme.colorScheme.error
                        ModerationStatus.Approved -> MaterialTheme.colorScheme.primary
                    },
                    shape = CircleShape,
                )
                .then(
                    if (onClick != null) Modifier.clickable { onClick() } else Modifier
                )
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = when (moderationStatus) {
                    ModerationStatus.Pending -> stringResource(Res.string.review_pending)
                    ModerationStatus.Rejected -> stringResource(Res.string.entry_rejected)
                    ModerationStatus.Approved -> stringResource(Res.string.status_approved)
                },
                style = MaterialTheme.typography.labelSmall,
                color = when (moderationStatus) {
                    ModerationStatus.Pending -> MaterialTheme.colorScheme.onSurface
                    ModerationStatus.Rejected -> MaterialTheme.colorScheme.onError
                    ModerationStatus.Approved -> MaterialTheme.colorScheme.onPrimary
                }
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

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview
@Composable
private fun UserSubmissionCellNotSubmittedPreview() {
    MaterialTheme(
        colorScheme = scritchColorScheme,
        typography = scritchTypography(),
        shapes = scritchShapes,
    ) {
        UserSubmissionCell(
            submissionState = SubmissionViewState.NotSubmitted,
            onSubmitWork = {},
            onModerationStatusClick = {},
            onShowPreview = {},
            isJamExpired = false,
            modifier = Modifier.size(120.dp)
        )
    }
}

@Preview
@Composable
private fun UserSubmissionCellUploadingPreview() {
    MaterialTheme(
        colorScheme = scritchColorScheme,
        typography = scritchTypography(),
        shapes = scritchShapes,
    ) {
        UserSubmissionCell(
            submissionState = SubmissionViewState.ImageTakenLocally(
                imageUri = UriUtils.parse("content://example.jpg"),
                uploadStatus = SubmissionUploadState.Uploading(0.6f)
            ),
            onSubmitWork = {},
            onModerationStatusClick = {},
            onShowPreview = {},
            isJamExpired = false,
            modifier = Modifier.size(120.dp)
        )
    }
}

@Preview
@Composable
private fun UserSubmissionCellSubmittedApprovedPreview() {
    MaterialTheme(
        colorScheme = scritchColorScheme,
        typography = scritchTypography(),
        shapes = scritchShapes,
    ) {
        UserSubmissionCell(
            submissionState = SubmissionViewState.Submitted(
                imageUrl = "https://example.com/image.jpg",
                moderationStatus = ModerationStatus.Approved
            ),
            onSubmitWork = {},
            onModerationStatusClick = {},
            onShowPreview = {},
            isJamExpired = false,
            modifier = Modifier.size(120.dp)
        )
    }
}

@Preview
@Composable
private fun UserSubmissionCellSubmittedPendingPreview() {
    MaterialTheme(
        colorScheme = scritchColorScheme,
        typography = scritchTypography(),
        shapes = scritchShapes,
    ) {
        UserSubmissionCell(
            submissionState = SubmissionViewState.Submitted(
                imageUrl = "https://example.com/image.jpg",
                moderationStatus = ModerationStatus.Pending
            ),
            onSubmitWork = {},
            onModerationStatusClick = {},
            onShowPreview = {},
            isJamExpired = false,
            modifier = Modifier.size(120.dp)
        )
    }
}

@Preview
@Composable
private fun UserSubmissionCellSubmittedRejectedPreview() {
    MaterialTheme(
        colorScheme = scritchColorScheme,
        typography = scritchTypography(),
        shapes = scritchShapes,
    ) {
        UserSubmissionCell(
            submissionState = SubmissionViewState.Submitted(
                imageUrl = "https://example.com/image.jpg",
                moderationStatus = ModerationStatus.Rejected
            ),
            onSubmitWork = {},
            onModerationStatusClick = {},
            onShowPreview = {},
            isJamExpired = false,
            modifier = Modifier.size(120.dp)
        )
    }
}

@Preview
@Composable
private fun UserSubmissionCellExpiredPreview() {
    MaterialTheme(
        colorScheme = scritchColorScheme,
        typography = scritchTypography(),
        shapes = scritchShapes,
    ) {
        UserSubmissionCell(
            submissionState = SubmissionViewState.NotSubmitted,
            onSubmitWork = {},
            onModerationStatusClick = {},
            onShowPreview = {},
            isJamExpired = true,
            modifier = Modifier.size(120.dp)
        )
    }
}

@Preview
@Composable
private fun SubmissionCellVisiblePreview() {
    MaterialTheme(
        colorScheme = scritchColorScheme,
        typography = scritchTypography(),
        shapes = scritchShapes,
    ) {
        SubmissionCell(
            submission = JamSubmission(
                userId = "user123",
                imageUrl = "https://example.com/image.jpg",
                createdAt = Timestamp.now(),
                status = ModerationStatus.Approved,
                nickname = "Picasso47"
            ),
            showImage = true,
            onShowPreview = {},
            modifier = Modifier.size(120.dp)
        )
    }
}

@Preview
@Composable
private fun SubmissionCellHiddenPreview() {
    MaterialTheme(
        colorScheme = scritchColorScheme,
        typography = scritchTypography(),
        shapes = scritchShapes,
    ) {
        SubmissionCell(
            submission = JamSubmission(
                userId = "user123",
                imageUrl = "https://example.com/image.jpg",
                createdAt = Timestamp.now(),
                status = ModerationStatus.Approved,
                nickname = "VanGogh203"
            ),
            showImage = false,
            onShowPreview = {},
            modifier = Modifier.size(120.dp)
        )
    }
}

@Preview
@Composable
private fun EmptyFeedCellPreview() {
    MaterialTheme(
        colorScheme = scritchColorScheme,
        typography = scritchTypography(),
        shapes = scritchShapes,
    ) {
        EmptyFeedCell(
            modifier = Modifier.size(120.dp)
        )
    }
}

@Preview
@Composable
private fun SubmissionOverlayCurrentUserPreview() {
    MaterialTheme(
        colorScheme = scritchColorScheme,
        typography = scritchTypography(),
        shapes = scritchShapes,
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            SubmissionOverlay(
                displayName = "Picasso47",
                isCurrentUser = true,
                modifier = Modifier.align(Alignment.BottomStart)
            )
        }
    }
}

@Preview
@Composable
private fun SubmissionOverlayOtherUserPreview() {
    MaterialTheme(
        colorScheme = scritchColorScheme,
        typography = scritchTypography(),
        shapes = scritchShapes,
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            SubmissionOverlay(
                displayName = "VanGogh203",
                isCurrentUser = false,
                modifier = Modifier.align(Alignment.BottomStart)
            )
        }
    }
}