package com.scritch.app.jam.ui.components.dialogs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.scritch.app.jam.ModerationStatus
import com.scritch.app.theme.scritchColorScheme
import com.scritch.app.theme.scritchShapes
import com.scritch.app.theme.scritchTypography
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import scritch.composeapp.generated.resources.Res
import scritch.composeapp.generated.resources.entry_approved
import scritch.composeapp.generated.resources.entry_approved_message
import scritch.composeapp.generated.resources.entry_rejected
import scritch.composeapp.generated.resources.entry_rejected_message
import scritch.composeapp.generated.resources.ok
import scritch.composeapp.generated.resources.review_pending
import scritch.composeapp.generated.resources.review_pending_message

@Composable
fun ModerationStatusDialog(
    moderationStatus: ModerationStatus,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = when (moderationStatus) {
                    ModerationStatus.Pending -> stringResource(Res.string.review_pending)
                    ModerationStatus.Approved -> stringResource(Res.string.entry_approved)
                    ModerationStatus.Rejected -> stringResource(Res.string.entry_rejected)
                },
                textAlign = androidx.compose.ui.text.style.TextAlign.Start,
                color = MaterialTheme.colorScheme.onSurface,
            )
        },
        text = {
            Text(
                text = when (moderationStatus) {
                    ModerationStatus.Pending -> stringResource(Res.string.review_pending_message)
                    ModerationStatus.Approved -> stringResource(Res.string.entry_approved_message)
                    ModerationStatus.Rejected -> stringResource(Res.string.entry_rejected_message)
                },
                color = MaterialTheme.colorScheme.onSurface,
            )
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(Res.string.ok))
            }
        },
    )
}

@Preview
@Composable
private fun ModerationStatusDialogPendingPreview() {
    MaterialTheme(
        colorScheme = scritchColorScheme,
        typography = scritchTypography(),
        shapes = scritchShapes,
    ) {
        ModerationStatusDialog(
            moderationStatus = ModerationStatus.Pending,
            onDismissRequest = {}
        )
    }
}

@Preview
@Composable
private fun ModerationStatusDialogApprovedPreview() {
    MaterialTheme(
        colorScheme = scritchColorScheme,
        typography = scritchTypography(),
        shapes = scritchShapes,
    ) {
        ModerationStatusDialog(
            moderationStatus = ModerationStatus.Approved,
            onDismissRequest = {}
        )
    }
}

@Preview
@Composable
private fun ModerationStatusDialogRejectedPreview() {
    MaterialTheme(
        colorScheme = scritchColorScheme,
        typography = scritchTypography(),
        shapes = scritchShapes,
    ) {
        ModerationStatusDialog(
            moderationStatus = ModerationStatus.Rejected,
            onDismissRequest = {}
        )
    }
}