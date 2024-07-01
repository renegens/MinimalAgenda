package presentation.components.day

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import data.DayData
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import presentation.theme.Body
import presentation.theme.Header1
import presentation.theme.Header2


@Composable
fun DayScreen(days: List<DayData>, today: LocalDate, todayIndex: Int) {
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = todayIndex + 1, initialFirstVisibleItemScrollOffset = 10)
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        itemsIndexed(
            items = days,
            key = { index, day -> day.epochDay }
        ) { index, day ->
            Divider(color = Color.White)
            Header(day.month.name, isToday = day.epochDay == today.toEpochDays())
            HeaderSecondary(day.dayOfWeek.name,day.dayOfMonth)
            Entries()
        }
    }

    coroutineScope.launch {
       //listState.scrollToItem(todayIndex)
    }
}

@Composable
fun Header(month: String, isToday: Boolean) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(month, style = Header2, color = if (isToday) Color.Yellow else Color.White)
        Text("Today", style = Body, color = Color.White)
    }
}

@Composable
fun HeaderSecondary(day: String, dayOfMonth: Int) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(day, style = Header2, color = Color.White)
        Text(dayOfMonth.toString(), style = Header1, color = Color.White)
    }
}

@Composable
fun Entries() {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text("10.55", style = Body, color = Color.White)
        Text("Meeting", style = Body, color = Color.White)
    }
    Row(modifier = Modifier.fillMaxWidth()) {
        Text("10.55", style = Body, color = Color.White)
        Text("Meeting", style = Body, color = Color.White)
    }
    Row(modifier = Modifier.fillMaxWidth()) {
        Text("10.55", style = Body, color = Color.White)
        Text("Meeting", style = Body, color = Color.White)
    }
    Row(modifier = Modifier.fillMaxWidth()) {
        Text("10.55", style = Body, color = Color.White)
        Text("Meeting", style = Body, color = Color.White)
    }
}