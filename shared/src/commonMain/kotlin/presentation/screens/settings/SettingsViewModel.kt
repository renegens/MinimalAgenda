package presentation.screens.settings

import androidx.lifecycle.viewModelScope
import domain.model.AppColor
import domain.model.AppThemeMode
import domain.model.Settings
import domain.use_case.GetSettingsUseCase
import domain.use_case.SaveSettingsUseCase
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okio.IOException
import data.DONATE_URL
import data.SUPPORT_ISSUES_URL
import platform.AppConfig
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
    private val permissionHandler: CalendarPermissionHandler,
    appConfig: AppConfig
) : BaseViewModel<Event, State, Effect>() {

    private val settingsWriteMutex = Mutex()
    private val donateEnabled = appConfig.showDonate
    private val focusModeSetupEnabled = appConfig.showFocusModeSetup

    init {
        setEvent(Event.Load)
        // Load initial calendar permission state
        val permissionState = permissionHandler.getPermissionState()
        setState { copy(calendarPermission = permissionState, showDonate = donateEnabled, showFocusModeSetup = focusModeSetupEnabled) }
    }

    override fun createInitialState() = State()

    override fun handleEvent(event: Event) {
        when (event) {
            Event.OnDismiss -> setEffect { Effect.Dismiss }
            is Event.OnModeSelected -> setAppThemeMode(event.appThemeMode)
            is Event.OnColorSelected -> setAppColor(event.appColor)

            is Event.OnTogglePrivateNotifications -> setPrivateNotification(event.checked)

            is Event.OnNotificationTimeEntered -> setNotificationTime(event.time)

            Event.OnContactSupport -> browser.openUrl(SUPPORT_ISSUES_URL)
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
            Event.OnOpenDonate -> {
                if (donateEnabled) browser.openUrl(DONATE_URL)
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

    private fun setPrivateNotification(isChecked: Boolean) = updateSettings { copy(privateNotifications = isChecked) }
    private fun setAppThemeMode(mode: AppThemeMode) = updateSettings { copy(appThemeMode = mode) }
    private fun setAppColor(color: AppColor) = updateSettings { copy(appColor = color) }
    private fun setNotificationTime(time: Int) = updateSettings { copy(notificationTimeInMinutes = time) }

    /** Queue complete snapshots so two quick changes cannot overwrite one another while a write suspends. */
    private fun updateSettings(change: Settings.() -> Settings) {
        viewModelScope.launch {
            settingsWriteMutex.withLock {
                val next = uiState.value.toSettings().change()
                try {
                    saveSettingsUseCase(next)
                    setState {
                        copy(
                            appThemeMode = next.appThemeMode,
                            appColor = next.appColor,
                            privateNotifications = next.privateNotifications,
                            notificationTime = next.notificationTimeInMinutes,
                            saveFailed = false,
                        )
                    }
                } catch (_: IOException) {
                    setState { copy(saveFailed = true) }
                }
            }
        }
    }

    private fun State.toSettings(): Settings {
        return Settings(
            appThemeMode = appThemeMode,
            appColor = appColor,
            privateNotifications = privateNotifications,
            notificationTimeInMinutes = notificationTime,
        )
    }
}