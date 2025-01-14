package presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import domain.use_case.generateOnboardingDemoEvents
import extensions.today
import kotlinx.coroutines.delay
import presentation.screens.calendar.day.EmptyMessage
import presentation.theme.AppCornerShape
import presentation.theme.DMSans18Bold60
import presentation.theme.DMSans18Medium
import presentation.theme.InvertedAppTheme

@Composable
fun DemonstrationDay() {
    var calendarEntries by remember {
        mutableStateOf(generateOnboardingDemoEvents())
    }

    val today by remember { mutableStateOf(today) }
    LaunchedEffect(Unit) {
        delay(2000) // Delay before removing each event
        while (calendarEntries.isNotEmpty()) {
            delay(1000) // Delay before removing each event
            calendarEntries = calendarEntries.drop(1) // Remove the first event
        }
    }
    Column(
        modifier = Modifier.height(380.dp)
            .clip(shape = AppCornerShape)
    ) {
        InvertedAppTheme {
            Column(modifier = Modifier.wrapContentHeight()) {
                Spacer(modifier = Modifier.height(24.dp))
                DayHeader(
                    day = today.dayOfWeek.toString().substring(0, 3),
                    dayInMonth = today.dayOfMonth,
                    month = today.monthNumber,
                    isToday = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                if (calendarEntries.isEmpty()) {
                    Spacer(modifier = Modifier.weight(1F))
                    EmptyMessage()
                    Spacer(modifier = Modifier.weight(1F))
                }
                if (calendarEntries.isNotEmpty()) {


                    LazyColumn {
                        items(calendarEntries, key = { it.second }) {
                            Row(
                                modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 28.dp)
                                    .wrapContentHeight()
                                    .animateItem()
                                    .padding(horizontal = 16.dp)
                            ) {

                                val time = it.first

                                val rowWidth = if (time.ampm == null) {
                                    100
                                } else {
                                    120
                                }

                                Row(
                                    modifier = Modifier.width(rowWidth.dp).padding(end = 8.dp)
                                ) {
                                    Text(
                                        modifier = Modifier.weight(0.6F),
                                        text = time.hours,
                                        style = DMSans18Bold60,
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                    Text(
                                        modifier = Modifier.weight(1F),
                                        text = ":${time.minutes}",
                                        style = DMSans18Bold60,
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                    time.ampm?.let {
                                        Text(
                                            modifier = Modifier.weight(1F),
                                            text = it,
                                            style = DMSans18Bold60,
                                            color = MaterialTheme.colorScheme.tertiary
                                        )
                                    }
                                }
                                Text(
                                    modifier = Modifier.padding(start = 4.dp),
                                    text = it.second,
                                    style = DMSans18Medium,
                                    color = MaterialTheme.colorScheme.tertiary,
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}