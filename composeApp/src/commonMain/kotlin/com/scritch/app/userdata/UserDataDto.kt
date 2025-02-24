package com.scritch.app.userdata

import dev.gitlive.firebase.firestore.DocumentSnapshot

data class UserDataDto(
    val disabledTopicIds: List<String>?,
    val disabledMediumIds: List<String>?,
    val disabledSupportIds: List<String>?,
    val categorySettings: Map<String, Boolean>?,
) {
    companion object {
        fun initial() = UserDataDto(
            disabledTopicIds = emptyList(),
            disabledMediumIds = emptyList(),
            disabledSupportIds = emptyList(),
            categorySettings = emptyMap(),
        )
    }

    constructor(documentSnapshot: DocumentSnapshot) : this(
        disabledTopicIds = documentSnapshot.get<List<String>?>("disabledTopicIds"),
        disabledMediumIds = documentSnapshot.get<List<String>?>("disabledMediumIds"),
        disabledSupportIds = documentSnapshot.get<List<String>?>("disabledSupportIds"),
        categorySettings = documentSnapshot.get<Map<String, Boolean>?>("unImposedCategories"),
    )

    fun asMap() = mapOf(
        "disabledMediumIds" to disabledMediumIds,
        "disabledSupportIds" to disabledSupportIds,
        "unImposedCategories" to categorySettings,
    )
}
