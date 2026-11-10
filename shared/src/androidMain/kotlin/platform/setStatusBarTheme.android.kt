package platform

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import domain.model.AppThemeMode

/**
 * `enableEdgeToEdge()` picks the bar icon colors from the system theme. When the user forces the app into
 * light or dark mode, apply the app theme instead so the icons stay readable on the paper background.
 */
@Composable
actual fun setStatusBarTheme(appThemeMode: AppThemeMode) {
    val view = LocalView.current
    if (view.isInEditMode) return
    val darkTheme = when (appThemeMode) {
        AppThemeMode.DefaultDark -> true
        AppThemeMode.DefaultLight -> false
        AppThemeMode.FollowSystem -> isSystemInDarkTheme()
    }
    SideEffect {
        val window = view.context.findActivity()?.window ?: return@SideEffect
        WindowCompat.getInsetsController(window, view).apply {
            isAppearanceLightStatusBars = !darkTheme
            isAppearanceLightNavigationBars = !darkTheme
        }
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
