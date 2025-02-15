package com.scritch.app.categories

import dev.gitlive.firebase.firestore.DocumentSnapshot

data class OptionDto(
    val id: String,
    val name: String?,
    val description: String?,
    val tips: String?,
){
    constructor(documentSnapshot: DocumentSnapshot): this(
        id = documentSnapshot.id,
        name = documentSnapshot.get<String?>("name"),
        description = documentSnapshot.get<String?>("description"),
        tips = documentSnapshot.get<String?>("tips"),
    )
}
