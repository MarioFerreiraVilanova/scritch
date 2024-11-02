package app.minimal.fasting.home

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen (
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel<HomeViewModel>(),
){
    Column (
        modifier = modifier
    ) {
        Text ("You are logged in!")

        Button(
            onClick = { viewModel.onLogOut() }
        ){
            Text("Log out")
        }
    }
}