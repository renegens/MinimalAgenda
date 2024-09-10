import androidx.compose.ui.window.ComposeUIViewController
import di.initKoin
import presentation.screens.main.App

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) {
    App()
}