package app.minimal.fasting.landing

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LandingScreen (
    modifier: Modifier = Modifier,
    viewModel: LandingViewModel = koinViewModel<LandingViewModel>(),
){
    Column (
        modifier = modifier
    ) {
        Text ("Signed out")

        Button(
            onClick = { viewModel.onLogIn() }
        ){
            Text("Log in")
        }
    }
}