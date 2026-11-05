package domain.use_case

import domain.model.Settings
import repository.SettingsRepository

class GetSettingsUseCase(
    private val settingsRepository: SettingsRepository
) {

    suspend operator fun invoke(): Settings = settingsRepository.getSettings()
}