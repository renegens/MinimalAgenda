package presentation.screens.add_event

import AppSwitch
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.DatePickerDefaults
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
import com.slapps.cupertino.CupertinoDatePicker
import com.slapps.cupertino.CupertinoTimePicker
import com.slapps.cupertino.ExperimentalCupertinoApi
import com.slapps.cupertino.rememberCupertinoDatePickerState
import com.slapps.cupertino.rememberCupertinoTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.key
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import data.calendar.CalendarEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import presentation.components.AppAlertDialog
import platform.formatTimestamp
import platform.formatLocalTime
import platform.uses24HourClock
import presentation.components.NewEventHeader
import presentation.components.clickableWithOpaqueText
import presentation.theme.DMSans18Medium
import presentation.theme.TextFieldStyleAddEvent
import getPlatform

@Composable
fun AddEventScreen(
    onCancel: () -> Unit,
    onSave: () -> Unit,
    request: EventEditorRequest = EventEditorRequest.Add(),
) {
    val viewModel: AddEventViewModel = koinViewModel(key = request.hashCode().toString()) { parametersOf(request) }
    val state by viewModel.uiState.collectAsState()
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    // Avoid unnecessary recomposition with a debounced scroll focus clear
    LaunchedEffect(scrollState.isScrollInProgress) {
        if (scrollState.isScrollInProgress) {
            delay(200) // Debounce duration can be adjusted as needed
            focusManager.clearFocus()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                AddEventContract.Effect.Save -> onSave()
                AddEventContract.Effect.Cancel -> onCancel()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(top = 8.dp)
            .verticalScroll(scrollState)
    ) {
        Header(
            label = if (state.isEditing) "Edit" else "New",
            onCancel = { viewModel.setEvent(AddEventContract.Event.OnCancel) },
            isEnabled = state.isSaveEnabled(),
            onSave = { viewModel.setEvent(AddEventContract.Event.OnSave) }
        )
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

        if (state.timesEditable) {
            val end = state.toStartAndEnd().second
            Details(
                startDate = state.startDate,
                startHours = state.startHours,
                startMinutes = state.startMinutes,
                endDate = end.date.toPickerMillis(),
                endHours = end.hour,
                endMinutes = end.minute,
                allDay = state.allDay,
                selectedDurationMinutes = state.selectedDurationMinutes,
                onDurationSelected = { viewModel.setEvent(AddEventContract.Event.OnDurationSelected(it)) },
                checkChange = { viewModel.setEvent(AddEventContract.Event.ToggleAllDay(it)) },
                timeError = state.timeError,
                focusManager = focusManager,
                onStartUpdate = {
                    viewModel.setEvent(AddEventContract.Event.OnStartEntered(it.first, it.second, it.third))
                },
                onEndUpdate = {
                    viewModel.setEvent(AddEventContract.Event.OnEndEntered(it.first, it.second, it.third))
                },
            )
        }

        else BodyText(
            "This event repeats. Changes to the title and location apply to all events in the series. " +
                "To change the time, edit the series in your calendar app.",
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))
        if (!state.isEditing && state.calendars.size > 1) {
            CalendarPicker(
                calendars = state.calendars,
                selectedId = state.calendarId,
                onSelected = { viewModel.setEvent(AddEventContract.Event.OnCalendarSelected(it)) }
            )
        } else if (!state.isEditing) {
            BodyText(
                "New events are added to the default calendar",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        if (state.saveFailed) {
            Spacer(modifier = Modifier.height(16.dp))
            BodyText(
                "Couldn't save the change. Check that the calendar is still available and try again.",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        if (state.isEditing) {
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(
                onClick = { viewModel.setEvent(AddEventContract.Event.OnDelete) },
                enabled = !state.isSaving,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                BodyText(
                    label = if (state.isRecurring) "Delete all events in series" else "Delete event",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        Spacer(modifier = Modifier.height(64.dp))
    }

    when (state.confirmation) {
        AddEventContract.Confirmation.DeleteEvent -> AppAlertDialog(
            title = if (state.isRecurring) "Delete series?" else "Delete event?",
            message = if (state.isRecurring) {
                "This event repeats. This deletes all events in the series."
            } else {
                "This removes the event from your calendar."
            },
            confirmButtonLabel = "Delete",
            negativeButtonLabel = "Cancel",
            onContinueRequest = { viewModel.setEvent(AddEventContract.Event.OnConfirm) },
            onNegativeRequest = { viewModel.setEvent(AddEventContract.Event.OnDismissConfirmation) }
        )

        AddEventContract.Confirmation.ChangeSeries -> AppAlertDialog(
            title = "Change series?",
            message = "This changes all events in the series.",
            confirmButtonLabel = "Change all",
            negativeButtonLabel = "Cancel",
            onContinueRequest = { viewModel.setEvent(AddEventContract.Event.OnConfirm) },
            onNegativeRequest = { viewModel.setEvent(AddEventContract.Event.OnDismissConfirmation) }
        )

        null -> {}
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CalendarPicker(
    calendars: List<CalendarEntity>,
    selectedId: String?,
    onSelected: (String) -> Unit,
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        BodyText("Calendar")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            calendars.forEach { calendar ->
                val selected = calendar.id == selectedId
                AssistChip(
                    onClick = { calendar.id?.let(onSelected) },
                    label = {
                        BodyText(
                            calendar.name,
                            color = if (selected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.tertiary
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    border = if (selected) BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null
                )
            }
        }
    }
}

@Composable
private fun Details(
    startDate: Long,
    startHours: Int,
    startMinutes: Int,
    endDate: Long,
    endHours: Int,
    endMinutes: Int,
    selectedDurationMinutes: Int?,
    allDay: Boolean,
    checkChange: (Boolean) -> Unit,
    onDurationSelected: (Int) -> Unit,
    onStartUpdate: (Triple<Long, Int, Int>) -> Unit,
    onEndUpdate: (Triple<Long, Int, Int>) -> Unit,
    timeError: String?,
    focusManager: FocusManager
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(MaterialTheme.colorScheme.secondaryContainer)
    ) {
        DetailsItemRow {
            BodyText("All Day")
            Spacer(modifier = Modifier.weight(1f))
            AppSwitch(checked = allDay, onCheckedChange = checkChange)
        }

        DetailsDivider()

        DateAndTime(
            label = "Starts",
            isChecked = allDay,
            date = startDate,
            hours = startHours,
            minutes = startMinutes,
            onTimeUpdate = onStartUpdate,
            focusManager = focusManager
        )

        DetailsDivider()
        DateAndTime(
            label = "Ends",
            isChecked = allDay,
            date = endDate,
            hours = endHours,
            minutes = endMinutes,
            onTimeUpdate = onEndUpdate,
            focusManager = focusManager,
        )

        if (!allDay) {
            DurationChips(
                selectedMinutes = selectedDurationMinutes,
                onDurationSelected = onDurationSelected,
                focusManager = focusManager,
            )
        }
        if (timeError != null) {
            Text(
                text = timeError,
                style = DMSans18Medium,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 4.dp, start = 16.dp, bottom = 8.dp)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DurationChips(
    selectedMinutes: Int?,
    onDurationSelected: (Int) -> Unit,
    focusManager: FocusManager
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        durationQuickPicks.forEach { duration ->
            val selected = selectedMinutes == duration
            AssistChip(
                modifier = Modifier.semantics { this.selected = selected },
                onClick = {
                    focusManager.clearFocus()
                    onDurationSelected(duration)
                },
                label = {
                    BodyText(
                        "${duration}m",
                        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary,
                    )
                },
                colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = if (selected) BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalCupertinoApi::class)
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
    val isAndroid = getPlatform().isAndroid
    val is24Hour = uses24HourClock()
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val selectedDate = formatTimestamp(date)
    val selectedTime = formatLocalTime(hours, minutes)

    DetailsItemRow {
        BodyText(label)
        Spacer(Modifier.weight(1f))
        TemporalDisplay(selectedDate) {
            showDatePicker = !showDatePicker
            showTimePicker = false
            focusManager.clearFocus()
        }

        if (!isChecked) {
            Spacer(modifier = Modifier.width(8.dp))
            TemporalDisplay(selectedTime) {
                showTimePicker = !showTimePicker
                showDatePicker = false
                focusManager.clearFocus()
            }
        }
    }

    AnimatedVisibility(
        visible = showDatePicker,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        if (isAndroid) {
            val pickerState = rememberDatePickerState(initialSelectedDateMillis = date)
            LaunchedEffect(date) {
                if (pickerState.selectedDateMillis != date) {
                    pickerState.selectedDateMillis = date
                    pickerState.displayedMonthMillis = date
                }
            }
            // Only writes from the control invoke callbacks; model synchronization uses the delegate.
            val controlledState = object : DatePickerState by pickerState {
                override var selectedDateMillis: Long?
                    get() = pickerState.selectedDateMillis
                    set(value) {
                        pickerState.selectedDateMillis = value
                        value?.let { onTimeUpdate(Triple(it, hours, minutes)) }
                    }
            }
            DatePicker(
                title = null,
                headline = null,
                state = controlledState,
                colors = DatePickerDefaults.colors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                showModeToggle = false,
            )
        } else {
            key(date) {
                val pickerState = rememberCupertinoDatePickerState(initialSelectedDateMillis = date)
                LaunchedEffect(pickerState) {
                    snapshotFlow { pickerState.selectedDateMillis }.drop(1).collect {
                        onTimeUpdate(Triple(it, hours, minutes))
                    }
                }
                CupertinoDatePicker(
                    state = pickerState,
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                )
            }
        }
    }

    AnimatedVisibility(
        visible = showTimePicker && !isChecked,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        if (isAndroid) {
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
                        onTimeUpdate(Triple(date, value, pickerState.minute))
                    }
                override var minute: Int
                    get() = pickerState.minute
                    set(value) {
                        pickerState.minute = value
                        onTimeUpdate(Triple(date, pickerState.hour, value))
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
                    modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally)
                )
            }
        } else {
            CupertinoTimeControl(hours, minutes, is24Hour) { hour, minute ->
                onTimeUpdate(Triple(date, hour, minute))
            }
        }

    }
}

@OptIn(ExperimentalCupertinoApi::class)
@Composable
private fun CupertinoTimeControl(
    hours: Int,
    minutes: Int,
    is24Hour: Boolean,
    onTimeUpdate: (Int, Int) -> Unit,
) {
    val modelTime = hours to minutes
    var seed by remember { mutableStateOf(modelTime) }
    var lastEmitted by remember { mutableStateOf(modelTime) }
    val currentModel by rememberUpdatedState(modelTime)
    val onUpdate by rememberUpdatedState(onTimeUpdate)
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
            modifier = Modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
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
    placeholder: String, content: String, onUpdate: (String) -> Unit, focusRequester: FocusRequester
) {
    OutlinedTextField(
        value = content,
        onValueChange = {
            onUpdate.invoke(it)
        },
        placeholder = {
            BodyText(
                label = placeholder, color = MaterialTheme.colorScheme.surfaceVariant
            )
        },
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
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
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
private fun Header(label: String, onCancel: () -> Unit, onSave: () -> Unit, isEnabled: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BodyText(
            label = "Cancel",
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.clickableWithOpaqueText(onCancel)
        )
        NewEventHeader(label)
        TextButton(onClick = onSave, enabled = isEnabled) {
            BodyText(
                label = "Save",
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