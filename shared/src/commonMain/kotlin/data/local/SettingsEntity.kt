package data.local

import kotlinx.serialization.Serializable

@Serializable
data class SettingsEntity(
    val appTheme: AppThemeModeEntity,
    val appColor: AppColorEntity,
    val privateNotifications: Boolean,
    val notificationTimeInMinutes: Int,
)
