package com.scritch.app.jam.ui.components.dialogs

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.scritch.app.jam.ModerationStatus
import com.scritch.app.jam.ui.components.ModerationStatusChip
import com.scritch.app.theme.scritchColorScheme
import com.scritch.app.theme.scritchShapes
import com.scritch.app.theme.scritchTypography
import com.scritch.app.uicomponents.Button
import com.scritch.app.uicomponents.ButtonStyle
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.scritch.app.jam.ui.components.dialogs.ReportSubmissionDialog
import com.scritch.app.reporting.ReportReason
import scritch.composeapp.generated.resources.Res
import scritch.composeapp.generated.resources.delete
import scritch.composeapp.generated.resources.redo
import scritch.composeapp.generated.resources.report_button
import scritch.composeapp.generated.resources.scritch_jam_by
import scritch.composeapp.generated.resources.submission_preview
import scritch.composeapp.generated.resources.your_contribution

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubmissionPreviewDialog(
    imageUrl: String,
    isUserSubmission: Boolean,
    moderationStatus: ModerationStatus,
    nickname: String,
    isJamExpired: Boolean,
    onDismissRequest: () -> Unit,
    onRetrySubmission: (() -> Unit)?,
    onDeleteSubmission: (() -> Unit)?,
    onModerationStatusClick: (() -> Unit)?,
    onReportSubmission: ((ReportReason) -> Unit)?,
) {
    var showReportDialog by remember { mutableStateOf(false) }
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Nickname header text
            Text(
                text = createStyledSubmissionText(
                    isUserSubmission = isUserSubmission,
                    nickname = nickname
                ),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            )
            
            Box {
                KamelImage(
                    resource = { asyncPainterResource(imageUrl) },
                    contentDescription = stringResource(Res.string.submission_preview),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.small)
                        .border(
                            width = 1.dp,
                            color = if (isUserSubmission) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.outline
                            },
                            shape = MaterialTheme.shapes.small
                        )
                        .zoomable(rememberZoomState()),
                    onLoading = { progress ->
                        // progress in [0f..1f] or null
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .heightIn(min = 160.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(
                                8.dp,
                                Alignment.CenterVertically
                            )
                        ) {
                            val animatedProgress by animateFloatAsState(progress)
                            CircularProgressIndicator(
                                progress = { animatedProgress },
                                modifier = Modifier.size(32.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Loading...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                        }
                    }
                )
                
                // Moderation status chip for user submissions (overlaid on top start corner)
                if (isUserSubmission) {
                    ModerationStatusChip(
                        moderationStatus = moderationStatus,
                        showForApproved = true,
                        onClick = onModerationStatusClick,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                    )
                }
            }
            
            // Action buttons for user submissions
            if (isUserSubmission && ((!isJamExpired && onRetrySubmission != null) || onDeleteSubmission != null)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    // Replace button - only show if jam is not expired
                    if (!isJamExpired) {
                        onRetrySubmission?.let { onRetry ->
                            Button(
                                onClick = onRetry,
                                label = "Replace",
                                icon = painterResource(Res.drawable.redo),
                                style = ButtonStyle.Secondary,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                    
                    // Delete button - always show for user submissions
                    onDeleteSubmission?.let { onDelete ->
                        Button(
                            onClick = onDelete,
                            label = "Delete",
                            icon = painterResource(Res.drawable.delete),
                            style = ButtonStyle.Negative,
                            modifier = if (!isJamExpired && onRetrySubmission != null) {
                                Modifier.weight(1f)
                            } else {
                                Modifier.fillMaxWidth()
                            },
                        )
                    }
                }
            }
            
            // Report button for other users' submissions
            if (!isUserSubmission && onReportSubmission != null) {
                TextButton(
                    onClick = { showReportDialog = true },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.report_button),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
    
    // Report dialog
    if (showReportDialog && onReportSubmission != null) {
        ReportSubmissionDialog(
            nickname = nickname,
            onDismiss = { showReportDialog = false },
            onConfirm = { reason ->
                onReportSubmission(reason)
                showReportDialog = false
            }
        )
    }
}

@Preview
@Composable
private fun SubmissionPreviewDialogUserApprovedPreview() {
    MaterialTheme(
        colorScheme = scritchColorScheme,
        typography = scritchTypography(),
        shapes = scritchShapes,
    ) {
        SubmissionPreviewDialog(
            imageUrl = "https://example.com/image.jpg",
            isUserSubmission = true,
            moderationStatus = ModerationStatus.Approved,
            nickname = "Picasso47",
            isJamExpired = false,
            onDismissRequest = {},
            onRetrySubmission = {},
            onDeleteSubmission = {},
            onModerationStatusClick = {},
            onReportSubmission = null,
        )
    }
}

@Preview
@Composable
private fun SubmissionPreviewDialogUserPendingPreview() {
    MaterialTheme(
        colorScheme = scritchColorScheme,
        typography = scritchTypography(),
        shapes = scritchShapes,
    ) {
        SubmissionPreviewDialog(
            imageUrl = "https://example.com/image.jpg",
            isUserSubmission = true,
            moderationStatus = ModerationStatus.Pending,
            nickname = "Picasso47",
            isJamExpired = false,
            onDismissRequest = {},
            onRetrySubmission = {},
            onDeleteSubmission = {},
            onModerationStatusClick = {},
            onReportSubmission = null,
        )
    }
}

@Preview
@Composable
private fun SubmissionPreviewDialogUserRejectedPreview() {
    MaterialTheme(
        colorScheme = scritchColorScheme,
        typography = scritchTypography(),
        shapes = scritchShapes,
    ) {
        SubmissionPreviewDialog(
            imageUrl = "https://example.com/image.jpg",
            isUserSubmission = true,
            moderationStatus = ModerationStatus.Rejected,
            nickname = "Picasso47",
            isJamExpired = false,
            onDismissRequest = {},
            onRetrySubmission = {},
            onDeleteSubmission = {},
            onModerationStatusClick = {},
            onReportSubmission = null,
        )
    }
}

@Preview
@Composable
private fun SubmissionPreviewDialogOtherUserPreview() {
    MaterialTheme(
        colorScheme = scritchColorScheme,
        typography = scritchTypography(),
        shapes = scritchShapes,
    ) {
        SubmissionPreviewDialog(
            imageUrl = "https://example.com/image.jpg",
            isUserSubmission = false,
            moderationStatus = ModerationStatus.Approved,
            nickname = "VanGogh203",
            isJamExpired = false,
            onDismissRequest = {},
            onRetrySubmission = null,
            onDeleteSubmission = null,
            onModerationStatusClick = null,
            onReportSubmission = { },
        )
    }
}

@Composable
private fun createStyledSubmissionText(
    isUserSubmission: Boolean,
    nickname: String
): AnnotatedString {
    val baseText = if (isUserSubmission) {
        stringResource(Res.string.your_contribution, nickname)
    } else {
        stringResource(Res.string.scritch_jam_by, nickname)
    }
    
    return buildAnnotatedString {
        val nicknameStart = baseText.indexOf(nickname)
        if (nicknameStart >= 0) {
            // Add text before nickname with regular color
            append(baseText.substring(0, nicknameStart))
            // Add nickname with primary color
            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                append(nickname)
            }
            // Add text after nickname with regular color
            append(baseText.substring(nicknameStart + nickname.length))
        } else {
            // Fallback: just append the whole text with regular color
            append(baseText)
        }
    }
}