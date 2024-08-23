package presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import minimalagenda.composeapp.generated.resources.DMSans_18pt_Bold
import minimalagenda.composeapp.generated.resources.DMSans_18pt_Medium
import minimalagenda.composeapp.generated.resources.DMSans_18pt_Regular
import minimalagenda.composeapp.generated.resources.DMSans_18pt_SemiBold
import minimalagenda.composeapp.generated.resources.DMSans_24pt_Black
import minimalagenda.composeapp.generated.resources.DMSans_24pt_Medium
import minimalagenda.composeapp.generated.resources.DMSans_Black
import minimalagenda.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.Font

private val DMSansBlackFamily: FontFamily
    @Composable
    get() {
        return FontFamily(
            Font(Res.font.DMSans_Black),
        )
    }

private val DMSans18RegularFamily: FontFamily
    @Composable
    get() {
        return FontFamily(
            Font(Res.font.DMSans_18pt_Regular),
        )
    }

private val DMSans18MediumFamily: FontFamily
    @Composable
    get() {
        return FontFamily(
            Font(Res.font.DMSans_18pt_Medium),
        )
    }

private val DMSans18BoldFamily: FontFamily
    @Composable
    get() {
        return FontFamily(
            Font(Res.font.DMSans_18pt_Bold),
        )
    }

private val DMSans18SemiBoldFamily: FontFamily
    @Composable
    get() {
        return FontFamily(
            Font(Res.font.DMSans_18pt_SemiBold),
        )
    }

private val DMSans24ptFamily: FontFamily
    @Composable
    get() {
        return FontFamily(
            Font(Res.font.DMSans_24pt_Medium),
            Font(Res.font.DMSans_24pt_Black),
        )
    }

val DMSansBlack250: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = DMSansBlackFamily,
        fontWeight = FontWeight.Black,
        fontSize = 84.sp,
        letterSpacing = (-8).sp,
    )

val DMSansBlack36: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = DMSansBlackFamily,
        fontWeight = FontWeight.Black,
        fontSize = 12.sp,
        letterSpacing = 1.sp
    )



val DMSans24Black100: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = DMSans24ptFamily,
        fontWeight = FontWeight.Black,
        fontSize = 34.sp,
        letterSpacing = (-1).sp
    )

val DMSans24Medium120: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = DMSans24ptFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 40.sp,
        letterSpacing = (-2.5).sp
    )

val DMSans24Medium60: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = DMSans24ptFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
    )

val DMSans18Bold60: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = DMSans18BoldFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        letterSpacing = (-1).sp
    )

val DMSans18Regular: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = DMSans18RegularFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 40.sp
    )

val DMSans18Medium: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = DMSans18MediumFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        letterSpacing = (-1).sp
    )

val DMSans18SemiBold: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = DMSans18SemiBoldFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        letterSpacing = (-1).sp
    )

val displayLarge: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = DMSansBlackFamily,
        fontWeight = FontWeight.Black,
        fontSize = 72.sp,
        letterSpacing = (-7).sp
    )

val headlineLarge: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = DMSans24ptFamily,
        fontWeight = FontWeight.Black,
        fontSize = 32.sp,
        letterSpacing = (-0.1).sp
    )

val displayMedium: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = DMSans24ptFamily,
        fontWeight = FontWeight.Light,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    )

val displaySmall: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = DMSans24ptFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.1.sp
    )

// Headline Styles


val headlineMedium: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = DMSans24ptFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.1.sp
    )

val headlineSmall: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = DMSans24ptFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    )

// Title Styles
val titleLarge: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = DMSans24ptFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    )

val titleMedium: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = DMSans24ptFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    )

val titleSmall: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = DMSans24ptFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    )

// Body Styles
val bodyLarge: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = DMSans24ptFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )

val bodyMedium: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = DMSans24ptFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    )

val bodySmall: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = DMSans24ptFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    )

// Label Styles
val labelLarge: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = DMSans24ptFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    )

val labelMedium: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = DMSans24ptFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )

val labelSmall: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = DMSans24ptFamily,
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