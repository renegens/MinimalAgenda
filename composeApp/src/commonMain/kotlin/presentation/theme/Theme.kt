package presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import org.koin.compose.KoinContext

@Composable
fun AppTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    KoinContext {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            content = content
        )
    }
}