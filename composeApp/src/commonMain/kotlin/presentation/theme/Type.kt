@file:OptIn(ExperimentalTextApi::class)

package presentation.theme

import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import minimalagenda.composeapp.generated.resources.Inter_VariableFont
import minimalagenda.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.Font


private val InterMedium: FontFamily
    @Composable
    get() {
        return FontFamily(
            Font(
                Res.font.Inter_VariableFont,
                weight = FontWeight.Medium,
                style = FontStyle.Normal
            )
        )
    }

private val InterSemiBold: FontFamily
    @Composable
    get() {
        return FontFamily(
            Font(
                Res.font.Inter_VariableFont,
                weight = FontWeight.SemiBold,
                style = FontStyle.Normal
            )
        )
    }

private val InterExtraBold: FontFamily
    @Composable
    get() {
        return FontFamily(
            Font(
                Res.font.Inter_VariableFont,
                weight = FontWeight.ExtraBold,
                style = FontStyle.Normal
            )
        )
    }


val Header1: TextStyle
    @Composable
    get() {
        return TextStyle(
            fontFamily = InterExtraBold,
            letterSpacing = (-2).sp,
            fontSize = 32.sp,
            lineHeight = 36.sp
        )
    }

val Header2: TextStyle
    @Composable
    get() {
        return TextStyle(
            fontFamily = InterSemiBold,
            letterSpacing = (-3).sp,
            fontSize = 24.sp,
            lineHeight = 28.sp,
        )
    }

val Body: TextStyle
    @Composable
    get() {
        return TextStyle(
            fontFamily = InterMedium,
            fontSize = 16.sp,
            lineHeight = 20.sp,
        )
    }

//
//val Typography.Title: TextStyle
//    @Composable
//    get() {
//        return title
//    }
//
//private val title: TextStyle = TextStyle(
//    fontFamily = Inter,
//    fontWeight = FontWeight.Bold,
//    fontSize = 24.sp,
//    lineHeight = 30.sp,
//    lineHeightStyle = LineHeightStyle(
//        alignment = LineHeightStyle.Alignment.Center,
//        trim = LineHeightStyle.Trim.None
//    )
//)
//
//private val smallerTitle: TextStyle = TextStyle(
//    fontFamily = Inter,
//    fontWeight = FontWeight.SemiBold,
//    fontSize = 21.sp,
//    lineHeight = 23.sp,
//    lineHeightStyle = LineHeightStyle(
//        alignment = LineHeightStyle.Alignment.Center,
//        trim = LineHeightStyle.Trim.None
//    )
//)
//
//val Typography.CaptionRegular: TextStyle
//    @Composable
//    get() {
//        return captionRegular
//    }
//
//private val captionRegular = TextStyle(
//    fontFamily = Inter,
//    fontWeight = FontWeight.Medium,
//    fontSize = 18.sp,
//    lineHeight = 22.5.sp,
//)
//
//val Typography.CaptionAccent: TextStyle
//    @Composable
//    get() {
//        return captionAccent
//    }
//
//private val captionAccent = TextStyle(
//    fontFamily = Inter,
//    fontWeight = FontWeight.SemiBold,
//    fontSize = 18.sp,
//    lineHeight = 22.5.sp,
//)
//
//val Typography.BodyRegular: TextStyle
//    @Composable
//    get() {
//        return bodyRegular
//    }
//
//val bodyRegular = TextStyle(
//    fontFamily = Inter,
//    fontWeight = FontWeight.Medium,
//    fontSize = 16.sp,
//    lineHeight = 20.sp,
//)
//
//val Typography.BodyAccent: TextStyle
//    @Composable
//    get() {
//        return bodyAccent
//    }
//
//private val bodyAccent = TextStyle(
//    fontFamily = Inter,
//    fontWeight = FontWeight.SemiBold,
//    fontSize = 16.sp,
//    lineHeight = 22.sp,
//)
//
//val Typography.Numbers: TextStyle
//    @Composable
//    get() {
//        return numbers
//    }
//
//private val numbers = TextStyle(
//    fontFamily = Inter,
//    fontWeight = FontWeight.Medium,
//    fontSize = 16.sp,
//    lineHeight = 24.sp,
//)
//
//val Typography.Subline: TextStyle
//    @Composable
//    get() {
//        return subline
//    }
//
//private val subline = TextStyle(
//    fontFamily = Inter,
//    fontWeight = FontWeight.Medium,
//    fontSize = 14.sp,
//    lineHeight = 16.sp,
//)
//
//val Typography.Tab: TextStyle
//    @Composable
//    get() {
//        return tab
//    }
//
//private val tab = TextStyle(
//    fontFamily = Inter,
//    fontWeight = FontWeight.SemiBold,
//    fontSize = 16.sp,
//    lineHeight = 20.sp,
//)
//
//val Typography.LabelRegular: TextStyle
//    @Composable
//    get() {
//        return labelRegular
//    }
//
//private val labelRegular = TextStyle(
//    fontFamily = Inter,
//    fontWeight = FontWeight.Medium,
//    fontSize = 12.sp,
//    lineHeight = 16.sp,
//)
//
//val Typography.LabelSmall: TextStyle
//    @Composable
//    get() {
//        return labelSmall
//    }
//
//private val labelSmall = TextStyle(
//    fontFamily = Inter,
//    fontWeight = FontWeight.Medium,
//    fontSize = 10.sp,
//    lineHeight = 16.sp,
//)
//
//val BaiJamjureeTypography = Typography(
//    h1 = header1,
//    h2 = title,
//    h3 = smallerTitle,
//    h4 = captionAccent,
//    h5 = subline,
//    body1 = bodyRegular,
//    body2 = bodyAccent,
//    subtitle1 = numbers,
//    subtitle2 = subline,
//    button = tab,
//    caption = labelRegular,
//    overline = labelSmall
//)



val InterTypography: Typography
    @Composable
    get() {
        return Typography(
            h1 = Header1,
            body1 = Body,
        )
    }



