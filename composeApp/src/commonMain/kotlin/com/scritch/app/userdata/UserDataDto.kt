package com.scritch.app.userdata

import dev.gitlive.firebase.firestore.DocumentSnapshot

data class UserDataDto(
    val disabledMediumIds: List<String>?,
    val disabledSupportIds: List<String>?,
){
    companion object {
        fun initial() = UserDataDto(
            disabledMediumIds = emptyList(),
            disabledSupportIds = emptyList(),
        )
    }

    constructor(documentSnapshot: DocumentSnapshot): this(
        disabledMediumIds = documentSnapshot.get<List<String>>("disabledMediumIds"),
        disabledSupportIds = documentSnapshot.get<List<String>>("disabledSupportIds"),
    )

    fun asMap() = mapOf(
        "disabledMediumIds" to disabledMediumIds,
        "disabledSupportIds" to disabledSupportIds
    )
}
