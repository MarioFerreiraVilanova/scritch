package com.scritch.app.userdata

import dev.gitlive.firebase.firestore.DocumentSnapshot

data class UserDataDto(
    val needsInitialSetup: Boolean?,
){
    companion object {
        fun initial() = UserDataDto(
            needsInitialSetup = true,
        )
    }

    constructor(documentSnapshot: DocumentSnapshot): this(
        needsInitialSetup = documentSnapshot.get<Boolean?>("needsInitialSetup"),
    )

    fun asMap() = mapOf(
        "needsInitialSetup" to needsInitialSetup
    )
}
