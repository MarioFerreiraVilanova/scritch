package app.minimal.fasting.fasting.wizard

import app.minimal.fasting.fasting.FastingPrefsDto

enum class WizardPage {
    Welcome,
    TimeToStart,
    FastingGoal,
    FastingDays,
}

data class WizardViewState(
    val fastingPrefs: FastingPrefsDto,
    val currentPage: WizardPage,
)
