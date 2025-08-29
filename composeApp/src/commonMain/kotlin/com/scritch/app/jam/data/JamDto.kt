package com.scritch.app.jam.data

import dev.gitlive.firebase.firestore.DocumentSnapshot
import dev.gitlive.firebase.firestore.Timestamp
import dev.gitlive.firebase.firestore.toMilliseconds
import kotlinx.datetime.Instant
import kotlin.time.ExperimentalTime

enum class JamStatus {
    ACTIVE,
    EXPIRED,
    UPCOMING;
    
    companion object {
        fun fromString(status: String?): JamStatus = when (status?.lowercase()) {
            "active" -> ACTIVE
            "expired" -> EXPIRED
            "upcoming" -> UPCOMING
            else -> ACTIVE // Default to active for backward compatibility
        }
    }
}

@OptIn(ExperimentalTime::class)
data class JamDto(
    val id: String,
    val constraint: String?,
    val medium: String?,
    val support: String?,
    val topic: String?,
    val startDate: Instant?,
    val endDate: Instant?,
    val status: String?
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
        status = documentSnapshot.get<String?>("status")
    )
    
    val jamStatus: JamStatus get() = JamStatus.fromString(status)
}