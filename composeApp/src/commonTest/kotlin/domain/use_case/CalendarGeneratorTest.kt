package domain.use_case

import domain.model.AgendaMonth
import domain.model.DayInWeek
import extensions.today
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

// CalendarGenerator reads "today" from the system clock, so expectations are derived from the same value.
class CalendarGeneratorTest {

    private val calendar = CalendarGenerator().calendar

    @Test
    fun startsOnFirstOfJanuaryTwoYearsBeforeToday() {
        val first = calendar.first()
        assertEquals(LocalDate(today.year - 2, 1, 1).toEpochDays(), first.epochDay)
        assertEquals(1, first.dayOfMonth)
        assertEquals(AgendaMonth.January, first.agendaMonthYear.agendaMonth)
        assertEquals(today.year - 2, first.agendaMonthYear.year)
    }

    @Test
    fun endsOnLastOfDecemberTwoYearsAfterToday() {
        val last = calendar.last()
        assertEquals(LocalDate(today.year + 2, 12, 31).toEpochDays(), last.epochDay)
        assertEquals(31, last.dayOfMonth)
        assertEquals(AgendaMonth.December, last.agendaMonthYear.agendaMonth)
        assertEquals(today.year + 2, last.agendaMonthYear.year)
    }

    @Test
    fun coversFiveYearsOfConsecutiveDays() {
        val expected = LocalDate(today.year + 2, 12, 31).toEpochDays() -
            LocalDate(today.year - 2, 1, 1).toEpochDays() + 1
        assertEquals(expected.toInt(), calendar.size)
        // five consecutive years contain 1 or 2 leap days
        assertTrue(calendar.size == 1826 || calendar.size == 1827, "size was ${calendar.size}")
        calendar.zipWithNext().forEach { (a, b) -> assertEquals(a.epochDay + 1, b.epochDay) }
    }

    @Test
    fun containsToday() {
        val day = calendar.single { it.epochDay == today.toEpochDays() }
        assertEquals(today.day, day.dayOfMonth)
        assertEquals(today.month.ordinal + 1, day.agendaMonthYear.agendaMonth.number)
    }

    @Test
    fun dayFieldsMatchTheDate() {
        calendar.forEach { day ->
            val date = LocalDate.fromEpochDays(day.epochDay)
            assertEquals(date.day, day.dayOfMonth)
            assertEquals(date.year, day.agendaMonthYear.year)
            assertEquals(date.month.ordinal + 1, day.agendaMonthYear.agendaMonth.number)
            assertEquals(date.dayOfWeek.isoDayNumber, day.dayInWeek.position)
        }
    }

    @Test
    fun knownDayOfWeek() {
        // 2024-01-01 was a Monday; 2024-02-29 was a Thursday (only if in range).
        val jan1 = calendar.firstOrNull { it.epochDay == LocalDate(2024, 1, 1).toEpochDays() }
        jan1?.let { assertEquals(DayInWeek.Monday, it.dayInWeek) }
        val leap = calendar.firstOrNull { it.epochDay == LocalDate(2024, 2, 29).toEpochDays() }
        leap?.let { assertEquals(DayInWeek.Thursday, it.dayInWeek) }
    }

    @Test
    fun leapDayExistsOnlyInLeapYears() {
        for (year in (today.year - 2)..(today.year + 2)) {
            val isLeap = (year % 4 == 0 && year % 100 != 0) || year % 400 == 0
            val feb29 = calendar.firstOrNull {
                it.agendaMonthYear.year == year &&
                    it.agendaMonthYear.agendaMonth == AgendaMonth.February && it.dayOfMonth == 29
            }
            assertEquals(isLeap, feb29 != null, "year $year")
            val feb28 = calendar.firstOrNull {
                it.agendaMonthYear.year == year &&
                    it.agendaMonthYear.agendaMonth == AgendaMonth.February && it.dayOfMonth == 28
            }
            assertNotNull(feb28)
        }
    }

    @Test
    fun dayOfMonthResetsExactlyWhenMonthChanges() {
        calendar.zipWithNext().forEach { (a, b) ->
            val monthChanged = a.agendaMonthYear != b.agendaMonthYear
            assertEquals(monthChanged, b.dayOfMonth == 1)
            if (!monthChanged) assertEquals(a.dayOfMonth + 1, b.dayOfMonth)
        }
    }
}
