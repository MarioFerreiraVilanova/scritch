package com.scritch.app.navigation

import com.scritch.app.categories.Category
import kotlinx.serialization.Serializable

// Main sections of the app
@Serializable object SplashScreen

@Serializable object Unauthenticated {
    @Serializable object LandingScreen
}
@Serializable object Authenticated {
    @Serializable object Home
    @Serializable object Settings
    @Serializable data class WizardMediumSelection(
        val category: Category,
        val step: Int?,
    ){
        companion object {
            fun stepOne () = WizardMediumSelection(
                category = Category.Topic,
                step = 1,
            )

            private fun stepTwo () = WizardMediumSelection(
                category = Category.Medium,
                step = 2,
            )

            private fun stepThree () = WizardMediumSelection(
                category = Category.Support,
                step = 3,
            )

            private fun stepFour () = WizardMediumSelection(
                category = Category.Constraint,
                step = 4,
            )

            fun nextStep(
                currentStep: Int,
            ) = when (currentStep){
                1 -> stepTwo()
                2 -> stepThree()
                3 -> stepFour()
                else -> null
            }
        }
    }
}