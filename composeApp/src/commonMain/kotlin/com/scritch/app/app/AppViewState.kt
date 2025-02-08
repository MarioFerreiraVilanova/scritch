package com.scritch.app.app

import com.scritch.app.auth.User

sealed class AppViewState {

    data object Unauthenticated : AppViewState()

    data class Authenticated(
        val user: User,
        val needsInitialSetup: Boolean? = null,
    ) : AppViewState()
}
