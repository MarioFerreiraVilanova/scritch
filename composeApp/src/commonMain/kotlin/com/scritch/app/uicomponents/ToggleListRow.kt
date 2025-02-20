package com.scritch.app.uicomponents

import androidx.compose.foundation.clickable
import androidx.compose.material3.ListItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ToggleListRow(
    title: String,
    subtitle: String?,
    checked: Boolean,
    onCheckChangeRequest: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    ListItem(
        modifier = modifier.then(
            if (enabled) {
                Modifier.clickable { onCheckChangeRequest() }
            } else {
                Modifier
            }
        ),
        headlineContent = {
            Text(title)
        },
        supportingContent = {
            if (subtitle != null) {
                Text(
                    text = subtitle,
                )
            }
        },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = { _ -> onCheckChangeRequest() },
                enabled = enabled,
            )
        }
    )
}