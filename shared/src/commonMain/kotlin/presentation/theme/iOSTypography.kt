package presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val iosExtraLargeTitle: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = FontFamily.Default, // Replace with SFUI-Bold font family
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
    )

val iosExtraLargeTitle2: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = FontFamily.Default, // Replace with SFUI-Bold font family
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
    )

val iosLargeTitle: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = FontFamily.Default, // Replace with SFUI-Regular font family
        fontWeight = FontWeight.Normal,
        fontSize = 34.sp,
    )

val iosTitle1: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = FontFamily.Default, // Replace with SFUI-Regular font family
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
    )

val iosTitle2: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = FontFamily.Default, // Replace with SFUI-Regular font family
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
    )

val iosTitle3: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = FontFamily.Default, // Replace with SFUI-Regular font family
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
    )

val iosHeadline: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = FontFamily.Default, // Replace with SFUI-Semibold font family
        fontWeight = FontWeight.SemiBold,
        fontSize = 17.sp,
    )

val iosCallout: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = FontFamily.Default, // Replace with SFUI-Regular font family
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
    )

val iosSubheadline: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = FontFamily.Default, // Replace with SFUI-Regular font family
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
    )

val iosBody: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = FontFamily.Default, // Replace with SFUI-Regular font family
        fontWeight = FontWeight.Normal,
        fontSize = 17.sp,
    )

val iosFootnote: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = FontFamily.Default, // Replace with SFUI-Regular font family
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
    )