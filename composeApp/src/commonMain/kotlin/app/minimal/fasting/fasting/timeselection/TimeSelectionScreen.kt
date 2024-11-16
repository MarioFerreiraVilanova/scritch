package app.minimal.fasting.fasting.timeselection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TimeSelectionScreen(
    modifier: Modifier = Modifier,
    viewModel: TimeSelectionViewModel = koinViewModel(),
    onDone: () -> Unit,
) {
    val viewState by viewModel.viewState.collectAsState()
    if (viewState.done){
        onDone()
    }
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = "When do you start fasting?",
            style = MaterialTheme.typography.h2,
        )
        Text(
            text = "Placeholder, a time picker will be place here"
        )
        Button(
            onClick = viewModel::onStartFastingClick,
            enabled = !viewState.saving,
            content = {
                if (viewState.saving){
                    Text("Save (start fasting now)")
                } else {
                    Text("Save (start fasting now)")
                }
            }
        )
    }
}