package com.scritch.app.jam

import dev.gitlive.firebase.firestore.DocumentSnapshot

data class JamDto(
    val id: String,
    val constraint: String?,
    val medium: String?,
    val support: String?,
    val topic: String?,
){
    constructor(documentSnapshot: DocumentSnapshot): this(
        id = documentSnapshot.id,
        constraint = documentSnapshot.get<String?>("constraint"),
        medium = documentSnapshot.get<String?>("medium"),
        support = documentSnapshot.get<String?>("support"),
        topic = documentSnapshot.get<String?>("topic"),
    )
}
