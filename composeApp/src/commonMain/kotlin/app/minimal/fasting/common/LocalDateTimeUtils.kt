package app.minimal.fasting.common

import dev.gitlive.firebase.firestore.Timestamp
import dev.gitlive.firebase.firestore.fromMilliseconds
import dev.gitlive.firebase.firestore.toMilliseconds
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.DurationUnit
import kotlin.time.toDuration

fun now() = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
fun LocalDateTime.toEpochMilliseconds() = this.toInstant(
    timeZone = TimeZone.currentSystemDefault()
).toEpochMilliseconds().toDouble()
fun LocalDateTime.toTimestamp() = Timestamp.fromMilliseconds(
    milliseconds = this.toEpochMilliseconds(),
)
fun LocalDateTime.Companion.fromTimestamp(timestamp: Timestamp): LocalDateTime {
    val instant = Instant.fromEpochMilliseconds(timestamp.toMilliseconds().toLong())
    return instant.toLocalDateTime(TimeZone.currentSystemDefault())
}

fun LocalDateTime.plusHours(hours: Int): LocalDateTime = this
    .toInstant(TimeZone.currentSystemDefault())
    .plus(duration = hours.toDuration(DurationUnit.HOURS))
    .toLocalDateTime(TimeZone.currentSystemDefault())

fun LocalDateTime.toHoursAndMinutes(): String {
    return "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
}