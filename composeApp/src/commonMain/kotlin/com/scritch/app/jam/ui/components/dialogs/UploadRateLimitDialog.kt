package com.scritch.app.jam.ui.components.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.scritch.app.theme.scritchColorScheme
import com.scritch.app.theme.scritchShapes
import com.scritch.app.theme.scritchTypography
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import scritch.composeapp.generated.resources.Res
import scritch.composeapp.generated.resources.ok
import scritch.composeapp.generated.resources.upload_rate_limit_title
import scritch.composeapp.generated.resources.upload_rate_limit_cooldown_message
import scritch.composeapp.generated.resources.upload_rate_limit_window_message
import kotlin.time.Duration

@Composable
fun UploadRateLimitDialog(
    isRateLimited: Boolean,
    remainingTime: Duration,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = stringResource(Res.string.upload_rate_limit_title))
        },
        text = {
            val messageRes = if (isRateLimited) {
                Res.string.upload_rate_limit_window_message
            } else {
                Res.string.upload_rate_limit_cooldown_message
            }
            
            val timeString = formatDuration(remainingTime)
            Text(text = stringResource(messageRes, timeString))
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(Res.string.ok))
            }
        }
    )
}

@Composable
private fun formatDuration(duration: Duration): String {
    val totalSeconds = duration.inWholeSeconds
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    
    return when {
        minutes > 0 -> "${minutes}m ${seconds}s"
        else -> "${seconds}s"
    }
}

@Preview
@Composable
private fun UploadRateLimitDialogPreview() {
    MaterialTheme(
        colorScheme = scritchColorScheme,
        typography = scritchTypography(),
        shapes = scritchShapes,
    ) {
        UploadRateLimitDialog(
            isRateLimited = false,
            remainingTime = Duration.parse("45s"),
            onDismissRequest = {}
        )
    }
}