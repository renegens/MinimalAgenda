package extensions

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

val today =
    Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

val todayEpochDay = today.toEpochDays()