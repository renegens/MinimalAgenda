package presentation.screens.settings.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import presentation.theme.DMSans18Medium

private val reminderTimePresets = listOf(0, 5, 10, 15, 30, 60)

/** Preserve an existing custom offset until the user explicitly selects a different value. */
internal fun notificationTimeOptions(selectedMinutes: Int): List<Int> =
    if (selectedMinutes in reminderTimePresets) reminderTimePresets else reminderTimePresets + selectedMinutes

internal fun notificationTimeLabel(minutes: Int): String =
    if (minutes == 0) "At start" else "$minutes min"

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun NotificationTimeChips(time: Int, onTimeSelected: (Int) -> Unit) {
    Column {
        ItemTitle(label = "Notification Alert Time")
        Spacer(modifier = Modifier.height(4.dp))
        ItemBody(label = "Choose when to be notified before an event starts.")
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            notificationTimeOptions(time).forEach { minutes ->
                val selected = minutes == time
                FilterChip(
                    selected = selected,
                    onClick = { onTimeSelected(minutes) },
                    label = {
                        Text(
                            text = notificationTimeLabel(minutes),
                            style = DMSans18Medium,
                            fontSize = 16.sp,
                            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary,
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        selectedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    ),
                    border = if (selected) BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null,
                )
            }
        }
    }
}
