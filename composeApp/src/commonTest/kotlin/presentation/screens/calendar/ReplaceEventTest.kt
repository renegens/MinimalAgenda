package presentation.screens.calendar

import domain.model.DayListViewType
import kotlinx.datetime.LocalDate
import testutil.agendaEvent
import testutil.at
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class ReplaceEventTest {
    private val d = LocalDate(2024, 6, 10)
    private val a = agendaEvent(id = "a", start = at(d, 9))
    private val b = agendaEvent(id = "b", start = at(d, 10))

    @Test
    fun replacesExistingEventInPlace() {
        val items = listOf(DayListViewType.Event(a), DayListViewType.Event(b))
        val hiddenB = b.copy(isHidden = true)
        val result = replaceEvent(items, b, hiddenB)
        assertEquals(listOf(a, hiddenB), result.map { (it as DayListViewType.Event).event })
    }

    @Test
    fun missingEventLeavesListUnchanged() {
        val items = listOf(DayListViewType.Event(a))
        assertEquals(items, replaceEvent(items, b, b.copy(isHidden = true)))
        assertSame(items, replaceEvent(items, b, b))
    }
}
