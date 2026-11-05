package presentation.screens.add_event

import data.calendar.CalendarMath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import presentation.screens.add_event.AddEventContract.Event
import testutil.FakeCalendarRepository
import testutil.agendaEvent
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class EventEndTimeTest {
    private val date = LocalDate(2026, 3, 10)

    @BeforeTest
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun vm(repo: FakeCalendarRepository = FakeCalendarRepository(), zone: TimeZone = TimeZone.UTC) =
        AddEventViewModel(repo, EventEditorRequest.Add(date), zone).apply {
            setEvent(Event.OnTitleEntered("Trip"))
            setEvent(Event.OnStartEntered(date.toPickerMillis(), 9, 0))
        }

    @Test
    fun eachChipSetsTheExactEndAndIsSelected() {
        val vm = vm()
        for (minutes in durationQuickPicks) {
            vm.setEvent(Event.OnDurationSelected(minutes))
            val state = vm.uiState.value
            val (start, end) = state.toStartAndEnd(TimeZone.UTC)
            assertEquals(minutes, state.selectedDurationMinutes)
            assertEquals(minutes * 60L, (end.toInstant(TimeZone.UTC) - start.toInstant(TimeZone.UTC)).inWholeSeconds)
        }
    }

    @Test
    fun directEndSelectsMatchingChipOrNone() {
        val vm = vm()
        vm.setEvent(Event.OnEndEntered(date.toPickerMillis(), 10, 30))
        assertEquals(90, vm.uiState.value.selectedDurationMinutes)
        vm.setEvent(Event.OnEndEntered(date.toPickerMillis(), 9, 45))
        assertNull(vm.uiState.value.selectedDurationMinutes)
        assertEquals(2_700L, vm.uiState.value.durationInSeconds)
    }

    @Test
    fun changingStartPreservesDurationAcrossMidnight() {
        val vm = vm()
        vm.setEvent(Event.OnDurationSelected(120))
        vm.setEvent(Event.OnStartEntered(date.toPickerMillis(), 23, 30))
        val state = vm.uiState.value
        assertEquals(7_200L, state.durationInSeconds)
        assertEquals(LocalDateTime(2026, 3, 11, 1, 30), state.toStartAndEnd(TimeZone.UTC).second)
        assertTrue(state.isSaveEnabled())
    }

    @Test
    fun multiDayEndLongerThanTwoHoursReachesTheRepository() {
        val repo = FakeCalendarRepository()
        val vm = vm(repo)
        vm.setEvent(Event.OnEndEntered(LocalDate(2026, 3, 12).toPickerMillis(), 18, 45))
        assertNull(vm.uiState.value.selectedDurationMinutes)
        vm.setEvent(Event.OnSave)
        val saved = repo.added.single()
        assertEquals(LocalDateTime(2026, 3, 10, 9, 0), saved.start)
        assertEquals(LocalDateTime(2026, 3, 12, 18, 45), saved.end)
        val write = CalendarMath.toWriteTimes(saved.start, saved.end, false, TimeZone.UTC, saved.timedRange)
        assertEquals(207_900_000L, write.dtEndMillis - write.dtStartMillis)
    }

    @Test
    fun equalOrEarlierTimedEndCannotSaveAndAChipRecovers() {
        val repo = FakeCalendarRepository()
        val vm = vm(repo)
        for (hour in listOf(9, 8)) {
            vm.setEvent(Event.OnEndEntered(date.toPickerMillis(), hour, 0))
            assertFalse(vm.uiState.value.isSaveEnabled())
            assertNotNull(vm.uiState.value.timeError)
            vm.setEvent(Event.OnSave)
            assertTrue(repo.added.isEmpty())
        }
        vm.setEvent(Event.OnDurationSelected(60))
        assertTrue(vm.uiState.value.isSaveEnabled())
        assertNull(vm.uiState.value.timeError)
    }

    @Test
    fun allDayEndsAreInclusiveAndMovingStartKeepsTheSpan() {
        val repo = FakeCalendarRepository()
        val vm = vm(repo)
        vm.setEvent(Event.ToggleAllDay(true))
        vm.setEvent(Event.OnEndEntered(LocalDate(2026, 3, 12).toPickerMillis(), 23, 59))
        assertEquals(3, vm.uiState.value.allDayDays)
        vm.setEvent(Event.OnStartEntered(LocalDate(2026, 3, 11).toPickerMillis(), 9, 0))
        assertEquals(LocalDate(2026, 3, 13), vm.uiState.value.toStartAndEnd(TimeZone.UTC).second.date)
        vm.setEvent(Event.OnSave)
        val saved = repo.added.single()
        assertTrue(saved.allDay)
        val write = CalendarMath.toWriteTimes(saved.start, saved.end, true, TimeZone.UTC)
        assertEquals(LocalDate(2026, 3, 14).toPickerMillis(), write.dtEndMillis)
    }

    @Test
    fun allDaySameDateIsValidButAnEarlierDateIsNot() {
        val vm = vm()
        vm.setEvent(Event.ToggleAllDay(true))
        vm.setEvent(Event.OnEndEntered(LocalDate(2026, 3, 9).toPickerMillis(), 23, 59))
        assertFalse(vm.uiState.value.isSaveEnabled())
        assertNotNull(vm.uiState.value.timeError)
        vm.setEvent(Event.OnEndEntered(date.toPickerMillis(), 23, 59))
        assertTrue(vm.uiState.value.isSaveEnabled())
        assertEquals(1, vm.uiState.value.allDayDays)
    }

    @Test
    fun existingMultiDayEventKeepsItsRealEndIncludingSeconds() {
        val repo = FakeCalendarRepository()
        val event = agendaEvent(
            start = LocalDateTime(2026, 3, 10, 9, 0, 15),
            end = LocalDateTime(2026, 3, 12, 18, 45, 40),
        )
        val vm = AddEventViewModel(repo, EventEditorRequest.Edit(event), TimeZone.UTC)
        assertEquals(event.endLocalDateTime, vm.uiState.value.toStartAndEnd(TimeZone.UTC).second)
        vm.setEvent(Event.OnSave)
        val saved = repo.updated.single()
        assertEquals(event.startLocalDateTime, saved.start)
        assertEquals(event.endLocalDateTime, saved.end)
    }

    @Test
    fun elapsedDurationSurvivesSpringForwardAndMovingToAnOrdinaryDay() {
        val zone = TimeZone.of("Europe/Athens")
        val vm = vm(zone = zone)
        val transition = LocalDate(2026, 3, 29)
        vm.setEvent(Event.OnStartEntered(transition.toPickerMillis(), 2, 30))
        vm.setEvent(Event.OnDurationSelected(120))
        assertEquals(LocalDateTime(2026, 3, 29, 5, 30), vm.uiState.value.toStartAndEnd(zone).second)
        vm.setEvent(Event.OnStartEntered(LocalDate(2026, 3, 30).toPickerMillis(), 2, 30))
        assertEquals(LocalDateTime(2026, 3, 30, 4, 30), vm.uiState.value.toStartAndEnd(zone).second)
        assertEquals(7_200L, vm.uiState.value.durationInSeconds)
    }

    @Test
    fun fallBackChipWritesTheSecondOccurrenceOfTheRepeatedHour() {
        val zone = TimeZone.of("Europe/Athens")
        val repo = FakeCalendarRepository()
        val vm = vm(repo, zone)
        val transition = LocalDate(2026, 10, 25)
        vm.setEvent(Event.OnStartEntered(transition.toPickerMillis(), 2, 30))
        vm.setEvent(Event.OnDurationSelected(120))
        // Local end 03:30 occurs twice. The elapsed duration chooses the later one.
        assertEquals(LocalDateTime(2026, 10, 25, 3, 30), vm.uiState.value.toStartAndEnd(zone).second)
        vm.setEvent(Event.OnEndEntered(transition.toPickerMillis(), 3, 30)) // unchanged displayed value
        vm.setEvent(Event.OnSave)
        val saved = repo.added.single()
        val write = CalendarMath.toWriteTimes(saved.start, saved.end, false, zone, saved.timedRange)
        assertEquals(7_200_000L, write.dtEndMillis - write.dtStartMillis)
        assertEquals(Instant.parse("2026-10-25T01:30:00Z").toEpochMilliseconds(), write.dtEndMillis)
    }

    @Test
    fun existingStartInTheSecondRepeatedHourIsPreservedWhenSaving() {
        val zone = TimeZone.of("Europe/Athens")
        val repo = FakeCalendarRepository()
        val event = agendaEvent(
            start = LocalDateTime(2026, 10, 25, 3, 30),
            end = LocalDateTime(2026, 10, 25, 4, 30),
        ).copy(
            startTimeInUTC = Instant.parse("2026-10-25T01:30:00Z").epochSeconds,
            endTimeInUTC = Instant.parse("2026-10-25T02:30:00Z").epochSeconds,
        )
        val vm = AddEventViewModel(repo, EventEditorRequest.Edit(event), zone)
        vm.setEvent(Event.OnSave)
        val range = repo.updated.single().timedRange!!
        assertEquals(event.startTimeInUTC, range.start.epochSeconds)
        assertEquals(event.endTimeInUTC, range.end.epochSeconds)
    }
}
