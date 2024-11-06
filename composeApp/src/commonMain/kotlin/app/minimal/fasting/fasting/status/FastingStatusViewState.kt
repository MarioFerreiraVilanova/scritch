package app.minimal.fasting.fasting.status

import app.minimal.fasting.fasting.FastingPrefsDto

sealed class FastingStatusViewState {
    data object Loading: FastingStatusViewState()
    data object NeedsSetup: FastingStatusViewState()
    data class Loaded(
        val fastingPrefs: FastingPrefsDto,
    ): FastingStatusViewState()
}