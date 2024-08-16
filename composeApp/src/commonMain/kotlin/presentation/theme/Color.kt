package presentation.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

object CustomColor {
    val DarkGray = Color(0xFF43474D)
    val LightGray = Color(0xFFA09F9F)
    val Orange = Color(0xFFEA4E27)
    val Sepia = Color(0xFFF0ECE5)
    val LightSelection = Color(0xFFd5d1c7)
    val DarkSelection = Color(0xFFd5d1c7)
}

val LightColorScheme = lightColorScheme(
    primary = CustomColor.Orange,
    secondary = CustomColor.LightGray,
    tertiary = CustomColor.DarkGray,
    background = CustomColor.Sepia,
    secondaryContainer = CustomColor.LightSelection
)

val DarkColorScheme = darkColorScheme(
    primary = CustomColor.LightGray,
    secondary = CustomColor.Orange,
    tertiary = CustomColor.Sepia,
    background = CustomColor.DarkGray
)