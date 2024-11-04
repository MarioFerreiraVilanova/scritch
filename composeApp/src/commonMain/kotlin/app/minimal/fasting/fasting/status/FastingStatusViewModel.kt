package app.minimal.fasting.fasting.status

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FastingStatusViewModel: ViewModel() {

    private val _viewState = MutableStateFlow<FastingStatusViewState>(
        value = FastingStatusViewState.Loading,
    )
    val viewState = _viewState.asStateFlow()
}