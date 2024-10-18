package presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import domain.model.AppColor
import domain.model.AppThemeMode
import platform.setStatusBarTheme
import presentation.components.PaperBackground

@Composable
fun AppTheme(
    appThemeMode: AppThemeMode,
    appColor: AppColor,
    content: @Composable () -> Unit
) {

    val darkColorScheme = getDarkColorScheme(primaryColor = appColor.color)
    val lightColorScheme = getLightColorScheme(primaryColor = appColor.color)

    val colorScheme = when (appThemeMode) {
        AppThemeMode.DefaultDark -> darkColorScheme
        AppThemeMode.DefaultLight -> lightColorScheme
        AppThemeMode.FollowSystem -> if (isSystemInDarkTheme()) darkColorScheme else lightColorScheme
    }

    setStatusBarTheme(appThemeMode)
    PaperBackground(appThemeMode) {
        MaterialTheme(
            colorScheme = colorScheme, content = content
        )
    }
}