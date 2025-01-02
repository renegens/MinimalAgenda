package presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
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
        colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
    ) {
        Text(
            label.uppercase(),
            style = textStyle,
            color = textColor
        )
    }
}