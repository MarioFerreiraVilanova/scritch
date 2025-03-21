package com.scritch.app.categories

import dev.gitlive.firebase.firestore.DocumentSnapshot

data class OptionDto(
    val id: String,
    val name: String?,
    val description: String?,
    val tips: Map<String, String>?,
    val prompt: String?,
    val frequency: Int?,
){
    constructor(documentSnapshot: DocumentSnapshot): this(
        id = documentSnapshot.id,
        name = documentSnapshot.get<String?>("name"),
        description = documentSnapshot.get<String?>("description"),
        tips = documentSnapshot.get<Map<String, String>?>("TipMap"),
        prompt = documentSnapshot.get<String?>("prompt"),
        frequency = documentSnapshot.get<Int?>("frequency"),
    )
}
