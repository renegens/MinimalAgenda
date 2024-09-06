package extensions

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

val today: LocalDate
    get() = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()).date