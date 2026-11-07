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
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

class SettingsRepositoryImpl(private val settingsLocalDataSource: SettingsLocalDataSource) :
    SettingsRepository {

    override suspend fun getSettings(): Settings {
        val json = settingsLocalDataSource.getSettings()
        return SettingsJson.decode(json)
    }

    override suspend fun saveSettings(settings: Settings) {
        settingsLocalDataSource.saveSettings(SettingsJson.encode(settings))
    }

    override suspend fun appTheme(): Flow<AppTheme> = settingsLocalDataSource.settings()
        .appThemeValues()
}

/** Both persisted formats share these remaining fields; removed fields are ignored on read. */
internal object SettingsJson {
    private val json = Json { ignoreUnknownKeys = true }

    fun decode(value: String): Settings = try {
        if (value.isBlank()) Settings() else json.decodeFromString<SettingsEntity>(value).asDomainModel()
    } catch (_: SerializationException) {
        Settings()
    }

    fun encode(settings: Settings): String = json.encodeToString(settings.asEntity())

    private fun Settings.asEntity() = SettingsEntity(
        appTheme = appThemeMode.asEntity(),
        appColor = appColor.asEntity(),
        privateNotifications = privateNotifications,
        notificationTimeInMinutes = notificationTimeInMinutes,
    )

    private fun SettingsEntity.asDomainModel() = Settings(
        appThemeMode = appTheme.asDomainModel(),
        appColor = appColor.asDomainModel(),
        privateNotifications = privateNotifications,
        notificationTimeInMinutes = notificationTimeInMinutes,
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

/** Shared transformation for the app's live theme observer, including legacy settings JSON. */
internal fun Flow<String>.appThemeValues(): Flow<AppTheme> = map { value ->
    val settings = SettingsJson.decode(value)
    AppTheme(appColor = settings.appColor, appThemeMode = settings.appThemeMode)
}
