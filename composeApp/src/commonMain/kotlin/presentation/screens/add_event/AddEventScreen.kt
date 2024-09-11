package presentation.screens.add_event

import AppSwitch
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel
import presentation.components.NewEventHeader
import presentation.theme.DMSans18Medium
import presentation.theme.TextFieldStyleAddEvent

@Composable
fun AddEventScreen(onCancel: () -> Unit, onSave: () -> Unit) {
    val viewModel: AddEventViewModel = koinViewModel()
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest {
            when (it) {

                else -> {}
            }
        }
    }


    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier.fillMaxSize().statusBarsPadding()
            .scrollable(state = scrollState, orientation = Orientation.Vertical)
    ) {
        Header(onCancel, onSave)
        Spacer(modifier = Modifier.height(16.dp))
        TitleInput(state.title)
        Spacer(modifier = Modifier.height(16.dp))

        Details(startDate = state.startDate,
            startHours = state.startHours,
            endDate = state.endDate,
            endHours = state.endHours,
            isChecked = state.allDay,
            checkChange = { viewModel.setEvent(AddEventContract.Event.ToggleAllDay(it)) })
    }
}

@Composable
private fun Details(
    startDate: Long,
    startHours: Int,
    endDate: Long,
    endHours: Int,
    isChecked: Boolean,
    checkChange: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            .background(color = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        DetailsItemRow {
            BodyText("All Day")
            Spacer(modifier = Modifier.weight(1F))
            AppSwitch(checked = isChecked, onCheckedChange = checkChange)
        }

        DetailsDivider()

        DateAndTime(label = "Starts", isChecked = isChecked, hours = startHours, date = startDate)

        DetailsDivider()

        DateAndTime(label = "Ends", isChecked = isChecked, hours = endHours, date = endDate)

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ColumnScope.DateAndTime(label: String, isChecked: Boolean, date: Long, hours: Int) {

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = date)
    val timePickerState = rememberTimePickerState(initialHour = hours, is24Hour = true)
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val selectedDate = datePickerState.selectedDateMillis?.let {
        Instant.fromEpochMilliseconds(it)
            .toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()

    } ?: ""

    val selectedTime = "${timePickerState.hour}:${timePickerState.minute}"

    DetailsItemRow {
        BodyText(label)
        Spacer(Modifier.weight(1F))
        TemporalDisplay(selectedDate) {
            showDatePicker = !showDatePicker
            showTimePicker = false
        }

        if (isChecked.not()) {
            Spacer(modifier = Modifier.width(8.dp))
            TemporalDisplay(selectedTime) {
                showTimePicker = !showTimePicker
                showDatePicker = false
            }
        }
    }

    AnimatedVisibility(
        visible = showDatePicker, enter = expandVertically(), exit = shrinkVertically()
    ) {
        DatePicker(
            state = datePickerState,
            showModeToggle = false,
        )
    }

    AnimatedVisibility(
        visible = showTimePicker, enter = expandVertically(), exit = shrinkVertically()
    ) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            TimePicker(
                state = timePickerState,
                colors = TimePickerDefaults.colors(timeSelectorSelectedContentColor = MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@Composable
private fun TemporalDisplay(content: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier.width(108.dp).height(40.dp).background(
            color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(8.dp)
        ).clickable { onClick.invoke() }, contentAlignment = Alignment.Center
    ) {
        Text(
            text = content,
            fontSize = 16.sp,
            style = DMSans18Medium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun TitleInput(eventTitle: String) {
    var text by remember { mutableStateOf(eventTitle) }

    OutlinedTextField(
        value = text,
        onValueChange = { text = it },
        placeholder = { BodyText("Title") },
        trailingIcon = {
            if (text.isNotEmpty()) {
                IconButton(onClick = { text = "" }) {
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = "Clear text",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            focusedIndicatorColor = MaterialTheme.colorScheme.secondaryContainer,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        textStyle = TextFieldStyleAddEvent,
        shape = RectangleShape,
        modifier = Modifier.fillMaxWidth().height(56.dp).padding(horizontal = 16.dp),
    )
}

@Composable
private fun Header(onCancel: () -> Unit, onSave: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = onCancel) {
            BodyText(
                label = "Cancel", color = MaterialTheme.colorScheme.tertiary
            )
        }
        NewEventHeader("New")
        TextButton(onClick = onSave) {
            BodyText(label = "Add", color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun BodyText(label: String, color: Color = MaterialTheme.colorScheme.tertiary) {
    Text(label, style = DMSans18Medium, color = color, fontSize = 16.sp)
}

@Composable
private fun DetailsDivider() {
    HorizontalDivider(
        color = MaterialTheme.colorScheme.inverseSurface,
        modifier = Modifier.padding(horizontal = 8.dp)
    )
}

@Composable
private fun DetailsItemRow(content: @Composable () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().height(64.dp).padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) { content.invoke() }
}