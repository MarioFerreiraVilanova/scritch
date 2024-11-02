package app.minimal.fasting.app

import dev.gitlive.firebase.auth.FirebaseUser

data class AppViewState(
    val user: FirebaseUser?
)
