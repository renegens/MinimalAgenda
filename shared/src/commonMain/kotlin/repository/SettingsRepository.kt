package repository

import domain.model.AppTheme
import domain.model.Settings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    suspend fun getSettings(): Settings
    suspend fun saveSettings(settings: Settings)
    suspend fun appTheme(): Flow<AppTheme>
}