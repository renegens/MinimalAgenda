package presentation.screens.settings

import androidx.lifecycle.viewModelScope
import domain.model.AppColor
import domain.model.AppThemeMode
import domain.model.Settings
import domain.use_case.GetSettingsUseCase
import domain.use_case.SaveSettingsUseCase
import extensions.guardPremium
import kotlinx.coroutines.launch
import platform.Browser
import platform.CalendarPermissionHandler
import presentation.base.BaseViewModel
import presentation.screens.settings.SettingsContract.Effect
import presentation.screens.settings.SettingsContract.Event
import presentation.screens.settings.SettingsContract.State

class SettingsViewModel(
    private val browser: Browser,
    private val saveSettingsUseCase: SaveSettingsUseCase,
    private val getSettingsUseCase: GetSettingsUseCase,
    private val permissionHandler: CalendarPermissionHandler
) : BaseViewModel<Event, State, Effect>() {

    init {
        setEvent(Event.Load)
        // Load initial calendar permission state
        val permissionState = permissionHandler.getPermissionState()
        setState { copy(calendarPermission = permissionState) }
    }

    override fun createInitialState() = State()

    override fun handleEvent(event: Event) {
        when (event) {
            Event.OnDismiss -> setEffect { Effect.Dismiss }
            is Event.OnModeSelected -> setAppThemeMode(event.appThemeMode)
            is Event.OnColorSelected -> guardPremium(
                onSubscribed = { setAppColor(event.appColor) }, onPaywall = ::showPaywall
            )

            is Event.OnTogglePrivateNotifications -> guardPremium(
                onSubscribed = { setPrivateNotification(event.checked) }, onPaywall = ::showPaywall
            )

            is Event.OnToggleHideBottomNavigation -> setHideBottomNavigation(event.checked)

            is Event.OnNotificationTimeEntered -> guardPremium(
                onSubscribed = { setNotificationTime(event.time) }, onPaywall = ::showPaywall
            )

            Event.OnSentFeedback -> TODO()
            Event.Load -> {
                viewModelScope.launch {
                    val settings = getSettingsUseCase()
                    val permissionState = permissionHandler.getPermissionState()
                    setState {
                        copy(
                            isLoading = false,
                            appColor = settings.appColor,
                            appThemeMode = settings.appThemeMode,
                            privateNotifications = settings.privateNotifications,
                            notificationTime = settings.notificationTimeInMinutes,
                            hideBottomNavigation = settings.hideBottomNavigation,
                            calendarPermission = permissionState
                        )
                    }
                }
            }
            Event.OnRequestCalendarPermission -> {
                viewModelScope.launch {
                    val newState = permissionHandler.requestCalendarPermission()
                    setState { copy(calendarPermission = newState) }
                }
            }
            Event.OnOpenDeviceSettings -> {
                setEffect { Effect.OpenDeviceSettings }
            }
            Event.OnOpenPrivacyPolicy -> {
                browser.openUrl("https://giant-solutions.eu/calendar/privacy/index.html")
            }
            Event.OnOpenTermsOfService -> {
                browser.openUrl("https://giant-solutions.eu/calendar/terms/index.html")
            }
        }
    }
    
    fun refreshPermissionState() {
        val permissionState = permissionHandler.getPermissionState()
        setState { copy(calendarPermission = permissionState) }
    }

    private fun setPrivateNotification(isChecked: Boolean) {
        viewModelScope.launch {
            val currentSettings = uiState.value.toSettings()
            saveSettingsUseCase.invoke(currentSettings.copy(privateNotifications = isChecked))
            setState { copy(privateNotifications = isChecked) }
        }
    }

    private fun setAppThemeMode(appThemeMode: AppThemeMode) {
        viewModelScope.launch {
            val currentSettings = uiState.value.toSettings()
            saveSettingsUseCase.invoke(currentSettings.copy(appThemeMode = appThemeMode))
            setState { copy(appThemeMode = appThemeMode) }
        }
    }

    private fun setAppColor(appColor: AppColor) {
        viewModelScope.launch {
            val currentSettings = uiState.value.toSettings()
            saveSettingsUseCase.invoke(currentSettings.copy(appColor = appColor))
            setState { copy(appColor = appColor) }
        }
    }

    private fun setNotificationTime(time: Int) {
        viewModelScope.launch {
            val currentSettings = uiState.value.toSettings()
            saveSettingsUseCase.invoke(currentSettings.copy(notificationTimeInMinutes = time))
            setState { copy(notificationTime = time) }
        }
    }

    private fun setHideBottomNavigation(isChecked: Boolean) {
        viewModelScope.launch {
            val currentSettings = uiState.value.toSettings()
            saveSettingsUseCase.invoke(currentSettings.copy(hideBottomNavigation = isChecked))
            setState { copy(hideBottomNavigation = isChecked) }
        }
    }

    private fun showPaywall() {
        setEffect { Effect.NotEligible }
    }

    private fun State.toSettings(): Settings {
        return Settings(
            appThemeMode = appThemeMode,
            appColor = appColor,
            privateNotifications = privateNotifications,
            notificationTimeInMinutes = notificationTime,
            hideBottomNavigation = hideBottomNavigation
        )
    }
}