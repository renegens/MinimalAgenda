package data.calendar

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CalendarMathTest {

    private val la = TimeZone.of("America/Los_Angeles")
    private val berlin = TimeZone.of("Europe/Berlin")
    private val auckland = TimeZone.of("Pacific/Auckland")

    private fun utcMillis(y: Int, m: Int, d: Int, h: Int = 0, min: Int = 0): Long =
        LocalDateTime(y, m, d, h, min).toInstant(TimeZone.UTC).toEpochMilliseconds()

    private fun day(y: Int, m: Int, d: Int) = LocalDate(y, m, d).toEpochDays()

    @Test
    fun millisToSecondsFloors() {
        assertEquals(1L, CalendarMath.millisToSeconds(1999))
        assertEquals(-1L, CalendarMath.millisToSeconds(-1))
        assertEquals(0L, CalendarMath.millisToSeconds(999))
    }

    @Test
    fun allDayWestOfUtcStaysOnItsDate() {
        val r = CalendarMath.resolveTimes(utcMillis(2026, 3, 10), utcMillis(2026, 3, 11), allDay = true, zone = la)
        assertEquals(day(2026, 3, 10), r.epochDay)
        assertFalse(r.isMultiDay)
        assertEquals(LocalDate(2026, 3, 10), r.startLocalDateTime.date)
        assertEquals(LocalDate(2026, 3, 10), r.endLocalDateTime.date)
        assertEquals(0, r.startLocalDateTime.hour)
    }

    @Test
    fun allDayEastOfUtcStaysOnItsDate() {
        for (zone in listOf(berlin, auckland, TimeZone.UTC)) {
            val r = CalendarMath.resolveTimes(utcMillis(2026, 3, 10), utcMillis(2026, 3, 11), allDay = true, zone = zone)
            assertEquals(day(2026, 3, 10), r.epochDay, zone.id)
            assertFalse(r.isMultiDay, zone.id)
        }
    }

    @Test
    fun allDayStartSecondsAreLocalMidnight() {
        val r = CalendarMath.resolveTimes(utcMillis(2026, 3, 10), utcMillis(2026, 3, 11), allDay = true, zone = berlin)
        val expected = LocalDateTime(2026, 3, 10, 0, 0).toInstant(berlin).epochSeconds
        assertEquals(expected, r.startSeconds)
    }

    @Test
    fun multiDayAllDayCoversInclusiveDays() {
        // 3 days: 10th, 11th, 12th (end is exclusive midnight of the 13th).
        val r = CalendarMath.resolveTimes(utcMillis(2026, 3, 10), utcMillis(2026, 3, 13), allDay = true, zone = la)
        assertTrue(r.isMultiDay)
        assertEquals(LocalDate(2026, 3, 10), r.startLocalDateTime.date)
        assertEquals(LocalDate(2026, 3, 12), r.endLocalDateTime.date)
    }

    @Test
    fun allDayWithEndEqualToBeginIsOneDay() {
        val r = CalendarMath.resolveTimes(utcMillis(2026, 3, 10), utcMillis(2026, 3, 10), allDay = true, zone = la)
        assertFalse(r.isMultiDay)
        assertEquals(day(2026, 3, 10), r.epochDay)
    }

    @Test
    fun timedEventUsesLocalZoneAndSeconds() {
        // 23:30 UTC on the 10th is 00:30 on the 11th in Berlin (CET, UTC+1 in March before DST).
        val begin = utcMillis(2026, 3, 10, 23, 30)
        val r = CalendarMath.resolveTimes(begin, begin + 3_600_000, allDay = false, zone = berlin)
        assertEquals(day(2026, 3, 11), r.epochDay)
        assertEquals(begin / 1000, r.startSeconds)
        assertEquals(begin / 1000 + 3600, r.endSeconds)
        assertEquals(LocalDateTime(2026, 3, 11, 0, 30), r.startLocalDateTime)
        assertFalse(r.isMultiDay)
    }

    @Test
    fun timedEventAcrossMidnightIsMultiDay() {
        val begin = utcMillis(2026, 6, 10, 22, 0)
        val r = CalendarMath.resolveTimes(begin, begin + 4 * 3_600_000, allDay = false, zone = TimeZone.UTC)
        assertTrue(r.isMultiDay)
        assertEquals(LocalDate(2026, 6, 10), r.startLocalDateTime.date)
        assertEquals(LocalDate(2026, 6, 11), r.endLocalDateTime.date)
    }

    @Test
    fun timedEventAcrossMidnightDependsOnZone() {
        // 10:30-11:30 UTC is within one day in UTC but crosses local midnight in Auckland (UTC+13 in Jan).
        val begin = utcMillis(2026, 1, 10, 10, 30)
        val end = begin + 3_600_000
        assertFalse(CalendarMath.resolveTimes(begin, end, false, TimeZone.UTC).isMultiDay)
        val r = CalendarMath.resolveTimes(utcMillis(2026, 1, 10, 10, 30), utcMillis(2026, 1, 10, 11, 30), false, auckland)
        // 23:30 -> 00:30 local
        assertTrue(r.isMultiDay)
    }

    @Test
    fun timedEventWithEndBeforeBeginIsClamped() {
        val begin = utcMillis(2026, 6, 10, 9, 0)
        val r = CalendarMath.resolveTimes(begin, 0, allDay = false, zone = TimeZone.UTC)
        assertEquals(r.startSeconds, r.endSeconds)
        assertFalse(r.isMultiDay)
    }

    @Test
    fun instanceIdRoundTrips() {
        val id = CalendarMath.buildInstanceId(42, 1_760_000_000_000)
        assertEquals("42/1760000000000", id)
        assertEquals(CalendarMath.InstanceId(42, 1_760_000_000_000), CalendarMath.parseInstanceId(id))
    }

    @Test
    fun instanceIdsOfRecurringEventDiffer() {
        assertTrue(CalendarMath.buildInstanceId(7, 1000) != CalendarMath.buildInstanceId(7, 2000))
    }

    @Test
    fun parseInstanceIdRejectsGarbage() {
        assertNull(CalendarMath.parseInstanceId(null))
        assertNull(CalendarMath.parseInstanceId(""))
        assertNull(CalendarMath.parseInstanceId("42"))
        assertNull(CalendarMath.parseInstanceId("a/b"))
        assertNull(CalendarMath.parseInstanceId("1/2/3"))
        assertNull(CalendarMath.parseInstanceId("ABC/RID=123"))
    }

    @Test
    fun recurringDetection() {
        assertTrue(CalendarMath.isRecurring("FREQ=DAILY;COUNT=3"))
        assertFalse(CalendarMath.isRecurring(null))
        assertFalse(CalendarMath.isRecurring("  "))
    }

    // ---- write conversion ----

    @Test
    fun timedWriteUsesRealInstantAndZone() {
        val w = CalendarMath.toWriteTimes(
            LocalDateTime(2026, 3, 10, 9, 0), LocalDateTime(2026, 3, 10, 10, 30), allDay = false, zone = berlin
        )
        assertEquals(utcMillis(2026, 3, 10, 8, 0), w.dtStartMillis)
        assertEquals(utcMillis(2026, 3, 10, 9, 30), w.dtEndMillis)
        assertEquals("Europe/Berlin", w.timeZoneId)
    }

    @Test
    fun timedWriteNeverEndsBeforeStart() {
        val w = CalendarMath.toWriteTimes(
            LocalDateTime(2026, 3, 10, 9, 0), LocalDateTime(2026, 3, 10, 8, 0), allDay = false, zone = berlin
        )
        assertEquals(w.dtStartMillis, w.dtEndMillis)
    }

    @Test
    fun allDayWriteIsUtcMidnightWithExclusiveEndInAnyZone() {
        for (zone in listOf(la, berlin, auckland)) {
            val w = CalendarMath.toWriteTimes(
                LocalDateTime(2026, 3, 10, 0, 0), LocalDateTime(2026, 3, 10, 23, 59), allDay = true, zone = zone
            )
            assertEquals(utcMillis(2026, 3, 10), w.dtStartMillis)
            assertEquals(utcMillis(2026, 3, 11), w.dtEndMillis)
            assertEquals("UTC", w.timeZoneId)
        }
    }

    @Test
    fun allDayMultiDayWriteEndsAfterLastDay() {
        val w = CalendarMath.toWriteTimes(
            LocalDateTime(2026, 3, 10, 0, 0), LocalDateTime(2026, 3, 12, 23, 59), allDay = true, zone = la
        )
        assertEquals(utcMillis(2026, 3, 10), w.dtStartMillis)
        assertEquals(utcMillis(2026, 3, 13), w.dtEndMillis)
    }

    @Test
    fun endAfterCrossesMidnight() {
        val end = CalendarMath.endAfter(LocalDateTime(2026, 3, 10, 23, 30), 90, berlin)
        assertEquals(LocalDateTime(2026, 3, 11, 1, 0), end)
    }

    @Test
    fun roundTripTimed() {
        for (zone in listOf(la, berlin, auckland)) {
            val start = LocalDateTime(2026, 7, 4, 18, 15)
            val end = CalendarMath.endAfter(start, 75, zone)
            val w = CalendarMath.toWriteTimes(start, end, allDay = false, zone = zone)
            val r = CalendarMath.resolveTimes(w.dtStartMillis, w.dtEndMillis, allDay = false, zone = zone)
            assertEquals(start, r.startLocalDateTime)
            assertEquals(end, r.endLocalDateTime)
            assertEquals(day(2026, 7, 4), r.epochDay)
        }
    }

    @Test
    fun roundTripAllDay() {
        for (zone in listOf(la, berlin, auckland)) {
            val start = LocalDateTime(2026, 12, 30, 0, 0)
            val end = LocalDateTime(2027, 1, 1, 23, 59)
            val w = CalendarMath.toWriteTimes(start, end, allDay = true, zone = zone)
            val r = CalendarMath.resolveTimes(w.dtStartMillis, w.dtEndMillis, allDay = true, zone = zone)
            assertEquals(LocalDate(2026, 12, 30), r.startLocalDateTime.date)
            assertEquals(LocalDate(2027, 1, 1), r.endLocalDateTime.date)
            assertEquals(day(2026, 12, 30), r.epochDay)
            assertTrue(r.isMultiDay)
        }
    }
    @Test
    fun eventKitAllDayBoundsRoundTripWithoutAnExtraEndingDay() {
        for (zone in listOf(la, berlin, auckland)) {
            val start = LocalDateTime(2026, 3, 28, 0, 0)
            val inclusiveEnd = LocalDateTime(2026, 3, 30, 23, 59, 59)
            val exclusiveEnd = CalendarMath.allDayEndExclusive(inclusiveEnd)
            assertEquals(LocalDateTime(2026, 3, 31, 0, 0), exclusiveEnd)
            val resolved = CalendarMath.resolveLocalCalendarTimes(
                start.toInstant(zone).toEpochMilliseconds(),
                exclusiveEnd.toInstant(zone).toEpochMilliseconds(),
                allDay = true,
                zone = zone,
            )
            assertEquals(start, resolved.startLocalDateTime)
            assertEquals(inclusiveEnd, resolved.endLocalDateTime)
            assertTrue(resolved.isMultiDay)
        }
    }

    @Test
    fun singleDayEventKitEventIsNotMultiDay() {
        val start = LocalDateTime(2026, 3, 29, 0, 0)
        val end = CalendarMath.allDayEndExclusive(start)
        val resolved = CalendarMath.resolveLocalCalendarTimes(
            start.toInstant(berlin).toEpochMilliseconds(),
            end.toInstant(berlin).toEpochMilliseconds(),
            allDay = true, zone = berlin,
        )
        assertFalse(resolved.isMultiDay)
        assertEquals(start.date, resolved.endLocalDateTime.date)
    }

}
