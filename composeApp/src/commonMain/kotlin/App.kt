import androidx.compose.runtime.*
import org.jetbrains.compose.ui.tooling.preview.Preview

import presentation.screens.main.MainScreen
import presentation.theme.AppTheme

@Composable
@Preview
fun App() {
    AppTheme {
        MainScreen()
    }
}