package com.scritch.app.prompt

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.scritch.app.solomode.Tips

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TipsSheet(
    viewState: PromptViewState,
    onTipDisplayed: () -> Unit,
){
    val sheetState = rememberModalBottomSheetState()

    LaunchedEffect(viewState.selectedOption) {
        if (viewState.selectedOption != null) {
            sheetState.show()
        }
    }
    LaunchedEffect(sheetState.isVisible) {
        if (!sheetState.isVisible) {
            onTipDisplayed()
        }
    }
    if (viewState.selectedOption != null) {
        ModalBottomSheet(
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            onDismissRequest = {
                onTipDisplayed()
            },
        ) {
            viewState.selectedOption.tips?.let { tips ->
                Tips(
                    tips = tips
                )
            }
        }
    }
}