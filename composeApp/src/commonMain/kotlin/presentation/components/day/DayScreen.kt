package presentation.components.day

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key.Companion.L
import androidx.compose.ui.unit.dp
import domain.model.AgendaDay
import extensions.millisToTime
import extensions.today
import extensions.todayEpochDay
import kotlinx.datetime.LocalDate
import presentation.components.SectionDivider

@Composable
fun DayScreen(
    days: List<AgendaDay>,
    onNowClicked: Boolean,
    scrollToDay: Int
) {
    val todayIndex by remember { mutableStateOf(days.indexOfFirst { it.epochDay == todayEpochDay }) }

    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = todayIndex + 1, initialFirstVisibleItemScrollOffset = 5
    )

    LaunchedEffect(onNowClicked) {
        listState.animateScrollToItem(todayIndex)
    }

    LaunchedEffect(scrollToDay) {
        if (scrollToDay > 0){
            listState.scrollToItem(scrollToDay)
        }
    }

    LazyColumn(
        state = listState, modifier = Modifier.fillMaxSize()
    ) {

        itemsIndexed(items = days, key = { index, day -> day.epochDay }) { index, day ->
            val isToday = day.epochDay == today.toEpochDays()
            Header(
                day = day.shortDay, dayInMonth = day.dayOfMonth, isToday = isToday
            )
            Entries(day)
            SectionDivider(isCurrent = isToday)
        }
    }
}

@Composable
fun Header(day: String, dayInMonth: Int, isToday: Boolean) {
    Text(
        "$day$dayInMonth",
        style = MaterialTheme.typography.headlineLarge,
        color = if (isToday) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
    )
}

@Composable
fun Entries(day: AgendaDay) {
    if (day.events.isEmpty()) {
        Spacer(modifier = Modifier.height(56.dp))
    }

    day.events.filter { it.epochDay == day.epochDay }.forEach {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                it.startTime?.millisToTime() ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                it.title ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}