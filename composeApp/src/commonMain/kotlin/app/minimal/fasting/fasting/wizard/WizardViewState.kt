package app.minimal.fasting.fasting.wizard

import app.minimal.fasting.fasting.FastingPrefs

enum class WizardPage {
    Welcome,
    TimeToStart,
    FastingGoal,
    FastingDays,
}

data class WizardViewState(
    val fastingPrefs: FastingPrefs,
    val currentPage: WizardPage,
)
