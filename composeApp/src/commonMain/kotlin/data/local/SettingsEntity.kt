package data.local

import kotlinx.serialization.Serializable

@Serializable
data class SettingsEntity(
    val appTheme: AppThemeModeEntity,
    val appColor: AppColorEntity,
    val privateNotifications: Boolean,
    val notificationTimeInMinutes: Int,
    val hideBottomNavigation: Boolean
)

@Serializable
data class OldSettingsEntity(
    val appTheme: AppThemeModeEntity,
    val appColor: AppColorEntity,
    val privateNotifications: Boolean,
    val notificationTimeInMinutes: Int
)