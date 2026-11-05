package presentation.screens.add_event

import data.calendar.CalendarEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import presentation.screens.add_event.AddEventContract.Confirmation
import presentation.screens.add_event.AddEventContract.Event
import testutil.FakeCalendarRepository
import testutil.agendaEvent
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AddEventViewModelTest {

    @BeforeTest
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private val date = LocalDate(2026, 3, 10)
    private fun cal(id: String, canWrite: Boolean = true, primary: Boolean = false) =
        CalendarEntity(id, "Cal $id", "acc", "type", canWrite, primary)

    private fun editVm(
        repo: FakeCalendarRepository,
        allDay: Boolean = false,
        recurring: Boolean = false,
        endHour: Int = 10,
    ): AddEventViewModel {
        val base = agendaEvent(
            id = "7/1000",
            title = "Old",
            start = LocalDateTime(2026, 3, 10, if (allDay) 0 else 9, 0),
            end = LocalDateTime(2026, 3, 10, if (allDay) 23 else endHour, if (allDay) 59 else 30),
            allDay = allDay,
        ).copy(location = "Room", isRecurring = recurring)
        return AddEventViewModel(repo, EventEditorRequest.Edit(base))
    }

    @Test
    fun addRequiresTitle() {
        val vm = AddEventViewModel(FakeCalendarRepository())
        assertFalse(vm.uiState.value.isSaveEnabled())
        vm.setEvent(Event.OnTitleEntered("Lunch"))
        assertTrue(vm.uiState.value.isSaveEnabled())
    }

    @Test
    fun invalidDurationBlocksSaveUnlessAllDay() {
        val vm = AddEventViewModel(FakeCalendarRepository())
        vm.setEvent(Event.OnTitleEntered("Lunch"))
        vm.setEvent(Event.OnDurationEntered("0"))
        assertFalse(vm.uiState.value.isSaveEnabled())
        vm.setEvent(Event.ToggleAllDay(true))
        assertTrue(vm.uiState.value.isSaveEnabled())
    }

    @Test
    fun addUsesDefaultPrimaryCalendarAndShowsPickerOnlyWithSeveral() = runTest {
        val repo = FakeCalendarRepository()
        repo.calendars = listOf(cal("1"), cal("2", primary = true), cal("3", canWrite = false))
        val vm = AddEventViewModel(repo)
        assertEquals(listOf("1", "2"), vm.uiState.value.calendars.map { it.id })
        assertEquals("2", vm.uiState.value.calendarId)

        vm.setEvent(Event.OnCalendarSelected("1"))
        vm.setEvent(Event.OnTitleEntered("Lunch"))
        vm.setEvent(Event.OnStartEntered(date.toPickerMillis(), 12, 0))
        vm.setEvent(Event.OnDurationEntered("45"))
        vm.setEvent(Event.OnSave)

        val added = repo.added.single()
        assertEquals("1", added.calendarId)
        assertEquals(LocalDateTime(2026, 3, 10, 12, 0), added.start)
        assertEquals(LocalDateTime(2026, 3, 10, 12, 45), added.end)
        assertFalse(added.allDay)
    }

    @Test
    fun addAllDayWritesFirstAndLastDay() {
        val repo = FakeCalendarRepository()
        val vm = AddEventViewModel(repo)
        vm.setEvent(Event.OnTitleEntered("Holiday"))
        vm.setEvent(Event.OnStartEntered(date.toPickerMillis(), 0, 0))
        vm.setEvent(Event.ToggleAllDay(true))
        vm.setEvent(Event.OnSave)
        val added = repo.added.single()
        assertTrue(added.allDay)
        assertEquals(date, added.start.date)
        assertEquals(date, added.end.date)
    }

    @Test
    fun failedSaveKeepsEditorOpenAndShowsError() {
        val repo = FakeCalendarRepository().apply { saveResult = false }
        val vm = AddEventViewModel(repo)
        vm.setEvent(Event.OnTitleEntered("x"))
        vm.setEvent(Event.OnSave)
        assertTrue(vm.uiState.value.saveFailed)
        assertFalse(vm.uiState.value.isSaving)
    }

    @Test
    fun editIsPrefilledAndUpdates() {
        val repo = FakeCalendarRepository()
        val vm = editVm(repo)
        val s = vm.uiState.value
        assertEquals("Old", s.title)
        assertEquals("Room", s.location)
        assertEquals("90", s.durationInMinutes)
        assertEquals(9, s.startHours)
        assertEquals(date.toPickerMillis(), s.startDate)
        assertTrue(vm.uiState.value.isEditing)

        vm.setEvent(Event.OnTitleEntered("New"))
        vm.setEvent(Event.OnDurationEntered("30"))
        vm.setEvent(Event.OnSave)

        assertTrue(repo.added.isEmpty())
        val u = repo.updated.single()
        assertEquals("7/1000", u.id)
        assertEquals("New", u.title)
        assertEquals(LocalDateTime(2026, 3, 10, 9, 0), u.start)
        assertEquals(LocalDateTime(2026, 3, 10, 9, 30), u.end)
    }

    @Test
    fun editAllDayMultiDayKeepsItsLength() {
        val repo = FakeCalendarRepository()
        val event = agendaEvent(
            id = "7/1000", start = LocalDateTime(2026, 3, 10, 0, 0),
            end = LocalDateTime(2026, 3, 12, 23, 59), allDay = true,
        )
        val vm = AddEventViewModel(repo, EventEditorRequest.Edit(event))
        vm.setEvent(Event.OnTitleEntered("Trip"))
        vm.setEvent(Event.OnSave)
        val u = repo.updated.single()
        assertEquals(LocalDate(2026, 3, 10), u.start.date)
        assertEquals(LocalDate(2026, 3, 12), u.end.date)
    }

    @Test
    fun deleteNeedsConfirmation() {
        val repo = FakeCalendarRepository()
        val vm = editVm(repo)
        vm.setEvent(Event.OnDelete)
        assertEquals(Confirmation.DeleteEvent, vm.uiState.value.confirmation)
        assertTrue(repo.deleted.isEmpty())

        vm.setEvent(Event.OnDismissConfirmation)
        assertNull(vm.uiState.value.confirmation)
        assertTrue(repo.deleted.isEmpty())

        vm.setEvent(Event.OnDelete)
        vm.setEvent(Event.OnConfirm)
        assertEquals(listOf("7/1000"), repo.deleted)
    }

    @Test
    fun recurringEditOnlyChangesTextAfterConfirmation() {
        val repo = FakeCalendarRepository()
        val vm = editVm(repo, recurring = true)
        assertFalse(vm.uiState.value.timesEditable)
        vm.setEvent(Event.OnTitleEntered("Series"))
        vm.setEvent(Event.OnSave)
        assertEquals(Confirmation.ChangeSeries, vm.uiState.value.confirmation)
        assertTrue(repo.updated.isEmpty())
        vm.setEvent(Event.OnConfirm)
        assertEquals("Series", repo.updated.single().title)
    }

    @Test
    fun deleteIgnoredInAddMode() {
        val vm = AddEventViewModel(FakeCalendarRepository())
        vm.setEvent(Event.OnDelete)
        assertNull(vm.uiState.value.confirmation)
    }

    @Test
    fun timedEndCrossesMidnightWhenEditing() {
        val state = AddEventContract.State(
            startDate = date.toPickerMillis(), startHours = 23, startMinutes = 0, durationInMinutes = "120"
        )
        val (start, end) = state.toStartAndEnd(TimeZone.UTC)
        assertEquals(LocalDateTime(2026, 3, 10, 23, 0), start)
        assertEquals(LocalDateTime(2026, 3, 11, 1, 0), end)
    }
}
