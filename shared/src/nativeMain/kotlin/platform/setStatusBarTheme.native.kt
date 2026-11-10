package platform

import androidx.compose.runtime.Composable
import domain.model.AppThemeMode

import platform.UIKit.*

@Composable
actual fun setStatusBarTheme(appThemeMode: AppThemeMode) {
    val isDarkMode = appThemeMode == AppThemeMode.DefaultDark

    if (isDarkMode) {
        UIApplication.sharedApplication.setStatusBarStyle(UIStatusBarStyleLightContent, animated = false)
    } else {
        UIApplication.sharedApplication.setStatusBarStyle(UIStatusBarStyleDefault, animated = false)
    }
}