package app.minimal.fasting.fasting.wizard.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.minimal.fasting.fasting.wizard.FastingWizardViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FastingWizardScreen(
    modifier: Modifier = Modifier,
    viewModel: FastingWizardViewModel = koinViewModel<FastingWizardViewModel>()
){
    Box (
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = "Fasting Wizard"
        )
    }
}