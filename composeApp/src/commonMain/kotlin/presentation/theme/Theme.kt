package presentation.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.daryse.daryse.presentation.theme.AppShapes

private val LightThemeColors = lightColors(
    primary = CustomColor.MainPrimary,
    primaryVariant = CustomColor.MainSecondary,
    onPrimary = CustomColor.White,
    secondary = CustomColor.BackgroundSecondary,
    secondaryVariant = CustomColor.BackgroundChrome,
    onSecondary = CustomColor.MainPrimary,
    error = CustomColor.StatusSignal,
    onError = CustomColor.White,
    background = CustomColor.White,
    onBackground = CustomColor.MainPrimary,
    surface = CustomColor.White,
    onSurface = CustomColor.MainPrimary,
)

val TextFieldDefaults.clearTextFieldColors: TextFieldColors
    @Composable
    get() = textFieldColors(
        textColor = CustomColor.MainPrimary,
        disabledTextColor = Color.Transparent,
        backgroundColor = CustomColor.White,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent
    )

val TextFieldDefaults.regularTextFieldColors: TextFieldColors
    @Composable
    get() = textFieldColors(
        textColor = CustomColor.MainPrimary,
        disabledTextColor = CustomColor.MainTertiary,
        backgroundColor = CustomColor.White,
        focusedIndicatorColor = CustomColor.MainPrimary,
        unfocusedIndicatorColor = CustomColor.MainTertiary,
        disabledIndicatorColor = CustomColor.BackgroundQuaternary
    )

val TextFieldDefaults.textInput: TextFieldColors
    @Composable
    get() = textFieldColors(
        textColor = CustomColor.MainPrimary,
        disabledTextColor = Color.Transparent,
        backgroundColor = CustomColor.BackgroundChrome,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent
    )

@Composable
fun AppTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colors = LightThemeColors,
        typography = InterTypography,
        shapes = AppShapes
    ) {
        content()
    }
}
