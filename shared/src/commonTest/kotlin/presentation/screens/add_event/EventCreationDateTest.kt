package presentation.screens.add_event

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

class EventCreationDateTest {
    private val now = LocalDateTime(2026, 11, 5, 10, 37, 45)

    private fun assertStart(date: LocalDate, hour: Int, state: AddEventContract.State) {
        assertEquals(date.toPickerMillis(), state.startDate)
        assertEquals(hour, state.startHours)
        assertEquals(0, state.startMinutes)
    }

    @Test
    fun todayStartsAtNextFullHour() {
        assertStart(now.date, 11, initialEditorState(EventEditorRequest.Add(now.date), now))
    }

    @Test
    fun evenAnExactHourAdvancesToTheNextHour() {
        val exactHour = LocalDateTime(2026, 11, 5, 10, 0)
        assertStart(exactHour.date, 11, initialEditorState(EventEditorRequest.Add(exactHour.date), exactHour))
    }

    @Test
    fun otherDatesStartAtNineIncludingDatesInThePast() {
        for (date in listOf(LocalDate(2026, 11, 4), LocalDate(2026, 11, 6), LocalDate(2027, 2, 1))) {
            assertStart(date, 9, initialEditorState(EventEditorRequest.Add(date), now))
        }
    }

    @Test
    fun menuWithoutSelectedDateUsesTodayAndTheNextHour() {
        assertStart(now.date, 11, initialEditorState(EventEditorRequest.Add(), now))
    }

    @Test
    fun lastHourRollsThroughMonthAndYearBoundaries() {
        for ((late, tomorrow) in listOf(
            LocalDateTime(2026, 11, 30, 23, 50) to LocalDate(2026, 12, 1),
            LocalDateTime(2026, 12, 31, 23, 0) to LocalDate(2027, 1, 1),
            LocalDateTime(2024, 2, 28, 23, 59) to LocalDate(2024, 2, 29),
        )) {
            assertStart(tomorrow, 0, initialEditorState(EventEditorRequest.Add(late.date), late))
            assertStart(tomorrow, 0, initialEditorState(EventEditorRequest.Add(), late))
        }
    }
}
