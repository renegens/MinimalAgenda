package platform

import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DateTimeFormattingTest {
    @Test
    fun compactDateFollowsMonthFirstDayFirstAndYearFirstPatterns() {
        assertEquals("11.5", compactDayAndMonth(5, 11, "M/d/yy"))
        assertEquals("5.11", compactDayAndMonth(5, 11, "dd.MM.yyyy"))
        assertEquals("11.5", compactDayAndMonth(5, 11, "yyyy/M/d"))
        assertEquals("11.5", compactDayAndMonth(5, 11, "yyyy年M月d日"))
        assertEquals("5.11", compactDayAndMonth(5, 11, "d L y"))
    }

    @Test
    fun quotedDatePatternTextDoesNotChangeTheOrder() {
        assertEquals("5.11", compactDayAndMonth(5, 11, "'Month' d 'de' MMMM"))
        assertEquals("11.5", compactDayAndMonth(5, 11, "'day' L 'd' d"))
        assertEquals("dM", unquotedPatternLetters("d 'it''s M' M"))
    }

    @Test
    fun clockPreferenceUsesHourSymbolsRatherThanFormattedSuffixes() {
        assertTrue(patternUses24HourClock("HH:mm"))
        assertTrue(patternUses24HourClock("kk:mm 'h'"))
        assertFalse(patternUses24HourClock("h:mm a"))
        assertFalse(patternUses24HourClock("a K:mm"))
        assertFalse(patternUses24HourClock("'HH' h:mm 'k' a"))
    }

    @Test
    fun pickerDateUsesUtcCalendarDateRatherThanShiftingToLocalYesterday() {
        val leapDay = LocalDate(2024, 2, 29)
        assertEquals(formatLocalDate(leapDay), formatTimestamp(leapDay.toEpochDays() * 86_400_000L))
        val yearBoundary = LocalDate(2027, 1, 1)
        assertEquals(formatLocalDate(yearBoundary), formatTimestamp(yearBoundary.toEpochDays() * 86_400_000L))
    }
}
