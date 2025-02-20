package com.scritch.app.wizard

import com.scritch.app.categories.Category
import com.scritch.app.categories.OptionState
import kotlinx.serialization.Serializable

@Serializable
data class WizardScreenViewState(
    val category: Category,
    val step: Int?,
    val optionStates: List<OptionState>?,
    val allDisabled: Boolean,
) {
    val isWizard = step != null
}
