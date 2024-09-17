package presentation.screens.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import domain.model.AppThemeMode
import presentation.theme.DMSans18Medium

@Composable
internal fun ModeSelection(selectedTheme: AppThemeMode, onClick: (AppThemeMode) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val isSelected = selectedTheme == AppThemeMode.DefaultLight
            RadioButton(colors = RadioButtonDefaults.colors(unselectedColor = MaterialTheme.colorScheme.secondary),
                selected = isSelected,
                onClick = { onClick(AppThemeMode.DefaultLight) })
            RadioButtonLabel(label = "Light Mode", isSelected = isSelected)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val isSelected = selectedTheme == AppThemeMode.DefaultDark
            RadioButton(colors = RadioButtonDefaults.colors(unselectedColor = MaterialTheme.colorScheme.secondary),
                selected = isSelected,
                onClick = { onClick(AppThemeMode.DefaultDark) })
            RadioButtonLabel(label = "Dark Mode", isSelected = isSelected)

        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val isSelected = selectedTheme == AppThemeMode.FollowSystem
            RadioButton(colors = RadioButtonDefaults.colors(unselectedColor = MaterialTheme.colorScheme.secondary),
                selected = isSelected,
                onClick = { onClick(AppThemeMode.FollowSystem) })
            RadioButtonLabel(label = "Follow System", isSelected = isSelected)
        }
    }
}

@Composable
private fun RadioButtonLabel(label: String, isSelected: Boolean) {
    Text(
        text = label,
        style = DMSans18Medium,
        fontSize = 16.sp,
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
    )
}