package presentation.components.months

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import data.MonthData
import extensions.uniqueMonthID
import kotlinx.datetime.LocalDate
import presentation.components.SectionDivider

@Composable
fun MonthGrid(
    monthData: MonthData, isCurrentMonth: Boolean, today: LocalDate
) {
    Spacer(modifier = Modifier.height(16.dp))
    MonthHeader(
        monthData = monthData, isCurrentMonth = isCurrentMonth
    )
    Spacer(modifier = Modifier.height(16.dp))
    DaysInWeek(today = today, isCurrentMonth = isCurrentMonth)
    CalendarDays(monthData = monthData, today = today, onDayClicked = {})
    Spacer(modifier = Modifier.height(16.dp))
    SectionDivider(isCurrent = today.month == monthData.month)
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CalendarDays(monthData: MonthData, today: LocalDate, onDayClicked: () -> Unit) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        maxItemsInEachRow = 7
    ) {
        var selectedDay: Int? = null

        monthData.days.forEachIndexed { index, i ->
            val day = monthData.days[index]
            if (monthData.id == today.uniqueMonthID()) {
                if (today.dayOfMonth == day) {
                    selectedDay = today.dayOfMonth
                } else {
                    selectedDay = null
                }
            }

            if (day == 0) {
                Text(
                    text = day.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.height(44.dp).width(44.dp),
                    color = Color.Transparent
                )
            } else {
                TextButton(
                    onClick = { onDayClicked.invoke() },
                    modifier = Modifier.height(44.dp).width(44.dp)
                ) {
                    Text(
                        text = day.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Start,
                        color = if (selectedDay == day) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}