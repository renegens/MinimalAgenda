package presentation.components.months

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import data.MonthData
import extensions.uniqueMonthID
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import use_case.getDaysInMonthWithZeroes

fun LazyGridScope.header(
    content: @Composable LazyGridItemScope.() -> Unit
) {
    item(span = { GridItemSpan(this.maxLineSpan) }, content = content)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MonthGrid(
    monthData: MonthData,
    isCurrentMonth: Boolean,
    today: LocalDate
) {
    MonthHeader(
        month = monthData.month.name,
        year = monthData.year,
        color = Color.White,
        isCurrentMonth
    )
    DaysInWeek()
    CalendarDays(monthData = monthData, today = today)
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CalendarDays(monthData: MonthData, today: LocalDate) {
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
                }else{
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
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(2.dp)
                        .size(40.dp)
                        .background(
                            color = if (selectedDay != null) Color(0xFFFFC107) else Color.Transparent,
                            shape = CircleShape
                        )
                ) {
                    TextButton(onClick = {}, modifier = Modifier.height(44.dp).width(44.dp)) {
                        Text(
                            text = day.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Start,
                            color = Color.White
                        )
                    }
                }

            }
        }
    }
}
