package repository

import data.local.AppColorEntity
import data.local.AppThemeModeEntity
import data.local.SettingsEntity
import data.local.SettingsLocalDataSource
import domain.model.AppColor
import domain.model.AppTheme
import domain.model.AppThemeMode
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

                val appTheme = AppTheme(
                    appColor = defaultSettings.appColor, appThemeMode = defaultSettings.appThemeMode
                )

                return@map appTheme
            }
            val entity = Json.decodeFromString<SettingsEntity>(it)

            val settings = entity.asDomainModel()

            return@map AppTheme(
                appColor = settings.appColor,
                appThemeMode = settings.appThemeMode
            )
        }
    }

    private fun Settings.asEntity() = SettingsEntity(
        appTheme = appThemeMode.asEntity(),
        appColor = appColor.asEntity(),
        privateNotifications = privateNotifications,
        notificationTimeInMinutes = notificationTimeInMinutes
    )

    private fun SettingsEntity.asDomainModel() = Settings(
        appThemeMode = appTheme.asDomainModel(),
        appColor = appColor.asDomainModel(),
        privateNotifications = privateNotifications,
        notificationTimeInMinutes = notificationTimeInMinutes
    )

    private fun AppThemeModeEntity.asDomainModel() = when (this) {
        AppThemeModeEntity.FollowSystem -> AppThemeMode.FollowSystem
        AppThemeModeEntity.DefaultLight -> AppThemeMode.DefaultLight
        AppThemeModeEntity.DefaultDark -> AppThemeMode.DefaultDark
    }

    private fun AppThemeMode.asEntity() = when (this) {
        AppThemeMode.FollowSystem -> AppThemeModeEntity.FollowSystem
        AppThemeMode.DefaultLight -> AppThemeModeEntity.DefaultLight
        AppThemeMode.DefaultDark -> AppThemeModeEntity.DefaultDark
    }

    private fun AppColor.asEntity() = when (this) {
        AppColor.Blue -> AppColorEntity.Blue
        AppColor.Red -> AppColorEntity.Red
        AppColor.Mint -> AppColorEntity.Mint
        AppColor.Rosa -> AppColorEntity.Rosa
        AppColor.Orange -> AppColorEntity.Orange
        AppColor.Lila -> AppColorEntity.Lila
    }

    private fun AppColorEntity.asDomainModel() = when (this) {
        AppColorEntity.Red -> AppColor.Red
        AppColorEntity.Mint -> AppColor.Mint
        AppColorEntity.Rosa -> AppColor.Rosa
        AppColorEntity.Orange -> AppColor.Orange
        AppColorEntity.Lila -> AppColor.Lila
        AppColorEntity.Blue -> AppColor.Blue
    }
}