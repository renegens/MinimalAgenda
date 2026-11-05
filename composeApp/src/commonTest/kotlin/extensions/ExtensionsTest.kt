package extensions

import data.calendar.toEvent
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import testutil.eventEntity
import testutil.at
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.ExperimentalTime

class StringExtensionsTest {
    @Test
    fun toShortNameTakesFirstThreeCharacters() {
        assertEquals("Mon", "Monday".toShortName())
        assertEquals("Dec", "December".toShortName())
        assertEquals("May", "May".toShortName())
    }

    @Test
    fun toShortNameOfShortStringReturnsWholeString() {
        assertEquals("ab", "ab".toShortName())
        assertEquals("", "".toShortName())
    }
}

class IntExtensionsTest {
    @Test
    fun toDigitsSplitsNumber() {
        assertEquals(listOf(2, 0, 2, 4), 2024.toDigits())
        assertEquals(listOf(0), 0.toDigits())
        assertEquals(listOf(7), 7.toDigits())
    }
}

class LongExtensionsTest {
    // These use the system time zone, so only zone-independent properties are asserted.
    @Test
    fun millisToTimeIsZeroPaddedHoursAndMinutes() {
        val s = 1_700_000_000L.millisToTime()
        assertEquals(5, s.length)
        assertEquals(':', s[2])
        assertTrue(s.replace(":", "").all { it.isDigit() })
        assertTrue(s.substring(0, 2).toInt() in 0..23)
        assertTrue(s.substring(3).toInt() in 0..59)
    }

    @Test
    fun millisToHoursAndMinutesAgreesWithMillisToTime() {
        val (h, m) = 1_700_000_000L.millisToHoursAndMinutes()
        assertEquals("$h:$m", 1_700_000_000L.millisToTime())
    }

    @Test
    fun millisToTimeTreatsInputAsSecondsDespiteName() {
        // 60 seconds apart => minute differs by 1 (or wraps); would be identical if treated as millis
        val a = 1_700_000_000L.millisToTime()
        val b = 1_700_000_060L.millisToTime()
        assertTrue(a != b)
    }

    @Test
    fun millisToEpochDayIsStableWithinADay() {
        // 12:00 UTC; any zone within +-11h maps to the same date as UTC. Compare against two close instants.
        val noonUtc = LocalDate(2024, 6, 10).toEpochDays() * 86_400_000L + 12 * 3_600_000L
        assertEquals(noonUtc.millisToEpochDay(), (noonUtc + 1_000).millisToEpochDay())
        val diff = (noonUtc + 86_400_000L).millisToEpochDay() - noonUtc.millisToEpochDay()
        assertTrue(diff == 1L)
    }
}

class DateHelpersTest {
    @Test
    fun screenshotModeIsOff() {
        assertFalse(takingScreenshots)
    }

    @Test
    fun nextHourIsBetweenOneAnd23() {
        assertTrue(nextHour in 1..23)
    }
}

class UnixTimestampTest {
    private fun ts(y: Int, m: Int, d: Int, extra: Long = 0) =
        LocalDate(y, m, d).toEpochDays() * 86_400L + extra

    @Test
    fun epochIsJanuary1970() {
        assertEquals(1 to 1970, getMonthAndYearFromUnixTimestamp(0))
    }

    @Test
    fun lastSecondOfJanuary() {
        assertEquals(1 to 2024, getMonthAndYearFromUnixTimestamp(ts(2024, 2, 1) - 1))
        assertEquals(2 to 2024, getMonthAndYearFromUnixTimestamp(ts(2024, 2, 1)))
    }

    @Test
    fun leapDayBelongsToFebruary() {
        assertEquals(2 to 2024, getMonthAndYearFromUnixTimestamp(ts(2024, 2, 29, 86_399)))
        assertEquals(3 to 2024, getMonthAndYearFromUnixTimestamp(ts(2024, 3, 1)))
    }

    @Test
    fun nonLeapCenturyHasNoFeb29() {
        assertEquals(2 to 2100, getMonthAndYearFromUnixTimestamp(ts(2100, 2, 28, 86_399)))
        assertEquals(3 to 2100, getMonthAndYearFromUnixTimestamp(ts(2100, 3, 1)))
    }

    @Test
    fun leapCenturyDivisibleBy400() {
        assertEquals(2 to 2000, getMonthAndYearFromUnixTimestamp(ts(2000, 2, 29)))
    }

    @Test
    fun yearBoundary() {
        assertEquals(12 to 2023, getMonthAndYearFromUnixTimestamp(ts(2024, 1, 1) - 1))
        assertEquals(1 to 2024, getMonthAndYearFromUnixTimestamp(ts(2024, 1, 1)))
    }

    @Test
    fun everyMonthStartMapsToItself() {
        for (y in listOf(2023, 2024)) for (m in 1..12) {
            assertEquals(m to y, getMonthAndYearFromUnixTimestamp(ts(y, m, 1)), "$y-$m")
        }
    }
}

class AgendaEventsListExtensionsTest {
    private val d1 = LocalDate(2024, 6, 10)
    private val d2 = LocalDate(2024, 7, 1)
    private val a = testutil.agendaEvent(id = "a", start = at(d1, 9))
    private val b = testutil.agendaEvent(id = "b", start = at(d1, 10), isHidden = true)
    private val c = testutil.agendaEvent(id = "c", start = at(d2, 9), isHidden = true)
    private val all = listOf(a, b, c)

    @Test
    fun hasEventsShowAllCountsHiddenEvents() {
        assertTrue(listOf(c).hasEvents(d2.toEpochDays(), showAll = true))
        assertFalse(all.hasEvents(LocalDate(2024, 8, 1).toEpochDays(), showAll = true))
    }

    @Test
    fun hasEventsWithoutShowAllRequiresAVisibleEvent() {
        assertTrue(all.hasEvents(d1.toEpochDays(), showAll = false))
        assertFalse(listOf(c).hasEvents(d2.toEpochDays(), showAll = false))
    }

    @Test
    fun hasHiddenEventsIsTrueWhenAnyEventIsVisible() {
        // Current (counter-intuitive) naming: returns true when at least one event is NOT hidden.
        assertTrue(all.hasHiddenEvents())
        assertFalse(listOf(b, c).hasHiddenEvents())
        assertFalse(emptyList<domain.model.AgendaEvent>().hasHiddenEvents())
    }

    @Test
    fun todayFiltersByEpochDay() {
        assertEquals(listOf(a, b), all.today(d1.toEpochDays()))
    }

    @Test
    fun getInRangeOfMonthAndYear() {
        val june = domain.model.AgendaMonthYear(domain.model.AgendaYear(2024), domain.model.AgendaMonth.June)
        assertEquals(listOf(a, b), all.getInRangeOf(june))
        assertEquals(all, all.getInRangeOf(domain.model.AgendaYear(2024)))
        assertTrue(all.getInRangeOf(domain.model.AgendaYear(2023)).isEmpty())
    }

    @Test
    fun getEventsForMonthUsesStartTimestampInUtc() {
        assertEquals(listOf(a, b), all.getEventsForMonth(domain.model.AgendaMonth.June, 2024))
        assertEquals(listOf(c), all.getEventsForMonth(domain.model.AgendaMonth.July, 2024))
        assertTrue(all.getEventsForMonth(domain.model.AgendaMonth.June, 2023).isEmpty())
    }
}

@OptIn(ExperimentalTime::class)
class EventEntityMappingTest {
    private val d = LocalDate(2024, 6, 10)

    @Test
    fun toEventMapsFields() {
        val e = eventEntity(id = "x", title = "T", start = at(d, 9), end = at(d, 10), allDay = true).toEvent()!!
        assertEquals("x", e.id)
        assertEquals("T", e.title)
        assertTrue(e.allDay)
        assertFalse(e.isHidden)
        assertEquals(d.toEpochDays(), e.epochDay)
        assertEquals(at(d, 9).toInstant(TimeZone.UTC).epochSeconds, e.startTimeInUTC)
    }

    @Test
    fun toEventDefaultsNullOptionalFields() {
        val e = eventEntity(title = null, start = at(d, 9), allDay = null, isRecurring = null).toEvent()!!
        assertEquals("", e.title)
        assertFalse(e.allDay)
        assertFalse(e.isRecurring)
    }

    @Test
    fun toEventReturnsNullWhenStartTimeMissing() {
        assertNull(eventEntity(start = at(d, 9), startTime = null).toEvent())
    }

    @Test
    fun toEventReturnsNullWhenIdMissing() {
        assertNull(eventEntity(id = null, start = at(d, 9)).toEvent())
    }
}
