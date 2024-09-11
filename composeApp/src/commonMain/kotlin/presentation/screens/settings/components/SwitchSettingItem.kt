package presentation.screens.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
internal fun SwitchSettingItem(
    title: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ItemText(label = title)
        Switch(
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.secondaryContainer,
                checkedTrackColor = MaterialTheme.colorScheme.primary
            ),
            checked = isChecked,
            onCheckedChange = onCheckedChange,
        )
    }
}