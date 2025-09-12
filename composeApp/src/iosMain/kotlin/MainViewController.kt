import androidx.compose.ui.window.ComposeUIViewController
import di.initKoin
import domain.model.AppTheme
import presentation.screens.calendar.CalendarScreen
import presentation.screens.main.App
import presentation.theme.AppTheme

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) {
    //App()
    CalendarScreen(
        appThemeMode = AppTheme.Default.appThemeMode,
        onAddEvent = {},
        onEditEvent = {},
        notEligible = {},
        onOpenDeviceSettings = {},
        forceEventLoad = 1
    )
}