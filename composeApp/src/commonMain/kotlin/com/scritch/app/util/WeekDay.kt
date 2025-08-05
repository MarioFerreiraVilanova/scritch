package com.scritch.app.util

import androidx.compose.runtime.Composable
import kotlinx.datetime.DayOfWeek
import org.jetbrains.compose.resources.stringResource
import scritch.composeapp.generated.resources.Res
import scritch.composeapp.generated.resources.friday
import scritch.composeapp.generated.resources.monday
import scritch.composeapp.generated.resources.saturday
import scritch.composeapp.generated.resources.sunday
import scritch.composeapp.generated.resources.thursday
import scritch.composeapp.generated.resources.tuesday
import scritch.composeapp.generated.resources.wednesday

@Composable
fun dayOfWeekString (
    day: DayOfWeek,
): String {
    return when (day) {
        DayOfWeek.MONDAY -> stringResource(Res.string.monday)
        DayOfWeek.TUESDAY -> stringResource(Res.string.tuesday)
        DayOfWeek.WEDNESDAY -> stringResource(Res.string.wednesday)
        DayOfWeek.THURSDAY -> stringResource(Res.string.thursday)
        DayOfWeek.FRIDAY -> stringResource(Res.string.friday)
        DayOfWeek.SATURDAY -> stringResource(Res.string.saturday)
        DayOfWeek.SUNDAY -> stringResource(Res.string.sunday)
        else -> ""
    }
}