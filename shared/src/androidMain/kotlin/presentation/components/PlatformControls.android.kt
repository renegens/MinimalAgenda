package presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.EventAvailable
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset

@Composable
actual fun AppIcon(symbol: AppSymbol, contentDescription: String?, modifier: Modifier, tint: Color) {
    val vector = when (symbol) {
        AppSymbol.Menu -> Icons.Outlined.MoreHoriz
        AppSymbol.Visibility -> Icons.Outlined.Visibility
        AppSymbol.VisibilityOff -> Icons.Outlined.VisibilityOff
        AppSymbol.Clock -> Icons.Outlined.Schedule
        AppSymbol.Add -> Icons.Outlined.Add
        AppSymbol.Settings -> Icons.Outlined.Settings
        AppSymbol.Reminder -> Icons.Outlined.Notifications
        AppSymbol.Info -> Icons.Outlined.Info
        AppSymbol.Edit -> Icons.Outlined.Edit
        AppSymbol.CalendarAdd -> Icons.Outlined.EventAvailable
        AppSymbol.PrivacyLock -> Icons.Filled.Lock
    }
    Icon(vector, contentDescription, modifier, tint)
}

@Composable
actual fun AppDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    offset: DpOffset,
    containerColor: Color,
    content: @Composable () -> Unit,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        offset = offset,
        containerColor = containerColor,
    ) { content() }
}

@Composable
actual fun AppMenuAction(
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    enabled: Boolean,
    content: @Composable () -> Unit,
) {
    DropdownMenuItem(text = content, onClick = onClick, leadingIcon = icon, enabled = enabled)
}
