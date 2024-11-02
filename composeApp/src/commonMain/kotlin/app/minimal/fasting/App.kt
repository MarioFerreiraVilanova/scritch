package app.minimal.fasting

import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App(
    viewModel: AppViewModel = koinViewModel<AppViewModel>()
) {
    MaterialTheme {
        Button(
            onClick = { viewModel.onLogIn() }
        ){
            Text("Test")
        }
    }
}