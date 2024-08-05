package presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import minimalagenda.composeapp.generated.resources.OpenSans_Bold
import minimalagenda.composeapp.generated.resources.OpenSans_Regular
import minimalagenda.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.Font

val openSansFontFamily: FontFamily
    @Composable
    get() {
        return FontFamily(
            Font(Res.font.OpenSans_Regular),
            Font(Res.font.OpenSans_Bold)
        )
    }

val displayLarge: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = openSansFontFamily,
        fontWeight = FontWeight.Light,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = 0.sp
    )

val displayMedium: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = openSansFontFamily,
        fontWeight = FontWeight.Light,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    )

val displaySmall: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = openSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.1.sp
    )

// Headline Styles
val headlineLarge: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = openSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    )

val headlineMedium: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = openSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.1.sp
    )

val headlineSmall: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = openSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    )

// Title Styles
val titleLarge: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = openSansFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    )

val titleMedium: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = openSansFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    )

val titleSmall: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = openSansFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    )

// Body Styles
val bodyLarge: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = openSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )

val bodyMedium: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = openSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    )

val bodySmall: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = openSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    )

// Label Styles
val labelLarge: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = openSansFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    )

val labelMedium: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = openSansFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )

val labelSmall: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = openSansFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )


val typography: Typography
    @Composable
    get() {
        return Typography(
            displayLarge = displayLarge,
            displayMedium = displayMedium,
            displaySmall = displaySmall,
            headlineLarge = headlineLarge,
            headlineMedium = headlineMedium,
            headlineSmall = headlineSmall,
            titleLarge = titleLarge,
            titleMedium = titleMedium,
            titleSmall = titleSmall,
            bodyLarge = bodyLarge,
            bodyMedium = bodyMedium,
            bodySmall = bodySmall,
            labelLarge = labelLarge,
            labelMedium = labelMedium,
            labelSmall = labelSmall
        )
    }