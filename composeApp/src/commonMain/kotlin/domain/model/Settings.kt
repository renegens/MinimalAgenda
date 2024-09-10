package domain.model

data class Settings(
    val appTheme: AppTheme = AppTheme.FollowSystem,
    val privateNotifications: Boolean = true,
    val notificationTimeInMinutes: Int = 5
)