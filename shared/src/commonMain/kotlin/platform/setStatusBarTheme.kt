package platform

import androidx.compose.runtime.Composable
import domain.model.AppThemeMode

/** Matches the system bar icons to the in-app theme, which can differ from the system theme. */
@Composable
expect fun setStatusBarTheme(appThemeMode: AppThemeMode)
