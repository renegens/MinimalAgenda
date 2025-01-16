package presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import domain.model.AppTheme
import presentation.theme.ButtonShape
import presentation.theme.CustomColor
import presentation.theme.DMSansBlack36

@Composable
fun MainButtonWholeWidth(
    label: String,
    buttonColor: Color = MaterialTheme.colorScheme.inverseSurface,
    textColor: Color = MaterialTheme.colorScheme.inversePrimary,
    textStyle: TextStyle = DMSansBlack36,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
        shape = ButtonShape
    ) {
        Text(
            label.uppercase(),
            style = textStyle,
            color = textColor
        )
    }
}

@Composable
fun MainOutlinedButton(
    label: String,
    textColor: Color = MaterialTheme.colorScheme.inversePrimary,
    textStyle: TextStyle = DMSansBlack36,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        shape = ButtonShape,
        border = BorderStroke(
            width = 1.dp, color = MaterialTheme.colorScheme.tertiary
        )
    ) {
        Text(
            label.uppercase(),
            style = textStyle,
            color = MaterialTheme.colorScheme.tertiary
        )
    }
}

@Composable
fun SecondaryCTAButton(
    label: String,
    buttonColor: Color = MaterialTheme.colorScheme.inverseSurface,
    textColor: Color = MaterialTheme.colorScheme.inversePrimary,
    textStyle: TextStyle = DMSansBlack36,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        colors = ButtonDefaults.buttonColors(containerColor = CustomColor.Orange_500.copy(alpha = 0.4F)),
        shape = ButtonShape
    ) {
        Text(
            label.uppercase(),
            style = textStyle,
            color = CustomColor.White_500
        )
    }
}

@Composable
fun MainCTAButton(label: String, onClick: () -> Unit) {
    MainButtonWholeWidth(
        label = label,
        buttonColor = AppTheme.Default.appColor.color,
        textColor = Color.White,
        onClick = onClick
    )
}