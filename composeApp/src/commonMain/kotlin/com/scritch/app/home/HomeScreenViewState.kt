package com.scritch.app.home

import com.scritch.app.categories.OptionState

data class HomeScreenViewState(
    val topic: OptionState?,
    val medium: OptionState?,
    val support: OptionState?,
    val selectedOption: OptionState?,
)
