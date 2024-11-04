package app.minimal.fasting.app

import app.minimal.fasting.auth.User

sealed class AppViewState {

    data object Unauthenticated : AppViewState()

    data class Authenticated(
        val user: User,
        val needsInitialSetup: Boolean? = null,
    ) : AppViewState()
}
