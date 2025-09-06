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
import scritch.composeapp.generated.resources.file_size_exceeded_title
import scritch.composeapp.generated.resources.file_size_exceeded_message
import scritch.composeapp.generated.resources.ok

@Composable
fun FileSizeExceededDialog(
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = stringResource(Res.string.file_size_exceeded_title))
        },
        text = {
            Text(text = stringResource(Res.string.file_size_exceeded_message))
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(Res.string.ok))
            }
        }
    )
}

@Preview
@Composable
private fun FileSizeExceededDialogPreview() {
    MaterialTheme(
        colorScheme = scritchColorScheme,
        typography = scritchTypography(),
        shapes = scritchShapes,
    ) {
        FileSizeExceededDialog(
            onDismissRequest = {}
        )
    }
}