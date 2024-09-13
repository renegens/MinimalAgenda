package presentation.screens.add_event

import AppSwitch
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
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
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
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
                AddEventContract.Effect.Save -> onSave.invoke()
                AddEventContract.Effect.Cancel -> onCancel.invoke()
            }
        }
    }

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    if (scrollState.isScrollInProgress) {
        focusManager.clearFocus()
    }

    Column(
        modifier = Modifier.fillMaxSize().statusBarsPadding().verticalScroll(scrollState)
    ) {
        Header(onCancel = { viewModel.setEvent(AddEventContract.Event.OnCancel) },
            isEnabled = state.isSaveEnabled(),
            onSave = { viewModel.setEvent(AddEventContract.Event.OnSave) })
        Spacer(modifier = Modifier.height(16.dp))
        Input(
            placeholder = "Title",
            content = state.title,
            onUpdate = { viewModel.setEvent(AddEventContract.Event.OnTitleEntered(it)) },
            focusRequester = focusRequester
        )
        Spacer(modifier = Modifier.height(4.dp))
        Input(
            placeholder = "Location",
            content = state.location,
            onUpdate = { viewModel.setEvent(AddEventContract.Event.OnLocationEntered(it)) },
            focusRequester = focusRequester
        )

        Spacer(modifier = Modifier.height(24.dp))

        Details(startDate = state.startDate,
            startHours = state.startHours,
            allDay = state.allDay,
            startMinutes = state.startMinutes,
            durationInMinutes = state.durationInMinutes,
            onDurationEntered = { viewModel.setEvent(AddEventContract.Event.OnDurationEntered(it)) },
            checkChange = { viewModel.setEvent(AddEventContract.Event.ToggleAllDay(it)) },
            durationError = state.durationError,
            focusManager = focusManager,
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
        Spacer(modifier = Modifier.height(64.dp))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Details(
    startDate: Long,
    startHours: Int,
    startMinutes: Int,
    durationInMinutes: String,
    allDay: Boolean,
    checkChange: (Boolean) -> Unit,
    onDurationEntered: (String) -> Unit,
    onTimeUpdate: (Triple<Long, Int, Int>) -> Unit,
    durationError: String?,
    focusManager: FocusManager
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            .background(color = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        DetailsItemRow {
            BodyText("All Day")
            Spacer(modifier = Modifier.weight(1F))
            AppSwitch(checked = allDay, onCheckedChange = checkChange)
        }

        DetailsDivider()

        DateAndTime(
            label = "Starts",
            isChecked = allDay,
            hours = startHours,
            date = startDate,
            minutes = startMinutes,
            onTimeUpdate = onTimeUpdate,
            focusManager = focusManager
        )

        if (allDay.not()) {
            DetailsDivider()

            DetailsItemRow {
                BodyText("Event duration in minutes")
                Spacer(Modifier.weight(1F))
                DurationInput(durationInMinutes = durationInMinutes,
                    onValueChange = { onDurationEntered.invoke(it) })
            }

            val durations = listOf(10, 15, 30, 60, 90, 120)

            FlowRow(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                durations.forEach { duration ->
                    AssistChip(
                        onClick = {
                            focusManager.clearFocus()
                            onDurationEntered.invoke(duration.toString())
                        },
                        label = { BodyText("${duration}m") },
                        colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        border = null
                    )
                }
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
}

@Composable
private fun DurationInput(
    durationInMinutes: String, onValueChange: (String) -> Unit,
) {

    Box(
        modifier = Modifier.width(64.dp).height(40.dp).background(
            color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(8.dp)
        ), contentAlignment = Alignment.CenterEnd
    ) {
        BasicTextField(
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            textStyle = TextFieldStyleDateAndTime,
            value = durationInMinutes,
            onValueChange = {
                onValueChange.invoke(it)
                //text = it
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
    onTimeUpdate: (Triple<Long, Int, Int>) -> Unit,
    focusManager: FocusManager
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

            if (showDatePicker) {
                focusManager.clearFocus()
            }
            showTimePicker = false
        }

        if (isChecked.not()) {
            Spacer(modifier = Modifier.width(8.dp))
            TemporalDisplay(selectedTime) {
                showTimePicker = !showTimePicker

                if (showTimePicker) {
                    focusManager.clearFocus()
                }

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
        TimePicker(
            state = timePickerState,
            colors = TimePickerDefaults.colors(
                timeSelectorSelectedContentColor = MaterialTheme.colorScheme.primary,
                timeSelectorSelectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                timeSelectorUnselectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally)
        )
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
private fun Input(
    placeholder: String,
    content: String,
    onUpdate: (String) -> Unit,
    focusRequester: FocusRequester
) {
    OutlinedTextField(
        value = content,
        onValueChange = {
            onUpdate.invoke(it)
        },
        placeholder = { BodyText(label = placeholder, color = MaterialTheme.colorScheme.surfaceVariant) },
        trailingIcon = {
            if (content.isNotEmpty()) {
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
        modifier = Modifier.fillMaxWidth().height(56.dp).padding(horizontal = 16.dp)
            .focusRequester(focusRequester),
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