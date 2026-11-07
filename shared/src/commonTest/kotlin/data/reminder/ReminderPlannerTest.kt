package data.reminder

import kotlin.test.Test
import kotlin.test.assertEquals

class ReminderPlannerTest {
    private val reminder = ScheduledReminder("1/0", "t", "b", triggerAtSeconds = 900, offsetMinutes = 5, startSeconds = 1200, endSeconds = 2000)
    private val same = EventTimes(1200, 2000)

    @Test fun instanceUnchanged_shows() =
        assertEquals(ReminderOutcome.Show, ReminderPlanner.decide(reminder, same, EventLookup(same, false), 900))

    @Test fun deleted_drops() =
        assertEquals(ReminderOutcome.Drop, ReminderPlanner.decide(reminder, null, null, 900))

    @Test fun recurringInstanceGone_drops() =
        assertEquals(ReminderOutcome.Drop, ReminderPlanner.decide(reminder, null, EventLookup(EventTimes(5000, 6000), true), 900))

    @Test fun moved_reschedulesWithOffset() {
        val moved = EventTimes(5000, 6000)
        assertEquals(
            ReminderOutcome.Reschedule(5000 - 300, moved),
            ReminderPlanner.decide(reminder, null, EventLookup(moved, false), 900),
        )
    }

    @Test fun movedAndAlreadyAdapted_shows() {
        val adapted = reminder.copy(startSeconds = 5000, endSeconds = 6000)
        assertEquals(ReminderOutcome.Show, ReminderPlanner.decide(adapted, null, EventLookup(EventTimes(5000, 6000), false), 4700))
    }

    @Test fun movedToPast_dueNowReschedule() {
        val moved = EventTimes(1000, 1500)
        val outcome = ReminderPlanner.decide(reminder, null, EventLookup(moved, false), 950)
        assertEquals(ReminderOutcome.Reschedule(700, moved), outcome)
    }

    @Test fun ended_drops() =
        assertEquals(ReminderOutcome.Drop, ReminderPlanner.decide(reminder, same, EventLookup(same, false), 2000 + 301))

    @Test fun withinGraceAfterEnd_shows() =
        assertEquals(ReminderOutcome.Show, ReminderPlanner.decide(reminder, same, EventLookup(same, false), 2000 + 299))

    @Test fun zeroLengthEventAtStart_shows() {
        val r = reminder.copy(endSeconds = 1200)
        val t = EventTimes(1200, 1200)
        assertEquals(ReminderOutcome.Show, ReminderPlanner.decide(r, t, EventLookup(t, false), 1210))
    }

    @Test fun instanceStartShifted_reschedules() {
        val shifted = EventTimes(1300, 2100)
        assertEquals(
            ReminderOutcome.Reschedule(1000, shifted),
            ReminderPlanner.decide(reminder, shifted, EventLookup(shifted, false), 900),
        )
    }

    @Test fun deliveryUsesTheCurrentPrivacySettingAndCurrentEventTitle() {
        val old = reminder.copy(body = "Sensitive old title")
        assertEquals("You have an upcoming event.", old.bodyForDelivery(true, "New title"))
        assertEquals("New title", old.bodyForDelivery(false, "New title"))
        assertEquals("Sensitive old title", old.bodyForDelivery(false))
    }
}
