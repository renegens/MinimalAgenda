import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext

import presentation.screens.main.MainScreen
import presentation.theme.AppTheme

@Composable
fun App(isSystemInDarkMode: Boolean) {
    AppTheme(isSystemInDarkMode) {
        KoinContext {
            Surface {
                MainScreen()
            }
        }
    }
}