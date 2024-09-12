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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel
import platform.formatTimestamp
import presentation.components.NewEventHeader
import presentation.theme.DMSans18Medium
import presentation.theme.TextFieldStyleAddEvent
import presentation.theme.TextFieldStyleDateAndTime

@Composable
fun AddEventScreen(onCancel: () -> Unit, onSave: () -> Unit) {
    val viewModel: AddEventViewModel = koinViewModel()
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest {
            when (it) {
                AddEventContract.Effect.OnSave -> onSave.invoke()
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().statusBarsPadding().verticalScroll(rememberScrollState())
    ) {
        Header(onCancel,
            isEnabled = state.title.isNotEmpty() && state.durationError == null,
            onSave = { viewModel.setEvent(AddEventContract.Event.OnSave) })
        Spacer(modifier = Modifier.height(16.dp))
        TitleInput(eventTitle = state.title,
            onUpdate = { viewModel.setEvent(AddEventContract.Event.OnTitleEntered(it)) })
        Spacer(modifier = Modifier.height(16.dp))

        Details(startDate = state.startDate,
            startHours = state.startHours,
            isChecked = state.allDay,
            startMinutes = state.startMinutes,
            durationInMinutes = state.durationInMinutes,
            onDurationEntered = { viewModel.setEvent(AddEventContract.Event.OnDurationEntered(it)) },
            checkChange = { viewModel.setEvent(AddEventContract.Event.ToggleAllDay(it)) },
            durationError = state.durationError,
            onTimeUpdate = {
                viewModel.setEvent(
                    AddEventContract.Event.OnStartEntered(
                        timestamp = it.first, hours = it.second, minutes = it.third
                    )
                )
            })

        Spacer(modifier = Modifier.height(24.dp))
        BodyText(
            "New events are added to the default calendar",
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Composable
private fun Details(
    startDate: Long,
    startHours: Int,
    startMinutes: Int,
    durationInMinutes: Int,
    isChecked: Boolean,
    checkChange: (Boolean) -> Unit,
    onDurationEntered: (String) -> Unit,
    onTimeUpdate: (Triple<Long, Int, Int>) -> Unit,
    durationError: String?
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

        DateAndTime(
            label = "Starts",
            isChecked = isChecked,
            hours = startHours,
            date = startDate,
            minutes = startMinutes,
            onTimeUpdate = onTimeUpdate
        )

        DetailsDivider()

        DetailsItemRow {
            BodyText("Event duration in minutes")
            Spacer(Modifier.weight(1F))
            DurationInput(
                durationInMinutes = durationInMinutes,
                onValueChange = { onDurationEntered.invoke(it) }
            )
        }
        if (durationError != null) {
            Text(
                text = durationError,
                style = DMSans18Medium,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 4.dp, start = 16.dp, bottom = 8.dp)
            )
        }
    }
}

@Composable
private fun DurationInput(
    durationInMinutes: Int,
    onValueChange: (String) -> Unit
) {
    var text by remember { mutableStateOf(durationInMinutes.toString()) }

    Box(
        modifier = Modifier.width(64.dp).height(40.dp).background(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(8.dp)
        ), contentAlignment = Alignment.CenterEnd
    ) {
        BasicTextField(
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            textStyle = TextFieldStyleDateAndTime,
            value = text,
            onValueChange = {
                onValueChange.invoke(it)
                text = it
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ColumnScope.DateAndTime(
    label: String,
    isChecked: Boolean,
    date: Long,
    hours: Int,
    minutes: Int,
    onTimeUpdate: (Triple<Long, Int, Int>) -> Unit
) {

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = date)
    val timePickerState =
        rememberTimePickerState(initialHour = hours, initialMinute = minutes, is24Hour = true)
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val selectedDate = datePickerState.selectedDateMillis?.let {
        onTimeUpdate.invoke(Triple(it, timePickerState.hour, timePickerState.minute))
        formatTimestamp(it)
    } ?: ""

    val minutesToDisplay =
        if (timePickerState.minute < 10) "0${timePickerState.minute}" else timePickerState.minute
    val selectedTime = "${timePickerState.hour}:$minutesToDisplay"

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
private fun TitleInput(eventTitle: String, onUpdate: (String) -> Unit) {
    OutlinedTextField(
        value = eventTitle,
        onValueChange = {
            onUpdate.invoke(it)
        },
        placeholder = { BodyText("Title") },
        trailingIcon = {
            if (eventTitle.isNotEmpty()) {
                IconButton(onClick = { onUpdate.invoke("") }) {
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
private fun Header(onCancel: () -> Unit, onSave: () -> Unit, isEnabled: Boolean) {
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
        TextButton(onClick = onSave, enabled = isEnabled) {
            BodyText(
                label = "Add",
                color = if (isEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
private fun BodyText(
    label: String, color: Color = MaterialTheme.colorScheme.tertiary, modifier: Modifier = Modifier
) {
    Text(
        label,
        style = DMSans18Medium,
        color = color,
        fontSize = 16.sp,
        modifier = Modifier.then(modifier)
    )
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