package com.scritch.app.wizard

import com.scritch.app.categories.Category
import kotlinx.serialization.Serializable

@Serializable
data class WizardScreenViewState(
    val category: Category,
    val step: Int,
)
