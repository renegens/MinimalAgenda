package domain.use_case

import domain.model.Settings
import repository.SettingsRepository

class SaveSettingsUseCase(
    private val settingsRepository: SettingsRepository
) {

    suspend operator fun invoke(settings: Settings)  {
        settingsRepository.saveSettings(settings)
    }
}