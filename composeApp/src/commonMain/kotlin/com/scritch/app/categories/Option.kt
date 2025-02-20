package com.scritch.app.categories

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.scritch.app.uicomponents.ToggleListRow

@Composable
fun Option (
    state: OptionState,
    enabled: Boolean,
    onCheckChangeRequest: () -> Unit,
    modifier: Modifier = Modifier,
){
    ToggleListRow(
        modifier = modifier,
        title = state.name,
        subtitle = state.description,
        checked = state.selected,
        enabled = enabled,
        onCheckChangeRequest = onCheckChangeRequest,
    )
}