package presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import com.slapps.cupertino.CupertinoDatePicker
import com.slapps.cupertino.CupertinoTimePicker
import com.slapps.cupertino.ExperimentalCupertinoApi
import com.slapps.cupertino.rememberCupertinoDatePickerState
import com.slapps.cupertino.rememberCupertinoTimePickerState
import kotlinx.coroutines.flow.drop

@OptIn(ExperimentalCupertinoApi::class)
@Composable
actual fun AppDatePicker(date: Long, modifier: Modifier, onDateSelected: (Long) -> Unit) {
    val onSelected by rememberUpdatedState(onDateSelected)
    key(date) {
        val pickerState = rememberCupertinoDatePickerState(initialSelectedDateMillis = date)
        LaunchedEffect(pickerState) {
            snapshotFlow { pickerState.selectedDateMillis }.drop(1).collect { onSelected(it) }
        }
        CupertinoDatePicker(
            state = pickerState,
            modifier = modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        )
    }
}

@OptIn(ExperimentalCupertinoApi::class)
@Composable
actual fun AppTimePicker(
    hours: Int,
    minutes: Int,
    is24Hour: Boolean,
    modifier: Modifier,
    onTimeSelected: (Int, Int) -> Unit,
) {
    val modelTime = hours to minutes
    var seed by remember { mutableStateOf(modelTime) }
    var lastEmitted by remember { mutableStateOf(modelTime) }
    val currentModel by rememberUpdatedState(modelTime)
    val onUpdate by rememberUpdatedState(onTimeSelected)
    LaunchedEffect(modelTime) {
        if (modelTime != lastEmitted) seed = modelTime
        lastEmitted = modelTime
    }
    val activeSeed = seed
    key(activeSeed, is24Hour) {
        val pickerState = rememberCupertinoTimePickerState(activeSeed.first, activeSeed.second, is24Hour)
        LaunchedEffect(pickerState) {
            snapshotFlow { pickerState.hour to pickerState.minute }.drop(1).collect { value ->
                if (activeSeed == seed && currentModel == lastEmitted) {
                    lastEmitted = value
                    onUpdate(value.first, value.second)
                }
            }
        }
        CupertinoTimePicker(
            state = pickerState,
            modifier = modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        )
    }
}
