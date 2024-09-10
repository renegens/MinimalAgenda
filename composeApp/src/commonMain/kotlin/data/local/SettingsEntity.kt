package data.local

import kotlinx.serialization.Serializable

@Serializable
data class SettingsEntity(
    val appTheme: AppThemeEntity,
    val privateNotifications: Boolean,
    val notificationTimeInMinutes: Int
)