package presentation.screens.calendar.years

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import domain.model.AgendaDay
import domain.model.AgendaEvent
import domain.model.AgendaMonthYear
import domain.model.AppThemeMode
import extensions.getInRangeOf
import extensions.scrollToNowIfNeeded
import kotlinx.datetime.LocalDate
import presentation.components.PaperBackground
import presentation.components.SectionDivider
import presentation.components.YearHeader

@Composable
fun YearsScreen(
    appThemeMode: AppThemeMode,
    days: List<AgendaDay>,
    events: List<AgendaEvent>,
    currentDate: LocalDate,
    showAll: Boolean,
    onNowClicked: Boolean,
    scrollToYear: Int,
    onMonthClicked: (AgendaMonthYear) -> Unit
) {

    if (days.isEmpty()) return

    val years by remember {
        mutableStateOf(days.groupBy { it.agendaMonthYear.agendaYear })
    }

    val todayIndex by mutableStateOf(
        years.keys.indexOfFirst { it.isCurrent(currentDate) }
    )

    val listState = rememberLazyListState(initialFirstVisibleItemIndex = todayIndex)

    LaunchedEffect(todayIndex) {
        listState.scrollToNowIfNeeded(todayIndex)
    }

    LaunchedEffect(onNowClicked) {
        listState.scrollToNowIfNeeded(todayIndex, shouldAnimate = true)
    }

    LaunchedEffect(scrollToYear) {
        if (scrollToYear > 0) {
            listState.scrollToItem(scrollToYear)
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
    ) {
        itemsIndexed(items = years.keys.toList(), key = { _, year -> year.year }) { index, year ->
            PaperBackground(appThemeMode = appThemeMode) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.height(24.dp))
                    YearHeader(agendaYear = year, currentDate = currentDate)
                    Spacer(modifier = Modifier.height(16.dp))
                    Months(
                        yearEvents = events.getInRangeOf(year),
                        year = year.year,
                        currentDate = currentDate,
                        onMonthClicked = onMonthClicked,
                        showAll = showAll
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    SectionDivider(isCurrent = year.year == currentDate.year)
                }
            }
        }
    }
}