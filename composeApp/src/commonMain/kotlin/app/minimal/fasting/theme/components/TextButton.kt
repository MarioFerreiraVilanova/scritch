package app.minimal.fasting.theme.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.minimal.fasting.theme.MinimalTheme

@Composable
fun TextButton(
    modifier: Modifier = Modifier,
    label: String,
    onClick: () -> Unit,
) {
    MaterialTheme.typography.h5
    BasicText(
        text = label,
        style = MinimalTheme.typography.h5,
        modifier = modifier.clickable (
            onClick = onClick,
        )
    )
}