package com.scritch.app.auth

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.map

class AuthenticationRepository {

    fun user() = Firebase.auth.currentUser?.toUser()

    fun userFlow() = Firebase.auth.authStateChanged.map {
        it?.toUser()
    }

    suspend fun login() {
        Firebase.auth.signInAnonymously()
    }

    suspend fun logout() {
        Firebase.auth.signOut()
    }
}

fun FirebaseUser.toUser() = User(id = uid)