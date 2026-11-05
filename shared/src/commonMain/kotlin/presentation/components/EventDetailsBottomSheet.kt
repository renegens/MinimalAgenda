package presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import domain.model.AgendaEvent
import platform.formatLocalDate
import platform.formatLocalTime
import presentation.theme.DMSans18Bold60
import presentation.theme.DMSans18Medium
import presentation.theme.iosExtraLargeTitle

@Composable
fun EventDetailsBottomSheet(
    event: AgendaEvent,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                style = iosExtraLargeTitle,
                text = "Event Details",
                color = MaterialTheme.colorScheme.tertiary,
            )
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Close details",
                    tint = MaterialTheme.colorScheme.tertiary
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Title
        DetailRow(
            label = "Title",
            value = event.title
        )
        
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        
        // Date
        val dateText = formatLocalDate(event.startLocalDateTime.date)
        DetailRow(
            label = "Date",
            value = dateText
        )
        
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        
        // Time
        if (event.allDay) {
            DetailRow(
                label = "Time",
                value = "All Day"
            )
        } else {
            val startTime = formatLocalTime(event.startLocalDateTime.hour, event.startLocalDateTime.minute)
            val endTime = formatLocalTime(event.endLocalDateTime.hour, event.endLocalDateTime.minute)
            DetailRow(
                label = "Time",
                value = "$startTime - $endTime"
            )
        }
        
        // Location (if available)
        if (!event.location.isNullOrBlank()) {
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            DetailRow(
                label = "Location",
                value = event.location
            )
        }
        
        // Recurring status
        if (event.isRecurring) {
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            DetailRow(
                label = "Recurring",
                value = "Yes"
            )
        }
        
        // Multi-day status
        if (event.isMultiDay) {
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            val multiDayText = if (event.isLastDayInMultiDayEvent) {
                "Ending"
            } else {
                "Multi-day event"
            }
            DetailRow(
                label = "Type",
                value = multiDayText
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = label,
            style = DMSans18Medium,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = value,
            style = DMSans18Bold60,
            color = MaterialTheme.colorScheme.tertiary
        )
    }
}

