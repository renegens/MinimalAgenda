package presentation.screens.settings

import androidx.lifecycle.viewModelScope
import domain.model.Settings
import domain.use_case.GetSettingsUseCase
import domain.use_case.SaveSettingsUseCase
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
            is Event.OnModeSelected -> {
                viewModelScope.launch {
                    val currentSettings = uiState.value.toSettings()
                    saveSettingsUseCase.invoke(currentSettings.copy(appTheme = event.appTheme))
                    setState { copy(appTheme = event.appTheme) }
                }
            }

            is Event.OnTogglePrivateNotifications -> {
                viewModelScope.launch {
                    val currentSettings = uiState.value.toSettings()
                    saveSettingsUseCase.invoke(currentSettings.copy(privateNotifications = event.checked))
                    setState { copy(privateNotifications = event.checked) }
                }
            }

            is Event.OnNotificationTimeEntered -> {
                viewModelScope.launch {
                    val currentSettings = uiState.value.toSettings()
                    saveSettingsUseCase.invoke(currentSettings.copy(notificationTimeInMinutes = event.time))
                    setState { copy(notificationTime = event.time) }
                }
            }

            Event.OnSentFeedback -> TODO()
            Event.Load -> {
                viewModelScope.launch {
                    val settings = getSettingsUseCase()
                    setState {
                        copy(
                            isLoading = false,
                            appTheme = settings.appTheme,
                            privateNotifications = settings.privateNotifications,
                            notificationTime = settings.notificationTimeInMinutes
                        )
                    }
                }
            }
        }
    }

    private fun State.toSettings(): Settings {
        return Settings(
            appTheme = appTheme,
            privateNotifications = privateNotifications,
            notificationTimeInMinutes = notificationTime
        )
    }
}