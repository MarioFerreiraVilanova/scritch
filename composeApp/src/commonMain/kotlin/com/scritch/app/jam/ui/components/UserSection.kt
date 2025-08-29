package com.scritch.app.jam.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.scritch.app.jam.ModerationStatus
import com.scritch.app.jam.SubmissionViewState
import com.scritch.app.theme.scritchColorScheme
import com.scritch.app.theme.scritchShapes
import com.scritch.app.theme.scritchTypography
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import scritch.composeapp.generated.resources.Res
import scritch.composeapp.generated.resources.weekly_scritch_has_ended

@Composable
fun UserSection(
    submissionState: SubmissionViewState,
    onSubmitWork: () -> Unit,
    onRemoveSubmission: () -> Unit,
    onRetryUpload: () -> Unit,
    onCancelUpload: () -> Unit,
    onModerationStatusClick: () -> Unit,
    isJamExpired: Boolean,
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
                isJamExpired = isJamExpired,
            )
        }
        SubmissionActions(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            viewState = submissionState,
            onSubmitWork = onSubmitWork,
            onRetry = onRetryUpload,
            onCancelUpload = onCancelUpload,
            isJamExpired = isJamExpired,
        )
        
        // Show expired message at the bottom when jam has ended
        if (isJamExpired) {
            Text(
                text = stringResource(Res.string.weekly_scritch_has_ended),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
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
            isJamExpired = false,
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
            isJamExpired = false,
        )
    }
}