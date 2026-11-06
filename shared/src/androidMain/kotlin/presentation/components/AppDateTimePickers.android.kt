package presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun AppDatePicker(date: Long, modifier: Modifier, onDateSelected: (Long) -> Unit) {
    val pickerState = rememberDatePickerState(initialSelectedDateMillis = date)
    LaunchedEffect(date) {
        if (pickerState.selectedDateMillis != date) {
            pickerState.selectedDateMillis = date
            pickerState.displayedMonthMillis = date
        }
    }
    // User writes invoke callbacks; model synchronization writes to the delegate directly.
    val controlledState = object : DatePickerState by pickerState {
        override var selectedDateMillis: Long?
            get() = pickerState.selectedDateMillis
            set(value) {
                pickerState.selectedDateMillis = value
                value?.let(onDateSelected)
            }
    }
    DatePicker(
        title = null,
        headline = null,
        state = controlledState,
        modifier = modifier,
        colors = DatePickerDefaults.colors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        showModeToggle = false,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun AppTimePicker(
    hours: Int,
    minutes: Int,
    is24Hour: Boolean,
    modifier: Modifier,
    onTimeSelected: (Int, Int) -> Unit,
) {
    val pickerState = rememberTimePickerState(hours, minutes, is24Hour)
    LaunchedEffect(hours, minutes) {
        pickerState.hour = hours
        pickerState.minute = minutes
    }
    val controlledState = object : TimePickerState by pickerState {
        override var hour: Int
            get() = pickerState.hour
            set(value) {
                pickerState.hour = value
                onTimeSelected(value, pickerState.minute)
            }
        override var minute: Int
            get() = pickerState.minute
            set(value) {
                pickerState.minute = value
                onTimeSelected(pickerState.hour, value)
            }
    }
    val density = LocalDensity.current.density
    CompositionLocalProvider(LocalDensity provides Density(density, 0.8f)) {
        TimePicker(
            state = controlledState,
            colors = TimePickerDefaults.colors(
                timeSelectorSelectedContentColor = MaterialTheme.colorScheme.primary,
                timeSelectorSelectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                timeSelectorUnselectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = modifier.fillMaxWidth(),
        )
    }
}
