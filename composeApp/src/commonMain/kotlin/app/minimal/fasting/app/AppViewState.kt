package app.minimal.fasting.app

import dev.gitlive.firebase.auth.FirebaseUser

sealed class AppViewState {

    data object Unauthenticated : AppViewState()

    data class Authenticated (
        val user: FirebaseUser,
        val needsInitialSetup: Boolean? = null,
    ): AppViewState ()
}
