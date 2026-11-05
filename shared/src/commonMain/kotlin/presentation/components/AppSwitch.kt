import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable

@Composable
fun AppSwitch(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Switch(
        colors = SwitchDefaults.colors(
            checkedThumbColor = MaterialTheme.colorScheme.secondaryContainer,
            checkedTrackColor = MaterialTheme.colorScheme.primary
        ),
        checked = checked,
        onCheckedChange = onCheckedChange,
    )
}