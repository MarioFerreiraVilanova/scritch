package com.scritch.app.prompt

import com.scritch.app.categories.OptionState

data class PromptViewState(
    val topic: OptionState?,
    val medium: OptionState?,
    val support: OptionState?,
    val constraint: OptionState?,
    val selectedOption: OptionState?,
){
    val valid = medium != null || support != null

    companion object {
        val EMPTY = PromptViewState(
            topic = null,
            medium = null,
            support = null,
            constraint = null,
            selectedOption = null,
        )
    }
}