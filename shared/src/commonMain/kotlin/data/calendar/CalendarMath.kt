package data.calendar

import kotlinx.datetime.LocalDate
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.atTime
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

/**
 * Pure helpers for turning platform calendar rows (epoch milliseconds) into the app's model
 * (epoch seconds, local date-times, epoch days). No platform APIs, so they are unit-testable.
 */
object CalendarMath {

    /** Result of [resolveTimes]. Times are epoch seconds, matching `EventEntity`. */
    data class ResolvedTimes(
        val startSeconds: Long,
        val endSeconds: Long,
        val startLocalDateTime: LocalDateTime,
        val endLocalDateTime: LocalDateTime,
        val epochDay: Long,
        val isMultiDay: Boolean,
    )

    /** Milliseconds to seconds, rounding toward negative infinity. */
    fun millisToSeconds(millis: Long): Long = millis.floorDiv(1000L)

    /**
     * Resolves an instance's begin/end (epoch millis, as stored by `CalendarContract`) for display in [zone].
     *
     * Timed events are converted in [zone].
     *
     * All-day events are stored as UTC midnight (end exclusive, also UTC midnight), so their dates are
     * computed in UTC; computing them in a zone west of UTC would land a day early. The result is placed
     * at local midnight of the first day and 23:59:59 local of the last day (like EventKit does), and a
     * one-day all-day event covers exactly one day.
     */
    fun resolveTimes(beginMillis: Long, endMillis: Long, allDay: Boolean, zone: TimeZone): ResolvedTimes {
        return if (allDay) resolveAllDay(beginMillis, endMillis, zone) else resolveTimed(beginMillis, endMillis, zone)
    }

    private fun resolveTimed(beginMillis: Long, endMillis: Long, zone: TimeZone): ResolvedTimes {
        val start = Instant.fromEpochMilliseconds(beginMillis).toLocalDateTime(zone)
        val end = Instant.fromEpochMilliseconds(maxOf(endMillis, beginMillis)).toLocalDateTime(zone)
        return ResolvedTimes(
            startSeconds = millisToSeconds(beginMillis),
            endSeconds = millisToSeconds(maxOf(endMillis, beginMillis)),
            startLocalDateTime = start,
            endLocalDateTime = end,
            epochDay = start.date.toEpochDays(),
            isMultiDay = isMultiDay(start.date, end.date),
        )
    }

    private fun resolveAllDay(beginMillis: Long, endMillis: Long, zone: TimeZone): ResolvedTimes {
        val firstDay = Instant.fromEpochMilliseconds(beginMillis).toLocalDateTime(TimeZone.UTC).date
        // End is exclusive midnight of the following day, so the last covered day is (end - 1ms).
        val lastDay = if (endMillis > beginMillis) {
            Instant.fromEpochMilliseconds(endMillis - 1).toLocalDateTime(TimeZone.UTC).date
        } else {
            firstDay
        }
        val start = firstDay.atStartOfDayIn(zone)
        val endLocal = lastDay.atTime(23, 59, 59)
        return ResolvedTimes(
            startSeconds = start.epochSeconds,
            endSeconds = endLocal.toInstant(zone).epochSeconds,
            startLocalDateTime = firstDay.atTime(0, 0),
            endLocalDateTime = endLocal,
            epochDay = firstDay.toEpochDays(),
            isMultiDay = isMultiDay(firstDay, lastDay),
        )
    }

    fun isMultiDay(startDate: LocalDate, endDate: LocalDate): Boolean = startDate != endDate

    /**
     * Instances of a recurring event share one `EVENT_ID`, so the app id of an instance is
     * `"<eventId>/<beginMillis>"`. The id is stable for an instance and unique across instances.
     */
    fun buildInstanceId(eventId: Long, beginMillis: Long): String = "$eventId/$beginMillis"

    data class InstanceId(val eventId: Long, val beginMillis: Long)

    /** Inverse of [buildInstanceId]; null when [id] isn't in that format. */
    fun parseInstanceId(id: String?): InstanceId? {
        if (id == null) return null
        val parts = id.split('/')
        if (parts.size != 2) return null
        val eventId = parts[0].toLongOrNull() ?: return null
        val begin = parts[1].toLongOrNull() ?: return null
        return InstanceId(eventId, begin)
    }

    /** Values to store in `CalendarContract.Events` (milliseconds) for a non-recurring event. */
    data class WriteTimes(val dtStartMillis: Long, val dtEndMillis: Long, val timeZoneId: String)

    /**
     * Inverse of [resolveTimes]. [start] and [end] are local date-times in [zone].
     *
     * Timed events are stored as real instants with `EVENT_TIMEZONE` = [zone] (end is never before start).
     *
     * All-day events must be stored as UTC midnight with `EVENT_TIMEZONE` "UTC" and an exclusive end, so
     * [start]'s date is the first day, [end]'s date is the LAST day (inclusive, as [resolveTimes] reports it)
     * and the stored end is UTC midnight of the day after.
     */
    fun toWriteTimes(start: LocalDateTime, end: LocalDateTime, allDay: Boolean, zone: TimeZone): WriteTimes {
        if (allDay) {
            val firstDay = start.date
            val lastDay = if (end.date > firstDay) end.date else firstDay
            return WriteTimes(
                dtStartMillis = firstDay.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds(),
                dtEndMillis = lastDay.plus(1, DateTimeUnit.DAY).atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds(),
                timeZoneId = TimeZone.UTC.id,
            )
        }
        val startInstant = start.toInstant(zone)
        val endInstant = end.toInstant(zone).let { if (it < startInstant) startInstant else it }
        return WriteTimes(startInstant.toEpochMilliseconds(), endInstant.toEpochMilliseconds(), zone.id)
    }

    /** [start] plus [durationInMinutes] on the real timeline of [zone] (may cross midnight). */
    fun endAfter(start: LocalDateTime, durationInMinutes: Long, zone: TimeZone): LocalDateTime =
        (start.toInstant(zone) + durationInMinutes.minutes).toLocalDateTime(zone)

    fun isRecurring(rrule: String?): Boolean = !rrule.isNullOrBlank()
}
