package presentation.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

object CustomColor {
    val Orange_500 = Color(0xFFEA4E27)
    val Orange_700 = Color(0xFFea4d27)
    val Gray_900 = Color(0xFF666666)
    val Gray_200 = Color(0xFFCCCCCC)
    val Gray_300 = Color(0xFFA09F9F)
    val Gray_700 = Color(0xFF43474D)
    val Sepia_500 = Color(0xFFF0ECE5)
    val Black_500 = Color(0xFF000000)
    val Sepia_900 = Color(0xFFd5d1c7)
    val Black_900 = Color(0xFF121212)
    val Black_200 = Color(0xFF333333)
    val White_800 = Color(0xFFe4e4e4)
}

val LightColorScheme = lightColorScheme(
    primary = CustomColor.Orange_500,
    secondary = CustomColor.Gray_300,
    tertiary = CustomColor.Gray_700,
    background = CustomColor.Sepia_500,
    secondaryContainer = CustomColor.Sepia_900,
)

val DarkColorScheme = darkColorScheme(
    primary = CustomColor.Orange_700,
    secondary = CustomColor.Gray_900,
    tertiary = CustomColor.Gray_200,
    background = CustomColor.Black_200,
    secondaryContainer = CustomColor.Black_900,
)