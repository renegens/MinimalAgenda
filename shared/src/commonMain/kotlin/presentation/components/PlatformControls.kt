package presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset

enum class AppSymbol {
    Menu, Visibility, VisibilityOff, Clock, Add, Settings, Reminder, Info, Edit, CalendarAdd, PrivacyLock
}

@Composable
expect fun AppIcon(
    symbol: AppSymbol,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color,
)

@Composable
expect fun AppDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    offset: DpOffset,
    containerColor: Color,
    content: @Composable () -> Unit,
)

@Composable
expect fun AppMenuAction(
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
)

@Composable
expect fun AppDatePicker(
    date: Long,
    modifier: Modifier = Modifier,
    onDateSelected: (Long) -> Unit,
)

@Composable
expect fun AppTimePicker(
    hours: Int,
    minutes: Int,
    is24Hour: Boolean,
    modifier: Modifier = Modifier,
    onTimeSelected: (Int, Int) -> Unit,
)
