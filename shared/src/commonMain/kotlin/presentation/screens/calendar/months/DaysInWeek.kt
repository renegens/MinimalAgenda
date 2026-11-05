package presentation.screens.calendar.months

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import domain.model.DayInWeek
import kotlinx.datetime.LocalDate
import presentation.theme.DMSans24Medium60

@Composable
fun DaysInWeek(today: LocalDate, isCurrentMonth: Boolean) {
    val todayIndex = today.dayOfWeek.ordinal

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        DayInWeek.entries.forEachIndexed { index, daysInWeek ->
            Box(modifier = Modifier.width(48.dp).height(15.dp)) {
                Text(
                    modifier = Modifier.wrapContentHeight(unbounded = true),
                    text = daysInWeek.firstLetter,
                    style = DMSans24Medium60,
                    color = if (index == todayIndex && isCurrentMonth) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary,
                )
            }
        }
    }
}