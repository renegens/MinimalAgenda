package presentation.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

object CustomColor {
    val Red_500 = Color(0xFFfe3b00)
    val Mint_500 = Color(0xFF00b1a9)
    val Rosa_500 = Color(0xFFf84a6d)
    val Orange_500 = Color(0xFFf26803)
    val Lila_500 = Color(0xFFb453d9)
    val Blue_500 = Color(0xFF0078fb)


    val Gray_200 = Color(0xFFCCCCCC)
    val Gray_300 = Color(0xFF999999)
    val Gray_500 = Color(0xFF666666)

    val Sepia_500 = Color(0xFFF0ECE5)

    val Sepia_100 = Color(0xFFece8e0)

    val Black_900 = Color(0xFF121212)
    val Black_500 = Color(0xFF000000)
    val Black_200 = Color(0xFF333333)

    val Sepia_900 = Color(0xFFd5d1c7)

    val White_500 = Color(0xFFFFFFFF)
}

fun getLightColorScheme(primaryColor: Color): ColorScheme {
    return lightColorScheme(
        primary = primaryColor, secondary =
        CustomColor.Gray_300,
        tertiary = CustomColor.Gray_500,
        surface = CustomColor.White_500,
        onSurface = CustomColor.Black_900,
        secondaryContainer = CustomColor.Sepia_900,
        inversePrimary = CustomColor.Black_900,
        inverseSurface = CustomColor.White_500,
        surfaceVariant = CustomColor.Sepia_100
    )
}

fun getDarkColorScheme(primaryColor: Color): ColorScheme {
    return darkColorScheme(
        primary = primaryColor,
        secondary = CustomColor.Gray_500,
        tertiary = CustomColor.Gray_200,
        secondaryContainer = CustomColor.Black_900,
        inversePrimary = CustomColor.White_500,
        inverseSurface = CustomColor.Black_900,
        surfaceVariant = CustomColor.Black_200,
        surface = CustomColor.Black_900,
        onSurface = CustomColor.White_500
    )
}