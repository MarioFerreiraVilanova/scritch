package app.minimal.fasting.fasting.status

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.minimal.fasting.auth.AuthenticationRepository
import app.minimal.fasting.fasting.repository.FastingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FastingStatusViewModel(
    fastingRepository: FastingRepository,
    authenticationRepository: AuthenticationRepository,
) : ViewModel() {

    private val _viewState = MutableStateFlow<FastingStatusViewState>(
        value = FastingStatusViewState.Loading,
    )
    val viewState = _viewState.asStateFlow()

    private val fastingPrefs = fastingRepository.fastingPrefs(
        userId = authenticationRepository.user()?.id
            ?: throw (RuntimeException("We shouldn't be here if the user is not authenticated"))
    )

    init {
        viewModelScope.launch {
            fastingPrefs.collectLatest { fastingPrefs ->
                if (fastingPrefs == null) {
                    _viewState.update {
                        FastingStatusViewState.NeedsSetup
                    }
                } else {
                    _viewState.update {
                        FastingStatusViewState.Loaded(
                            fastingPrefs = fastingPrefs
                        )
                    }
                }
            }
        }
    }
}