package platform

import domain.model.AppTheme

import platform.UIKit.*

actual fun setStatusBarTheme(appTheme: AppTheme) {
    val isDarkMode = appTheme == AppTheme.DefaultDark

    if (isDarkMode) {
        UIApplication.sharedApplication.setStatusBarStyle(UIStatusBarStyleLightContent, animated = false)
    } else {
        UIApplication.sharedApplication.setStatusBarStyle(UIStatusBarStyleDefault, animated = false)
    }
}