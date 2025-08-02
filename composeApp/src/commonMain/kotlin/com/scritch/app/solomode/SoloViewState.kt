package com.scritch.app.solomode

import com.scritch.app.categories.OptionState

data class SoloViewState(
    val topic: OptionState?,
    val medium: OptionState?,
    val support: OptionState?,
    val constraint: OptionState?,
    val selectedOption: OptionState?,
)
