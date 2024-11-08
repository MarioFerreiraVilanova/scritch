package app.minimal.fasting.common

import dev.gitlive.firebase.firestore.Timestamp
import dev.gitlive.firebase.firestore.fromMilliseconds
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

fun now() = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
fun LocalDateTime.toEpochMilliseconds() = this.toInstant(
    timeZone = TimeZone.currentSystemDefault()
).toEpochMilliseconds().toDouble()
fun LocalDateTime.toTimestamp() = Timestamp.fromMilliseconds(
    milliseconds = this.toEpochMilliseconds(),
)