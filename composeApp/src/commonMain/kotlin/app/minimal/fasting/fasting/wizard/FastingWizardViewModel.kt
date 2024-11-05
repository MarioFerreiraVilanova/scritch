package app.minimal.fasting.fasting.wizard

import androidx.lifecycle.ViewModel
import app.minimal.fasting.common.next
import app.minimal.fasting.fasting.FastingPrefs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FastingWizardViewModel : ViewModel() {
    private val _viewState = MutableStateFlow(
        WizardViewState(
            fastingPrefs = FastingPrefs(),
            currentPage = WizardPage.entries.first(),
        )
    )
    val viewState = _viewState.asStateFlow()

    fun onNext() {
        _viewState.update {
            it.copy(
                currentPage = it.currentPage.next()
            )
        }
    }
}