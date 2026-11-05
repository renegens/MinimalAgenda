package data.calendar

import kotlin.time.Instant

/** Exact instants disambiguate repeated local times when daylight saving time ends. */
data class EventTimeRange(val start: Instant, val end: Instant)
