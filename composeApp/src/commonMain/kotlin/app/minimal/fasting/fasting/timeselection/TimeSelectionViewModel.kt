package app.minimal.fasting.fasting.timeselection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.minimal.fasting.auth.AuthenticationRepository
import app.minimal.fasting.common.now
import app.minimal.fasting.fasting.repository.FastingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TimeSelectionViewModel(
    private val fastingRepository: FastingRepository,
    private val authenticationRepository: AuthenticationRepository,
) : ViewModel() {
    private val _viewState = MutableStateFlow(
        TimeSelectionViewState.fromNow()
    )

    val viewState = _viewState.asStateFlow()

    fun onStartFastingClick() {
        /*viewModelScope.launch {
            _viewState.update {
                it.copy(saving = true)
            }
            fastingRepository.startFast(
                startTime = now(),
                userId = authenticationRepository.user()?.id
                    ?: throw RuntimeException("You need to be logged in order to save a fast entry")
            )
            _viewState.update {
                it.copy(
                    saving = false,
                    done = true,
                )
            }
        }*/
    }

    fun onHourSelect(
        selectedHour: Int,
    ){
        _viewState.update {
            it.copy(
                selectedHour = selectedHour,
                step = TimeSelectionStep.Minutes,
            )
        }
    }

    fun onMinuteSelect(
        selectedMinute: Int,
    ){
        _viewState.update {
            it.copy(
                selectedMinute = selectedMinute,
            )
        }
    }
}