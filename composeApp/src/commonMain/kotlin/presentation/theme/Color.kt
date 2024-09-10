package presentation.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

object CustomColor {
    val Orange_500 = Color(0xFFfe3b00)

    val Gray_200 = Color(0xFFCCCCCC)
    val Gray_300 = Color(0xFF999999)
    val Gray_500 = Color(0xFF666666)

    val Sepia_500 = Color(0xFFF0ECE5)

    val Black_900 = Color(0xFF121212)
    val Black_500 = Color(0xFF000000)
    val Black_200 = Color(0xFF333333)

    val Sepia_900 = Color(0xFFd5d1c7)

    val White_500 = Color(0xFFFFFFFF)
}

val LightColorScheme: ColorScheme
    get() = lightColorScheme(
        primary = CustomColor.Orange_500,
        secondary = CustomColor.Gray_300,
        tertiary = CustomColor.Gray_500,
        background = CustomColor.Sepia_500,
        secondaryContainer = CustomColor.Sepia_900,
        inversePrimary = CustomColor.Black_900,
        inverseSurface = CustomColor.White_500
    )

val DarkColorScheme: ColorScheme
    get() = darkColorScheme(
        primary = CustomColor.Orange_500,
        secondary = CustomColor.Gray_500,
        tertiary = CustomColor.Gray_200,
        background = CustomColor.Black_200,
        secondaryContainer = CustomColor.Black_900,
        inversePrimary = CustomColor.White_500,
        inverseSurface = CustomColor.Black_900
    )