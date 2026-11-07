package presentation.screens.calendar

import domain.model.DayListViewType
import domain.use_case.*
import extensions.today
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import platform.CalendarPermissionHandler
import platform.NotificationHandler
import platform.NotificationPermissionHandler
import testutil.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class CalendarViewModelTest {
    @BeforeTest fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())
    @AfterTest fun tearDown() = Dispatchers.resetMain()

    private fun viewModel(repo: FakeCalendarRepository, permission: FakePermissionHandler): CalendarViewModel {
        val calendar = CalendarGenerator()
        val settings = GetSettingsUseCase(FakeSettingsRepository())
        val notifier = object : NotificationHandler {
            override suspend fun scheduleNotification(id: String, title: String, body: String, delayInSeconds: Int) = true
        }
        val notificationPermission = object : NotificationPermissionHandler {
            override suspend fun requestNotificationPermission() = true
        }
        return CalendarViewModel(
            GetAllEventsUseCase(repo, FakeHiddenEventsRepository(), FlatCalendarViewFactory(calendar), ShouldShowDemoModeUseCase(permission)),
            permission, calendar, ToggleEventVisibilityUseCase(FakeHiddenEventsRepository()),
            ScheduleNotificationUseCase(notifier, notificationPermission, settings),
            HasCalendarsUseCase(repo), CreateLocalCalendarUseCase(repo),
        )
    }

    @Test fun hidingUpdatesBothAgendaRowsAndMonthYearIndicators() {
        val repo = FakeCalendarRepository(listOf(eventEntity(id = "device-event", start = at(today, 9), end = at(today, 10))))
        val vm = viewModel(repo, FakePermissionHandler(CalendarPermissionHandler.State.FullAccess))
        vm.setEvent(CalendarContract.Event.InitialDataLoad)
        val event = vm.uiState.value.agendaEvents.single()
        vm.setEvent(CalendarContract.Event.ToggleEventVisibility(event))
        assertTrue(vm.uiState.value.agendaEvents.single().isHidden)
        assertTrue(vm.uiState.value.flatDaysAndEvents.filterIsInstance<DayListViewType.Event>().single().event.isHidden)
    }

    @Test fun revokingPermissionRefreshesDemoAndCalendarState() {
        val permission = FakePermissionHandler(CalendarPermissionHandler.State.FullAccess)
        val repo = FakeCalendarRepository(listOf(eventEntity(id = "device-event", start = at(today, 9), end = at(today, 10))))
        val vm = viewModel(repo, permission)
        vm.setEvent(CalendarContract.Event.InitialDataLoad)
        assertFalse(vm.uiState.value.isDemoMode)
        permission.state = CalendarPermissionHandler.State.Denied
        vm.setEvent(CalendarContract.Event.RefreshPermissions)
        assertTrue(vm.uiState.value.isDemoMode)
        assertEquals(CalendarPermissionHandler.State.Denied, vm.uiState.value.calendarPermission)
        assertFalse(vm.uiState.value.noCalendars)
        assertTrue(vm.uiState.value.agendaEvents.none { it.id == "device-event" })
    }
}
