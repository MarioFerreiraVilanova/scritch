package com.scritch.app.userdata

import com.scritch.app.auth.AuthenticationRepository
import kotlinx.coroutines.flow.Flow

class LoadUserDataUseCase(
    private val authenticationRepository: AuthenticationRepository,
    private val userDataRepository: UserDataRepository,
) {

    operator fun invoke(): Flow<UserData>? = authenticationRepository.user()?.id?.let { userId ->
        userDataRepository.userDataFlow(userId)
    }
}