import androidx.compose.ui.window.ComposeUIViewController
import di.initKoin
import platform.UIKit.UIScreen
import platform.UIKit.UIUserInterfaceStyle
import presentation.screens.main.App

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) {
    val isDarkTheme =
        UIScreen.mainScreen.traitCollection.userInterfaceStyle ==
                UIUserInterfaceStyle.UIUserInterfaceStyleDark
    App(isSystemInDarkMode = isDarkTheme)
}