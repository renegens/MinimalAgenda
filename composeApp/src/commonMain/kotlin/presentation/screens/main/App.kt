package presentation.screens.main

import androidx.compose.runtime.*
import org.koin.compose.KoinContext
import presentation.components.PaperBackground
import presentation.screens.calendar.CalendarScreen
import presentation.theme.AppTheme

@Composable
fun App(isSystemInDarkMode: Boolean) {
    AppTheme(isSystemInDarkMode) {
        KoinContext {
            PaperBackground {
                CalendarScreen()
            }
        }
    }
}