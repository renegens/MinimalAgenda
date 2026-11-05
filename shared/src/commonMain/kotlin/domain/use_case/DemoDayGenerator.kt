package domain.use_case

import domain.model.AgendaEvent
import domain.model.FormattedTime
import extensions.today
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import platform.uses24HourClock
import kotlin.time.ExperimentalTime

fun generateOnboardingDemoEvents(): List<Pair<FormattedTime, String>> {
    // 5 Events: Mix of "Boring" and "Secret" to encourage hiding.
    return if (uses24HourClock()) {
        // 24-Hour Format (e.g., 14:00)
        listOf(
            Pair(FormattedTime("09", "00"), "👋 Try Hiding This Event!"),
            Pair(FormattedTime("10", "30"), "Weekly Team Meeting"),
            Pair(FormattedTime("12", "00"), "🤫 Interview with Competitor"),
            Pair(FormattedTime("14", "00"), "Deep Work"),
            Pair(FormattedTime("17", "30"), "💍 Buy Engagement Ring")
        )
    } else {
        // 12-Hour Format (e.g., 02:00 PM)
        listOf(
            Pair(FormattedTime("09", "00", "AM"), "👋 Try Hiding This Event!"),
            Pair(FormattedTime("10", "30", "AM"), "Weekly Team Meeting"),
            Pair(FormattedTime("12", "00", "PM"), "🤫 Interview with Competitor"),
            Pair(FormattedTime("02", "00", "PM"), "Deep Work"),
            Pair(FormattedTime("05", "30", "PM"), "💍 Buy Engagement Ring")
        )
    }
}

@OptIn(ExperimentalTime::class)
fun generateDemoAgendaEvents(): List<AgendaEvent> {
    val uses24HourClock = uses24HourClock()
    val today = today

    return listOf(
        // Event 1: The Hook
        AgendaEvent(
            id = "1",
            title = "👋 Tap here to hide me.",
            location = "",
            startTimeInUTC = today.atTime(9, 0).toInstant(TimeZone.UTC).epochSeconds,
            endTimeInUTC = today.atTime(10, 0).toInstant(TimeZone.UTC).epochSeconds,
            allDay = false,
            epochDay = today.toEpochDays(),
            isRecurring = false,
            isHidden = false,
            startLocalDateTime = today.atTime(9, 0),
            endLocalDateTime = today.atTime(10, 0),
            uses24HourClock = uses24HourClock,
            isMultiDay = false
        ),
        // Event 2: Safe/Boring
        AgendaEvent(
            id = "2",
            title = "Weekly Team Meeting",
            location = "Office",
            startTimeInUTC = today.atTime(10, 30).toInstant(TimeZone.UTC).epochSeconds,
            endTimeInUTC = today.atTime(11, 30).toInstant(TimeZone.UTC).epochSeconds,
            allDay = false,
            epochDay = today.toEpochDays(),
            isRecurring = false,
            isHidden = false,
            startLocalDateTime = today.atTime(10, 30),
            endLocalDateTime = today.atTime(11, 30),
            uses24HourClock = uses24HourClock,
            isMultiDay = false
        ),
        // Event 3: Secret (Privacy Trigger)
        AgendaEvent(
            id = "3",
            title = "🤫 Interview with Competitor",
            location = "Online",
            startTimeInUTC = today.atTime(12, 0).toInstant(TimeZone.UTC).epochSeconds,
            endTimeInUTC = today.atTime(13, 0).toInstant(TimeZone.UTC).epochSeconds,
            allDay = false,
            epochDay = today.toEpochDays(),
            isRecurring = false,
            isHidden = false,
            startLocalDateTime = today.atTime(12, 0),
            endLocalDateTime = today.atTime(13, 0),
            uses24HourClock = uses24HourClock,
            isMultiDay = false
        ),
        // Event 4: Neutral filler
        AgendaEvent(
            id = "4",
            title = "Deep Work",
            location = "",
            startTimeInUTC = today.atTime(14, 0).toInstant(TimeZone.UTC).epochSeconds,
            endTimeInUTC = today.atTime(16, 0).toInstant(TimeZone.UTC).epochSeconds,
            allDay = false,
            epochDay = today.toEpochDays(),
            isRecurring = false,
            isHidden = false,
            startLocalDateTime = today.atTime(14, 0),
            endLocalDateTime = today.atTime(16, 0),
            uses24HourClock = uses24HourClock,
            isMultiDay = false
        ),
        // Event 5: Secret (Personal)
        AgendaEvent(
            id = "5",
            title = "💍 Buy Engagement Ring",
            location = "Jeweler",
            startTimeInUTC = today.atTime(17, 30).toInstant(TimeZone.UTC).epochSeconds,
            endTimeInUTC = today.atTime(18, 30).toInstant(TimeZone.UTC).epochSeconds,
            allDay = false,
            epochDay = today.toEpochDays(),
            isRecurring = false,
            isHidden = false,
            startLocalDateTime = today.atTime(17, 30),
            endLocalDateTime = today.atTime(18, 30),
            uses24HourClock = uses24HourClock,
            isMultiDay = false
        ),
        AgendaEvent(
            id = "6",
            title = "\uD83D\uDCC5 Connect to existing calendar",
            location = "Settings",
            startTimeInUTC = today.atTime(20, 30).toInstant(TimeZone.UTC).epochSeconds,
            endTimeInUTC = today.atTime(21, 30).toInstant(TimeZone.UTC).epochSeconds,
            allDay = false,
            epochDay = today.toEpochDays(),
            isRecurring = false,
            isHidden = false,
            startLocalDateTime = today.atTime(20, 30),
            endLocalDateTime = today.atTime(21, 30),
            uses24HourClock = uses24HourClock,
            isMultiDay = false
        )
    )
}