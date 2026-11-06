package domain.use_case

import domain.model.AppColor
import domain.model.AppThemeMode
import domain.model.Settings
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import platform.CalendarPermissionHandler.State
import testutil.FakeHiddenEventsRepository
import testutil.FakePermissionHandler
import testutil.FakeSettingsRepository
import testutil.agendaEvent
import testutil.at
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ShouldShowDemoModeUseCaseTest {
    @Test
    fun fullAccessDisablesDemoMode() {
        assertFalse(ShouldShowDemoModeUseCase(FakePermissionHandler(State.FullAccess))())
    }

    @Test
    fun deniedEnablesDemoMode() {
        assertTrue(ShouldShowDemoModeUseCase(FakePermissionHandler(State.Denied))())
    }

    @Test
    fun notDeterminedEnablesDemoMode() {
        assertTrue(ShouldShowDemoModeUseCase(FakePermissionHandler(State.NotDetermined))())
    }

    @Test
    fun followsPermissionChanges() {
        val handler = FakePermissionHandler(State.Denied)
        val useCase = ShouldShowDemoModeUseCase(handler)
        assertTrue(useCase())
        handler.state = State.FullAccess
        assertFalse(useCase())
    }
}

class ToggleEventVisibilityUseCaseTest {
    private val date = LocalDate(2024, 6, 10)

    @Test
    fun delegatesToRepositoryWithOriginalEvent() = runTest {
        val repo = FakeHiddenEventsRepository()
        val event = agendaEvent(start = at(date, 9))
        ToggleEventVisibilityUseCase(repo)(event)
        assertEquals(1, repo.toggled.size)
        assertSame(event, repo.toggled.single())
    }

    @Test
    fun returnsEventWithFlippedVisibility() = runTest {
        val useCase = ToggleEventVisibilityUseCase(FakeHiddenEventsRepository())
        val visible = agendaEvent(start = at(date, 9), isHidden = false)
        val hidden = agendaEvent(start = at(date, 9), isHidden = true)
        assertTrue((useCase(visible) as ToggleEventVisibilityUseCase.State.Success).agendaEvent.isHidden)
        assertFalse((useCase(hidden) as ToggleEventVisibilityUseCase.State.Success).agendaEvent.isHidden)
        // the input is not mutated
        assertFalse(visible.isHidden)
    }
}

class SettingsUseCasesTest {
    @Test
    fun getSettingsReturnsRepositoryValue() = runTest {
        val settings = Settings(appColor = AppColor.Blue, notificationTimeInMinutes = 15)
        assertSame(settings, GetSettingsUseCase(FakeSettingsRepository(settings))())
    }

    @Test
    fun saveSettingsPersistsThroughRepository() = runTest {
        val repo = FakeSettingsRepository()
        val settings = Settings(
            appThemeMode = AppThemeMode.DefaultDark,
            privateNotifications = true
        )
        SaveSettingsUseCase(repo)(settings)
        assertSame(settings, repo.saved)
        assertEquals(settings, GetSettingsUseCase(repo)())
    }

    @Test
    fun settingsDefaults() {
        val s = Settings()
        assertEquals(AppThemeMode.FollowSystem, s.appThemeMode)
        assertEquals(AppColor.Orange, s.appColor)
        assertFalse(s.privateNotifications)
        assertEquals(5, s.notificationTimeInMinutes)
    }
}
