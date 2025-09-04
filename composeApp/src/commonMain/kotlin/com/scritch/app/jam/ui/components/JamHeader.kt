package com.scritch.app.jam.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.until
import com.scritch.app.theme.scritchColorScheme
import com.scritch.app.theme.scritchShapes
import com.scritch.app.theme.scritchTypography
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import scritch.composeapp.generated.resources.Res
import scritch.composeapp.generated.resources.clock
import scritch.composeapp.generated.resources.ends_in_d_h
import scritch.composeapp.generated.resources.ends_in_h_m
import scritch.composeapp.generated.resources.ends_in_m_s
import scritch.composeapp.generated.resources.ends_in_s
import scritch.composeapp.generated.resources.weekly_jam_description
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
fun JamHeader(
    endDate: LocalDateTime?,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(Res.string.weekly_jam_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        AnimatedVisibility(endDate != null) {
            JamEndCountdown(
                endDate = endDate ?: return@AnimatedVisibility
            )
        }
        HorizontalDivider()
    }
}

@OptIn(ExperimentalTime::class)
@Composable
fun JamEndCountdown(
    endDate: LocalDateTime, // if you have Instant, see note below
    timeZone: TimeZone = TimeZone.currentSystemDefault()
) {
    fun remainingSeconds(): Long {
        val end = endDate.toInstant(timeZone)
        val now = Clock.System.now()
        return now.until(end, DateTimeUnit.SECOND)
    }

    @Composable
    fun format(sec: Long): String {
        if (sec <= 0) return "Ended"

        val days = sec / 86_400
        val hours = (sec % 86_400) / 3_600
        val minutes = (sec % 3_600) / 60
        val seconds = sec % 60

        return when {
            days >= 1 -> stringResource(Res.string.ends_in_d_h, days, hours)
            sec >= 3_600 -> stringResource(Res.string.ends_in_h_m, hours, minutes)
            sec >= 60 -> stringResource(Res.string.ends_in_m_s, minutes, seconds)
            else -> stringResource(Res.string.ends_in_s, seconds)
        }
    }

    val remaining by produceState(initialValue = remainingSeconds()) {
        while (true) {
            value = remainingSeconds()
            // Tick every minute until last hour, then every second
            val delayMs = if (value >= 3_600) 60_000L else 1_000L
            delay(delayMs)
        }
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(Res.drawable.clock), 
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = format(remaining),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview
@Composable
private fun JamHeaderWithCountdownPreview() {
    MaterialTheme(
        colorScheme = scritchColorScheme,
        typography = scritchTypography(),
        shapes = scritchShapes,
    ) {
        JamHeader(
            endDate = LocalDateTime(2024, 12, 25, 23, 59, 59)
        )
    }
}

@Preview
@Composable
private fun JamHeaderWithoutCountdownPreview() {
    MaterialTheme(
        colorScheme = scritchColorScheme,
        typography = scritchTypography(),
        shapes = scritchShapes,
    ) {
        JamHeader(endDate = null)
    }
}