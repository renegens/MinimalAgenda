package repository

import data.local.AppThemeEntity
import data.local.SettingsEntity
import data.local.SettingsLocalDataSource
import domain.model.AppTheme
import domain.model.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SettingsRepositoryImpl(private val settingsLocalDataSource: SettingsLocalDataSource) :
    SettingsRepository {

    override suspend fun getSettings(): Settings {
        val json = settingsLocalDataSource.getSettings()
        if (json.isEmpty()) {
            val defaultSettings = Settings()
            saveSettings(defaultSettings)
            return defaultSettings
        }

        val entity = Json.decodeFromString<SettingsEntity>(json)

        return entity.asDomainModel()
    }

    override suspend fun saveSettings(settings: Settings) {
        val objectToSave = Json.encodeToString(settings.asEntity())
        settingsLocalDataSource.saveSettings(objectToSave)
    }

    override suspend fun appTheme(): Flow<AppTheme> {
        return settingsLocalDataSource.settings().map {
            if (it.isEmpty()) {
                val defaultSettings = Settings()
                saveSettings(defaultSettings)
                return@map defaultSettings.appTheme
            }
            val entity = Json.decodeFromString<SettingsEntity>(it)
            return@map entity.asDomainModel().appTheme
        }
    }

    private fun Settings.asEntity() = SettingsEntity(
        appTheme = appTheme.asEntity(),
        privateNotifications = privateNotifications,
        notificationTimeInMinutes = notificationTimeInMinutes
    )

    private fun SettingsEntity.asDomainModel() = Settings(
        appTheme = appTheme.asDomainModel(),
        privateNotifications = privateNotifications,
        notificationTimeInMinutes = notificationTimeInMinutes
    )

    private fun AppThemeEntity.asDomainModel() = when (this) {
        AppThemeEntity.FollowSystem -> AppTheme.FollowSystem
        AppThemeEntity.DefaultLight -> AppTheme.DefaultLight
        AppThemeEntity.DefaultDark -> AppTheme.DefaultDark
    }

    private fun AppTheme.asEntity() = when (this) {
        AppTheme.FollowSystem -> AppThemeEntity.FollowSystem
        AppTheme.DefaultLight -> AppThemeEntity.DefaultLight
        AppTheme.DefaultDark -> AppThemeEntity.DefaultDark
    }
}