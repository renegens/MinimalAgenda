package presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import domain.model.AgendaMonthYear
import domain.model.AgendaYear
import extensions.toShortName
import kotlinx.datetime.LocalDate
import presentation.theme.DMSans18Medium
import presentation.theme.DMSansBlack250

@Composable
fun MonthHeader(monthYear: AgendaMonthYear, modifier: Modifier, currentDate: LocalDate) {
    val label = buildAnnotatedString {
        withStyle(style = SpanStyle(color = ColorPrimary(monthYear.isCurrent(currentDate)))) {
            append(monthYear.agendaMonth.nameRes.toShortName())
        }
        withStyle(style = SpanStyle(color = ColorSecondary(monthYear.isCurrent(currentDate)))) {
            append(monthYear.agendaYear.lastTwoDigits)
        }
    }
    HeaderLarge(modifier = modifier, label = label)
}

@Composable
fun DayHeader(day: String, dayInMonth: Int, month: Int, isToday: Boolean) {
    val label = buildAnnotatedString {
        withStyle(style = SpanStyle(color = ColorPrimary(isToday))) {
            append(day)
        }
        withStyle(style = SpanStyle(color = ColorSecondary(isToday))) {
            append("$dayInMonth.$month")
        }
    }
    HeaderLarge(label = label)
}

@Composable
fun YearHeader(agendaYear: AgendaYear, currentDate: LocalDate) {
    val label = buildAnnotatedString {
        withStyle(style = SpanStyle(color = ColorSecondary(agendaYear.isCurrent(currentDate)))) {
            append(agendaYear.firstTwoDigits)
        }
        withStyle(style = SpanStyle(color = ColorPrimary(agendaYear.isCurrent(currentDate)))) {
            append(agendaYear.lastTwoDigits)
        }
    }
    HeaderLarge(label = label)
}

@Composable
private fun HeaderLarge(modifier: Modifier = Modifier, label: AnnotatedString) {
    Box(Modifier.height(84.dp).fillMaxWidth().padding(horizontal = 16.dp)) {
        Text(
            style = DMSansBlack250,
            text = label,
            modifier = Modifier.wrapContentHeight(unbounded = true).then(modifier)
        )
    }
}

@Composable
private fun ColorPrimary(isCurrent: Boolean): Color {
    return if (isCurrent) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.secondary
    }
}

@Composable
private fun ColorSecondary(isCurrent: Boolean): Color {
    return if (isCurrent) {
        MaterialTheme.colorScheme.tertiary
    } else {
        MaterialTheme.colorScheme.secondary
    }
}

@Composable
fun SettingsHeader(label: String) {
    Box(Modifier.height(100.dp).fillMaxWidth()) {
        Text(
            style = DMSansBlack250,
            text = label,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.wrapContentHeight(unbounded = true)
        )
    }
}

@Composable
fun NewEventHeader(label: String) {
    val label = buildAnnotatedString {
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
            append(label)
        }
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.tertiary)) {
            append("Event")
        }
    }
    Text(
        fontSize = 24.sp,
        style = DMSans18Medium,
        text = label,
        modifier = Modifier.padding(bottom = 4.dp).wrapContentHeight(unbounded = true)
    )

}