package presentation.screens.settings

import domain.model.AppColor
import domain.model.AppThemeMode
import platform.CalendarPermissionHandler
import presentation.base.UiEffect
import presentation.base.UiEvent
import presentation.base.UiState

class SettingsContract {

    data class State(
        val isLoading: Boolean = true,
        val appThemeMode: AppThemeMode = AppThemeMode.FollowSystem,
        val appColor: AppColor = AppColor.Red,
        val privateNotifications: Boolean = true,
        val notificationTime: Int = 5,
        val hideBottomNavigation: Boolean = false,
        val calendarPermission: CalendarPermissionHandler.State = CalendarPermissionHandler.State.NotDetermined
    ) : UiState

    sealed class Effect : UiEffect {
        object Dismiss: Effect()
        object OpenDeviceSettings: Effect()
    }

    sealed class Event : UiEvent {
        object Load : Event()
        object OnDismiss : Event()
        class OnTogglePrivateNotifications(val checked: Boolean) : Event()
        class OnToggleHideBottomNavigation(val checked: Boolean) : Event()
        class OnModeSelected(val appThemeMode: AppThemeMode) : Event()
        class OnColorSelected(val appColor: AppColor) : Event()
        object OnSentFeedback : Event()
        class OnNotificationTimeEntered(val time: Int) : Event()
        object OnRequestCalendarPermission : Event()
        object OnOpenDeviceSettings : Event()
        object OnOpenPrivacyPolicy : Event()
        object OnOpenTermsOfService : Event()
    }
}