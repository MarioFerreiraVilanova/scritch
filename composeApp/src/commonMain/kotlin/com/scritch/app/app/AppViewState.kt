package com.scritch.app.app

import com.scritch.app.auth.User

sealed class AppViewState {

    data object StatingApp: AppViewState()

    data object Unauthenticated : AppViewState()

    data class Authenticated(
        val user: User,
    ) : AppViewState()
}
