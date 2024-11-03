package app.minimal.fasting.fasting.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FastingStatusScreen (
    viewModel: FastingStatusViewModel = koinViewModel<FastingStatusViewModel>()
){

}