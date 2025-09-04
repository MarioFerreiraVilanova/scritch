package com.scritch.app.jam.ui.components.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.scritch.app.theme.scritchColorScheme
import com.scritch.app.theme.scritchShapes
import com.scritch.app.theme.scritchTypography
import com.scritch.app.uicomponents.Button
import com.scritch.app.uicomponents.ButtonStyle
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import scritch.composeapp.generated.resources.Res
import scritch.composeapp.generated.resources.are_you_sure_delete_entry
import scritch.composeapp.generated.resources.cancel
import scritch.composeapp.generated.resources.delete_entry
import scritch.composeapp.generated.resources.yes_delete_it

@Composable
fun SubmissionDeleteDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = stringResource(Res.string.delete_entry))
        },
        text = {
            Text(text = stringResource(Res.string.are_you_sure_delete_entry))
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                style = ButtonStyle.Negative,
            ) {
                Text(text = stringResource(Res.string.yes_delete_it))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(Res.string.cancel))
            }
        }
    )
}

@Preview
@Composable
private fun SubmissionDeleteDialogPreview() {
    MaterialTheme(
        colorScheme = scritchColorScheme,
        typography = scritchTypography(),
        shapes = scritchShapes,
    ) {
        SubmissionDeleteDialog(
            onDismissRequest = {},
            onConfirm = {}
        )
    }
}