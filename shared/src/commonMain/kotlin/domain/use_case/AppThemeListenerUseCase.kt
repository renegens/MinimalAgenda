package domain.use_case

import domain.model.AppTheme
import kotlinx.coroutines.flow.Flow
import repository.SettingsRepository

class AppThemeListenerUseCase(
    private val settingsRepository: SettingsRepository
) {

    suspend operator fun invoke(): Flow<AppTheme> = settingsRepository.appTheme()
}