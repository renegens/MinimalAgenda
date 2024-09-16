package domain.model

data class Settings(
    val appThemeMode: AppThemeMode = AppThemeMode.FollowSystem,
    val appColor: AppColor = AppColor.Red,
    val privateNotifications: Boolean = false,
    val notificationTimeInMinutes: Int = 5,
)