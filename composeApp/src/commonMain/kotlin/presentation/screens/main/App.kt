package presentation.screens.main

import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import org.koin.compose.KoinContext
import presentation.screens.calendar.CalendarScreen
import presentation.theme.AppTheme

@Composable
fun App(isSystemInDarkMode: Boolean) {
    AppTheme(isSystemInDarkMode) {
        KoinContext {
            Surface {
                CalendarScreen()
            }
        }
    }
}