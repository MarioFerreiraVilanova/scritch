package com.scritch.app.jam.ui.components.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.scritch.app.reporting.ReportReason
import org.jetbrains.compose.resources.stringResource
import scritch.composeapp.generated.resources.Res
import scritch.composeapp.generated.resources.report_submission_title
import scritch.composeapp.generated.resources.report_submission_question
import scritch.composeapp.generated.resources.report_reason_inappropriate_content
import scritch.composeapp.generated.resources.report_reason_spam
import scritch.composeapp.generated.resources.report_reason_harassment
import scritch.composeapp.generated.resources.report_reason_copyright
import scritch.composeapp.generated.resources.report_reason_other
import scritch.composeapp.generated.resources.report_button
import scritch.composeapp.generated.resources.cancel

@Composable
fun ReportSubmissionDialog(
    nickname: String,
    onDismiss: () -> Unit,
    onConfirm: (ReportReason) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedReason by remember { mutableStateOf<ReportReason?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(stringResource(Res.string.report_submission_title, nickname))
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(Res.string.report_submission_question),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                
                ReportReason.entries.forEach { reason ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedReason = reason }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        RadioButton(
                            selected = selectedReason == reason,
                            onClick = { selectedReason = reason }
                        )
                        Text(
                            text = when (reason) {
                                ReportReason.INAPPROPRIATE_CONTENT -> stringResource(Res.string.report_reason_inappropriate_content)
                                ReportReason.SPAM -> stringResource(Res.string.report_reason_spam)
                                ReportReason.HARASSMENT -> stringResource(Res.string.report_reason_harassment)
                                ReportReason.COPYRIGHT -> stringResource(Res.string.report_reason_copyright)
                                ReportReason.OTHER -> stringResource(Res.string.report_reason_other)
                            },
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    selectedReason?.let { onConfirm(it) }
                },
                enabled = selectedReason != null
            ) {
                Text(stringResource(Res.string.report_button))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.cancel))
            }
        },
        modifier = modifier
    )
}