package domain.use_case

import data.local.HiddenEventEntity
import domain.model.DayListViewType
import extensions.today
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import platform.CalendarPermissionHandler
import testutil.FakeCalendarRepository
import testutil.FakeHiddenEventsRepository
import testutil.FakePermissionHandler
import testutil.at
import testutil.eventEntity
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GetAllEventsUseCaseTest {

    private val day = LocalDate(today.year, 6, 10)
    private val calendarRepo = FakeCalendarRepository()
    private val hiddenRepo = FakeHiddenEventsRepository()
    private val permission = FakePermissionHandler(CalendarPermissionHandler.State.FullAccess)

    private fun useCase() = GetAllEventsUseCase(
        calendarRepository = calendarRepo,
        hiddenEventsRepository = hiddenRepo,
        flatCalendarViewFactory = FlatCalendarViewFactory(CalendarGenerator()),
        showDemoModeUseCase = ShouldShowDemoModeUseCase(permission)
    )

    private suspend fun run(): GetAllEventsUseCase.State.Success =
        useCase()(24) as GetAllEventsUseCase.State.Success

    @Test
    fun noEventsGivesEmptyAgendaButFullDayList() = runTest {
        val result = run()
        assertTrue(result.agendaEvents.isEmpty())
        assertTrue(result.dayListViewTypes.all { it !is DayListViewType.Event })
        assertTrue(result.dayListViewTypes.isNotEmpty())
    }

    @Test
    fun passesDurationToRepository() = runTest {
        useCase()(7)
        assertEquals(7, calendarRepo.lastDuration)
        assertEquals(1, calendarRepo.getEventsCalls)
    }

    @Test
    fun singleDayEventsKeepTheirDay() = runTest {
        val next = day.plus(DatePeriod(days = 1))
        calendarRepo.events = listOf(
            eventEntity(id = "a", start = at(day, 9), end = at(day, 10)),
            eventEntity(id = "b", start = at(day, 11), end = at(day, 12)),
            eventEntity(id = "c", start = at(next, 9), end = at(next, 10)),
        )
        val result = run()
        assertEquals(3, result.agendaEvents.size)
        assertEquals(listOf("a", "b"), result.agendaEvents.filter { it.epochDay == day.toEpochDays() }.map { it.id })
        assertEquals(listOf("c"), result.agendaEvents.filter { it.epochDay == next.toEpochDays() }.map { it.id })
        val inList = result.dayListViewTypes.filterIsInstance<DayListViewType.Event>()
        assertEquals(3, inList.size)
    }

    @Test
    fun eventFieldsAreMappedFromEntity() = runTest {
        calendarRepo.events = listOf(
            eventEntity(id = "a", title = "Dentist", location = "Clinic", start = at(day, 9), end = at(day, 10), isRecurring = true)
        )
        val e = run().agendaEvents.single()
        assertEquals("Dentist", e.title)
        assertEquals("Clinic", e.location)
        assertTrue(e.isRecurring)
        assertFalse(e.isHidden)
        assertFalse(e.isLastDayInMultiDayEvent)
    }

    @Test
    fun allDayEventIsPassedThrough() = runTest {
        calendarRepo.events = listOf(
            eventEntity(id = "a", start = at(day, 0), end = at(day, 23, 59), allDay = true)
        )
        val e = run().agendaEvents.single()
        assertTrue(e.allDay)
        assertEquals(day.toEpochDays(), e.epochDay)
    }

    @Test
    fun threeDayEventIsExpandedIntoStartIntermediateAndLastDay() = runTest {
        val d1 = day
        val d2 = day.plus(DatePeriod(days = 1))
        val d3 = day.plus(DatePeriod(days = 2))
        calendarRepo.events = listOf(
            eventEntity(id = "m", start = at(d1, 22), end = at(d3, 8))
        )
        val events = run().agendaEvents
        assertEquals(listOf(d1, d2, d3).map { it.toEpochDays() }, events.map { it.epochDay })
        val (first, middle, last) = events
        assertFalse(first.allDay)
        assertFalse(first.isLastDayInMultiDayEvent)
        assertTrue(middle.allDay)
        assertFalse(middle.isLastDayInMultiDayEvent)
        assertFalse(last.allDay)
        assertTrue(last.isLastDayInMultiDayEvent)
        assertTrue(events.all { it.id == "m" })
    }

    @Test
    fun twoDayEventHasStartAndLastDayOnly() = runTest {
        val d2 = day.plus(DatePeriod(days = 1))
        calendarRepo.events = listOf(eventEntity(id = "m", start = at(day, 22), end = at(d2, 2)))
        val events = run().agendaEvents
        assertEquals(2, events.size)
        assertFalse(events[0].isLastDayInMultiDayEvent)
        assertFalse(events[0].allDay)
        assertTrue(events[1].isLastDayInMultiDayEvent)
        assertFalse(events[1].allDay)
    }

    @Test
    fun multiDayEventShowsUpOnEachDayInTheList() = runTest {
        val d3 = day.plus(DatePeriod(days = 2))
        calendarRepo.events = listOf(eventEntity(id = "m", start = at(day, 22), end = at(d3, 8)))
        val list = run().dayListViewTypes.filterIsInstance<DayListViewType.Event>()
        assertEquals(3, list.size)
    }

    @Test
    fun monthSpanningEventCrossesMonthBoundary() = runTest {
        val start = LocalDate(today.year, 1, 31)
        val end = LocalDate(today.year, 2, 2)
        calendarRepo.events = listOf(eventEntity(id = "m", start = at(start, 20), end = at(end, 9)))
        val days = run().agendaEvents.map { LocalDate.fromEpochDays(it.epochDay!!) }
        assertEquals(listOf(start, LocalDate(today.year, 2, 1), end), days)
    }

    @Test
    fun hiddenEventIsFlaggedOnlyOnMatchingDay() = runTest {
        val next = day.plus(DatePeriod(days = 1))
        calendarRepo.events = listOf(
            eventEntity(id = "a", start = at(day, 9), end = at(day, 10)),
            eventEntity(id = "b", start = at(day, 11), end = at(day, 12)),
            eventEntity(id = "a", start = at(next, 9), end = at(next, 10)),
        )
        hiddenRepo.hidden = listOf(HiddenEventEntity("a", day.toEpochDays()))
        val events = run().agendaEvents
        assertEquals(
            listOf(true, false, false),
            events.map { it.isHidden }
        )
    }

    @Test
    fun hiddenMatchingIgnoresRecurrenceIdSuffix() = runTest {
        calendarRepo.events = listOf(
            eventEntity(id = "abc/RID=111", start = at(day, 9), end = at(day, 10)),
        )
        hiddenRepo.hidden = listOf(HiddenEventEntity("abc/RID=999", day.toEpochDays()))
        assertTrue(run().agendaEvents.single().isHidden)
    }

    @Test
    fun hiddenMultiDayEventOnlyHidesThatDay() = runTest {
        val d2 = day.plus(DatePeriod(days = 1))
        val d3 = day.plus(DatePeriod(days = 2))
        calendarRepo.events = listOf(eventEntity(id = "m", start = at(day, 22), end = at(d3, 8)))
        hiddenRepo.hidden = listOf(HiddenEventEntity("m", d2.toEpochDays()))
        assertEquals(listOf(false, true, false), run().agendaEvents.map { it.isHidden })
    }

    @Test
    fun hiddenEventsAreKeptInTheResultNotFilteredOut() = runTest {
        calendarRepo.events = listOf(eventEntity(id = "a", start = at(day, 9), end = at(day, 10)))
        hiddenRepo.hidden = listOf(HiddenEventEntity("a", day.toEpochDays()))
        val result = run()
        assertEquals(1, result.agendaEvents.size)
        val listed = result.dayListViewTypes.filterIsInstance<DayListViewType.Event>().single()
        assertTrue(listed.event.isHidden)
    }

    @Test
    fun hiddenEntryWithoutMatchingEventIsIgnored() = runTest {
        calendarRepo.events = listOf(eventEntity(id = "a", start = at(day, 9), end = at(day, 10)))
        hiddenRepo.hidden = listOf(HiddenEventEntity("zzz", day.toEpochDays()))
        assertFalse(run().agendaEvents.single().isHidden)
    }

    @Test
    fun demoModeOffUsesCalendarRepository() = runTest {
        permission.state = CalendarPermissionHandler.State.FullAccess
        calendarRepo.events = listOf(eventEntity(id = "a", start = at(day, 9), end = at(day, 10)))
        val result = run()
        assertEquals(1, calendarRepo.getEventsCalls)
        assertEquals(listOf("a"), result.agendaEvents.map { it.id })
    }

    @Test
    fun demoModeOnWhenDeniedUsesDemoEventsAndSkipsRepository() = runTest {
        permission.state = CalendarPermissionHandler.State.Denied
        calendarRepo.events = listOf(eventEntity(id = "a", start = at(day, 9), end = at(day, 10)))
        val result = run()
        assertEquals(0, calendarRepo.getEventsCalls)
        assertEquals(listOf("1", "2", "3", "4", "5", "6"), result.agendaEvents.map { it.id })
        assertTrue(result.agendaEvents.all { it.epochDay == today.toEpochDays() })
    }

    @Test
    fun demoModeOnWhenNotDetermined() = runTest {
        permission.state = CalendarPermissionHandler.State.NotDetermined
        assertEquals(6, run().agendaEvents.size)
        assertEquals(0, calendarRepo.getEventsCalls)
    }

    @Test
    fun hiddenEventsApplyToDemoEvents() = runTest {
        permission.state = CalendarPermissionHandler.State.Denied
        hiddenRepo.hidden = listOf(HiddenEventEntity("1", today.toEpochDays()))
        val events = run().agendaEvents
        assertEquals(listOf(true, false, false, false, false, false), events.map { it.isHidden })
    }

    @Test
    fun demoEventsAppearOnTodayInDayList() = runTest {
        permission.state = CalendarPermissionHandler.State.Denied
        val list = run().dayListViewTypes
        val header = list.indexOfFirst { it is DayListViewType.Header && it.agendaDay.epochDay == today.toEpochDays() }
        val divider = list.indexOfFirst { it is DayListViewType.Divider && it.epochDay == today.toEpochDays() }
        assertEquals(6, list.subList(header, divider).count { it is DayListViewType.Event })
    }
}
