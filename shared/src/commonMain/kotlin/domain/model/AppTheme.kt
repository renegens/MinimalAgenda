package domain.model

data class AppTheme(
    val appColor: AppColor,
    val appThemeMode: AppThemeMode
) {
    companion object {
        val Default = AppTheme(
            appColor = AppColor.Orange,
            appThemeMode = AppThemeMode.FollowSystem
        )
    }
}