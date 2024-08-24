package presentation.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

object CustomColor {
    val Orange_500 = Color(0xFFfe3b00) //done

    val Gray_200 = Color(0xFFCCCCCC)
    val Gray_300 = Color(0xFF999999) //done
    val Gray_500 = Color(0xFF666666) //done

    val Sepia_500 = Color(0xFFF0ECE5)

    val Black_900 = Color(0xFF121212)
    val Black_500 = Color(0xFF000000)
    val Black_200 = Color(0xFF333333) //done

    val Sepia_900 = Color(0xFFd5d1c7)

    val White_500 = Color(0xFFFFFFFF)
}

val LightColorScheme = lightColorScheme(
    primary = CustomColor.Orange_500,
    secondary = CustomColor.Gray_300,
    tertiary = CustomColor.Gray_500,
    background = CustomColor.Sepia_500,
    secondaryContainer = CustomColor.Sepia_900,
)

val DarkColorScheme = darkColorScheme(
    primary = CustomColor.Orange_500,
    secondary = CustomColor.Gray_500,
    tertiary = CustomColor.Gray_200,
    background = CustomColor.Black_200,
    secondaryContainer = CustomColor.Black_900,
)