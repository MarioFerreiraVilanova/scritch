package app.minimal.fasting

import dev.gitlive.firebase.auth.FirebaseUser

data class AppViewState(
    val user: FirebaseUser?
)
