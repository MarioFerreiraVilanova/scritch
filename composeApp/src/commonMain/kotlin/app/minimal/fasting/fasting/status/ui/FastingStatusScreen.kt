package app.minimal.fasting.fasting.status.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.minimal.fasting.fasting.status.FastingStatusViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FastingStatusScreen(
    modifier: Modifier = Modifier,
    viewModel: FastingStatusViewModel = koinViewModel<FastingStatusViewModel>()
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = "Fasting status screen",
        )
    }
}