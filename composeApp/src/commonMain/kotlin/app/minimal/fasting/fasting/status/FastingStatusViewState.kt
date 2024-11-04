package app.minimal.fasting.fasting.status

import app.minimal.fasting.fasting.FastingPrefs

sealed class FastingStatusViewState {
    data object Loading: FastingStatusViewState()
    data class Loaded(
        val fastingPrefs: FastingPrefs,
    ): FastingStatusViewState()
}