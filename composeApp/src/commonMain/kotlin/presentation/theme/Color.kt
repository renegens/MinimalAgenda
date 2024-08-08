package presentation.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

object CustomColor {
    val DarkGray = Color(0xFF43474D)
    val LightGray = Color(0xFFA09F9F)
    val Orange = Color(0xFFEA4E27)
    val Sepia = Color(0xFFF0ECE5)
}

val LightColorScheme = lightColorScheme(
    primary = CustomColor.DarkGray,
    secondary = CustomColor.LightGray,
    tertiary = CustomColor.Orange,
    background = CustomColor.Sepia
)

val DarkColorScheme = darkColorScheme(
    primary = CustomColor.LightGray,
    secondary = CustomColor.Orange,
    tertiary = CustomColor.Sepia,
    background = CustomColor.DarkGray
)