package domain.use_case

import domain.model.DayListViewType
import extensions.today
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import testutil.agendaEvent
import testutil.at
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class FlatCalendarViewFactoryTest {

    private val generator = CalendarGenerator()
    private val factory = FlatCalendarViewFactory(generator)
    private val day = LocalDate(today.year, 6, 10)

    private fun eventsOn(list: List<DayListViewType>, date: LocalDate): List<DayListViewType.Event> {
        val header = list.indexOfFirst { it is DayListViewType.Header && it.agendaDay.epochDay == date.toEpochDays() }
        val divider = list.indexOfFirst { it is DayListViewType.Divider && it.epochDay == date.toEpochDays() }
        return list.subList(header + 1, divider).filterIsInstance<DayListViewType.Event>()
    }

    @Test
    fun withoutEventsEachDayIsHeaderThenDivider() {
        val list = factory.build()
        assertEquals(generator.calendar.size * 2, list.size)
        generator.calendar.forEachIndexed { i, d ->
            val header = list[i * 2]
            val divider = list[i * 2 + 1]
            assertIs<DayListViewType.Header>(header)
            assertEquals(d, header.agendaDay)
            assertIs<DayListViewType.Divider>(divider)
            assertEquals(d.epochDay, divider.epochDay)
        }
    }

    @Test
    fun emptyEventListEqualsNull() {
        assertEquals(factory.build().size, factory.build(emptyList()).size)
    }

    @Test
    fun eventsAreInsertedBetweenHeaderAndDividerOfTheirDay() {
        val e = agendaEvent(id = "a", start = at(day, 9), end = at(day, 10))
        val list = factory.build(listOf(e))
        assertEquals(generator.calendar.size * 2 + 1, list.size)
        assertEquals(listOf(e), eventsOn(list, day).map { it.event })
        assertTrue(eventsOn(list, day.plus(DatePeriod(days = 1))).isEmpty())
    }

    @Test
    fun eventsOnADayAreSortedByStartTimeThenTitle() {
        val late = agendaEvent(id = "1", title = "Z", start = at(day, 15), end = at(day, 16))
        val earlyB = agendaEvent(id = "2", title = "B", start = at(day, 8), end = at(day, 9))
        val earlyA = agendaEvent(id = "3", title = "A", start = at(day, 8), end = at(day, 9))
        val list = factory.build(listOf(late, earlyB, earlyA))
        assertEquals(listOf("3", "2", "1"), eventsOn(list, day).map { it.event.id })
    }

    @Test
    fun lastDayOfMultiDayEventSortsByEndTime() {
        val start = at(day.plus(DatePeriod(days = -1)), 20)
        val multiLast = agendaEvent(
            id = "m", title = "A", start = start, end = at(day, 11),
            isLastDayInMultiDayEvent = true, epochDay = day.toEpochDays()
        )
        val regular = agendaEvent(id = "r", title = "B", start = at(day, 10), end = at(day, 12))
        val list = factory.build(listOf(multiLast, regular))
        // regular sorts by start (10:00), multi-day last day by end (11:00)
        assertEquals(listOf("r", "m"), eventsOn(list, day).map { it.event.id })
    }

    @Test
    fun eventsOutsideCalendarRangeAreDropped() {
        val far = LocalDate(today.year + 10, 1, 1)
        val list = factory.build(listOf(agendaEvent(start = at(far, 9))))
        assertEquals(generator.calendar.size * 2, list.size)
    }

    @Test
    fun hiddenEventsAreStillListed() {
        val e = agendaEvent(start = at(day, 9), isHidden = true)
        assertEquals(1, eventsOn(factory.build(listOf(e)), day).size)
    }
}
