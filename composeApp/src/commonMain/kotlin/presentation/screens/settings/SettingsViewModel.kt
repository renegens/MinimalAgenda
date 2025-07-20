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
import presentation.base.BaseViewModel
import presentation.screens.settings.SettingsContract.Effect
import presentation.screens.settings.SettingsContract.Event
import presentation.screens.settings.SettingsContract.State

class SettingsViewModel(
    private val browser: Browser,
    private val saveSettingsUseCase: SaveSettingsUseCase,
    private val getSettingsUseCase: GetSettingsUseCase
) : BaseViewModel<Event, State, Effect>() {

    init {
        setEvent(Event.Load)
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

            is Event.OnNotificationTimeEntered -> guardPremium(
                onSubscribed = { setNotificationTime(event.time) }, onPaywall = ::showPaywall
            )

            Event.OnSentFeedback -> TODO()
            Event.Load -> {
                viewModelScope.launch {
                    val settings = getSettingsUseCase()
                    setState {
                        copy(
                            isLoading = false,
                            appColor = settings.appColor,
                            appThemeMode = settings.appThemeMode,
                            privateNotifications = settings.privateNotifications,
                            notificationTime = settings.notificationTimeInMinutes
                        )
                    }
                }
            }
        }
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

    private fun showPaywall() {
        setEffect { Effect.NotEligible }
    }

    private fun State.toSettings(): Settings {
        return Settings(
            appThemeMode = appThemeMode,
            appColor = appColor,
            privateNotifications = privateNotifications,
            notificationTimeInMinutes = notificationTime
        )
    }
}