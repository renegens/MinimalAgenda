package domain.use_case

import extensions.today
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import platform.uses24HourClock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.ExperimentalTime

class DemoDayGeneratorTest {

    private val events = generateDemoAgendaEvents()

    @Test
    fun generatesSixEventsWithSequentialIds() {
        assertEquals(listOf("1", "2", "3", "4", "5", "6"), events.map { it.id })
    }

    @Test
    fun allEventsFallOnToday() {
        val epochDay = today.toEpochDays()
        events.forEach {
            assertEquals(epochDay, it.epochDay)
            assertEquals(today, it.startLocalDateTime.date)
            assertEquals(today, it.endLocalDateTime.date)
        }
    }

    @Test
    fun eventsAreSingleDayTimedVisibleAndNonRecurring() {
        events.forEach {
            assertEquals(false, it.isMultiDay)
            assertEquals(false, it.allDay)
            assertEquals(false, it.isHidden)
            assertEquals(false, it.isRecurring)
            assertEquals(false, it.isLastDayInMultiDayEvent)
        }
    }

    @Test
    fun startHoursAreInExpectedOrder() {
        val starts = events.map { it.startLocalDateTime.hour to it.startLocalDateTime.minute }
        assertEquals(listOf(9 to 0, 10 to 30, 12 to 0, 14 to 0, 17 to 30, 20 to 30), starts)
    }

    @Test
    fun durationsAreOneHourExceptDeepWorkWhichIsTwo() {
        val minutes = events.map { (it.endTimeInUTC!! - it.startTimeInUTC) / 60 }
        assertEquals(listOf(60L, 60L, 60L, 120L, 60L, 60L), minutes)
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun epochSecondsAreTheLocalDateTimeInterpretedAsUtc() {
        events.forEach {
            assertEquals(it.startLocalDateTime.toInstant(TimeZone.UTC).epochSeconds, it.startTimeInUTC)
            assertEquals(it.endLocalDateTime.toInstant(TimeZone.UTC).epochSeconds, it.endTimeInUTC)
            assertTrue(it.endTimeInUTC!! > it.startTimeInUTC)
        }
    }

    @Test
    fun clockFormatMatchesPlatform() {
        events.forEach { assertEquals(uses24HourClock(), it.uses24HourClock) }
    }

    @Test
    fun titlesAndLocations() {
        assertEquals("Weekly Team Meeting", events[1].title)
        assertEquals("Office", events[1].location)
        assertEquals("Deep Work", events[3].title)
        assertEquals("Settings", events[5].location)
    }

    @Test
    fun onboardingEventsHaveFiveEntriesAndMatchClockStyle() {
        val items = generateOnboardingDemoEvents()
        assertEquals(5, items.size)
        assertEquals("Weekly Team Meeting", items[1].second)
        if (uses24HourClock()) {
            items.forEach { assertNull(it.first.ampm) }
            assertEquals("14", items[3].first.hours)
        } else {
            assertEquals(listOf("AM", "AM", "PM", "PM", "PM"), items.map { it.first.ampm })
            assertEquals("02", items[3].first.hours)
        }
    }
}
