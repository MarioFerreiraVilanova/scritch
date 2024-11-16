package app.minimal.fasting.fasting.timeselection

import app.minimal.fasting.common.now

data class TimeSelectionViewState(
    val hintHour: Int? = null,
    val hintMinute: Int? = null,
    val selectedHour: Int? = null,
    val selectedMinute: Int? = null,
    val step: TimeSelectionStep = TimeSelectionStep.Hours,
) {

    // Creates a view state instance with hints pointing at the current time
    companion object {
        fun fromNow(): TimeSelectionViewState {
            now().let {
                return TimeSelectionViewState(
                    hintHour = it.hour,
                    hintMinute = it.minute,
                )
            }
        }
    }
}

enum class TimeSelectionStep {
    Hours,
    Minutes,
}
