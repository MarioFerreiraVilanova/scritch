package com.scritch.app.userprofile

import dev.gitlive.firebase.firestore.DocumentSnapshot
import dev.gitlive.firebase.firestore.Timestamp

data class UserProfileDto(
    val userId: String?,
    val nickname: String?,
    val createdAt: Timestamp?,
) {
    companion object {
        fun initial(userId: String, nickname: String) = UserProfileDto(
            userId = userId,
            nickname = nickname,
            createdAt = Timestamp.now(),
        )
    }

    constructor(documentSnapshot: DocumentSnapshot) : this(
        userId = documentSnapshot.get<String?>("userId"),
        nickname = documentSnapshot.get<String?>("nickname"),
        createdAt = documentSnapshot.get<Timestamp?>("createdAt"),
    )

    fun asMap() = mapOf(
        "userId" to userId,
        "nickname" to nickname,
        "createdAt" to createdAt,
    )
}