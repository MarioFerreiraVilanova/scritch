package com.scritch.app.jam.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.scritch.app.jam.ModerationStatus
import com.scritch.app.jam.SubmissionViewState
import com.scritch.app.theme.scritchColorScheme
import com.scritch.app.theme.scritchShapes
import com.scritch.app.theme.scritchTypography
import androidx.compose.material3.MaterialTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun UserSection(
    submissionState: SubmissionViewState,
    onSubmitWork: () -> Unit,
    onRemoveSubmission: () -> Unit,
    onRetryUpload: () -> Unit,
    onCancelUpload: () -> Unit,
    onModerationStatusClick: () -> Unit,
) {
    Column {
        val submitted = submissionState as? SubmissionViewState.Submitted
        AnimatedVisibility(
            visible = submitted != null,
        ) {
            EntryPreview(
                imageUrl = submitted?.imageUrl ?: return@AnimatedVisibility,
                onRetakeImage = onSubmitWork,
                onDeleteImage = onRemoveSubmission,
                moderationStatus = submitted.moderationStatus,
                onModerationStatusClick = onModerationStatusClick,
            )
        }
        SubmissionActions(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            viewState = submissionState,
            onSubmitWork = onSubmitWork,
            onRetry = onRetryUpload,
            onCancelUpload = onCancelUpload,
        )
    }
}

@Preview
@Composable
private fun UserSectionNotSubmittedPreview() {
    MaterialTheme(
        colorScheme = scritchColorScheme,
        typography = scritchTypography(),
        shapes = scritchShapes,
    ) {
        UserSection(
            submissionState = SubmissionViewState.NotSubmitted,
            onSubmitWork = {},
            onRemoveSubmission = {},
            onRetryUpload = {},
            onCancelUpload = {},
            onModerationStatusClick = {},
        )
    }
}

@Preview
@Composable
private fun UserSectionSubmittedPreview() {
    MaterialTheme(
        colorScheme = scritchColorScheme,
        typography = scritchTypography(),
        shapes = scritchShapes,
    ) {
        UserSection(
            submissionState = SubmissionViewState.Submitted(
                imageUrl = "https://example.com/image.jpg",
                moderationStatus = ModerationStatus.Pending
            ),
            onSubmitWork = {},
            onRemoveSubmission = {},
            onRetryUpload = {},
            onCancelUpload = {},
            onModerationStatusClick = {},
        )
    }
}