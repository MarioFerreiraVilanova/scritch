package com.scritch.app.wizard

import com.scritch.app.categories.Category
import com.scritch.app.categories.Option
import kotlinx.serialization.Serializable

@Serializable
data class WizardScreenViewState(
    val category: Category,
    val step: Int,
    val options: List<Option>?,
)
