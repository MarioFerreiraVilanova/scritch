package com.scritch.app.jam.data

import dev.gitlive.firebase.firestore.DocumentSnapshot
import dev.gitlive.firebase.firestore.Timestamp
import dev.gitlive.firebase.firestore.toMilliseconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
data class JamDto(
    val id: String,
    val constraint: String?,
    val medium: String?,
    val support: String?,
    val topic: String?,
    val startDate: Instant?,
    val endDate: Instant?,
    val submissionCount: Int = 0,
    val participants: List<String> = emptyList()
){
    constructor(documentSnapshot: DocumentSnapshot): this(
        id = documentSnapshot.id,
        constraint = documentSnapshot.get<String?>("constraint"),
        medium = documentSnapshot.get<String?>("medium"),
        support = documentSnapshot.get<String?>("support"),
        topic = documentSnapshot.get<String?>("topic"),
        startDate = documentSnapshot.get<Timestamp?>("startDate")?.let {
            Instant.fromEpochMilliseconds(it.toMilliseconds().toLong())
        },
        endDate = documentSnapshot.get<Timestamp?>("endDate")?.let {
            Instant.fromEpochMilliseconds(it.toMilliseconds().toLong())
        },
        submissionCount = documentSnapshot.get<Long?>("submissionCount")?.toInt() ?: 0,
        participants = documentSnapshot.get<List<String>?>("participants") ?: emptyList()
    )
}