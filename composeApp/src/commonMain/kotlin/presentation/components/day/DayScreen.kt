package presentation.components.day

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import presentation.theme.Body
import presentation.theme.Header1
import presentation.theme.Header2
import use_case.generateDayEntries
import use_case.getDaysInMonthWithZeroes

@Composable
fun DayScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        val currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val dates = generateDayEntries(currentDate)

        items(dates.size) { index ->
            val current = dates[index]
            Divider(color = Color.White)
            Header(current.month.name)
            HeaderSecondary(current.dayOfWeek.name, current.dayOfMonth)
            Entries()
        }

    }
}

@Composable
fun Header(month: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(month, style = Header2, color = Color.White)
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