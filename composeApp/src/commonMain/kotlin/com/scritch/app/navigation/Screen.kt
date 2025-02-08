package com.scritch.app.navigation

import com.scritch.app.categories.Category
import kotlinx.serialization.Serializable

// Main sections of the app
@Serializable object Unauthenticated {
    @Serializable object LandingScreen
    @Serializable data class WizardMediumSelection(
        val category: Category,
        val step: Int,
    ){
        companion object {
            fun stepOne () = WizardMediumSelection(
                category = Category.Medium,
                step = 1,
            )

            fun stepTwo () = WizardMediumSelection(
                category = Category.Support,
                step = 2,
            )
        }
    }
}
@Serializable object Authenticated {
    @Serializable object Home
}