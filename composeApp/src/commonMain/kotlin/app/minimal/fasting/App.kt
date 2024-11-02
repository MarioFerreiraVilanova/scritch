package app.minimal.fasting

import androidx.compose.foundation.layout.Column
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
    val viewState by viewModel.appViewState.collectAsState()

    MaterialTheme {
        Column {
            Text (if (viewState.user == null) "Signed out" else "Signed in")

            if (viewState.user == null){
                Button(
                    onClick = { viewModel.onLogIn() }
                ){
                    Text("Log in")
                }
            } else {
                Button(
                    onClick = { viewModel.onLogOut() }
                ){
                    Text("Log out")
                }
            }
        }
    }
}