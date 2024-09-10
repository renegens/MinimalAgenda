package presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import domain.model.AppTheme
import platform.setStatusBarTheme
import presentation.components.PaperBackground

@Composable
fun AppTheme(
    appTheme: AppTheme,
    content: @Composable () -> Unit
) {

    val colorScheme = when (appTheme) {
        AppTheme.DefaultDark -> DarkColorScheme
        AppTheme.DefaultLight -> LightColorScheme
        AppTheme.FollowSystem -> if (isSystemInDarkTheme()) DarkColorScheme else LightColorScheme
    }

    setStatusBarTheme(appTheme)
    PaperBackground(appTheme) {
        MaterialTheme(
            colorScheme = colorScheme, typography = typography, content = content
        )
    }
}