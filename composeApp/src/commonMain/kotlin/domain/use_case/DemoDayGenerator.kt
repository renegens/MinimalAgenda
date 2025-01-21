package domain.use_case

import domain.model.AgendaEvent
import domain.model.FormattedTime
import extensions.today
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import platform.uses24HourClock

fun generateOnboardingDemoEvents(): List<Pair<FormattedTime, String>> {
    return if (uses24HourClock()) {
        listOf(
            Pair(FormattedTime("06", "30", "AM"), "Morning Routine"),
            Pair(FormattedTime("08", "45", "AM"), "Team Standup"),
            Pair(FormattedTime("10", "00", "AM"), "Project Review Meeting"),
            Pair(FormattedTime("02", "00", "PM"), "Client Call"),
            Pair(FormattedTime("03", "15", "PM"), "Strategy Planning Session"),
            Pair(FormattedTime("05", "30", "PM"), "Wrap-up Meeting"),
            Pair(FormattedTime("08", "45", "PM"), "Personal Reading Time")
        )
    } else {
        listOf(
            Pair(FormattedTime("06", "30"), "Morning Routine"),
            Pair(FormattedTime("08", "45"), "Team Standup"),
            Pair(FormattedTime("10", "00"), "Project Review Meeting"),
            Pair(FormattedTime("14", "00"), "Client Call"),
            Pair(FormattedTime("15", "15"), "Strategy Planning Session"),
            Pair(FormattedTime("17", "30"), "Wrap-up Meeting"),
            Pair(FormattedTime("20", "45"), "Personal Reading Time")
        )
    }
}

fun generateDemoAgendaEvents(): List<AgendaEvent> {
    val uses24HourClock = uses24HourClock()
    val today = today

    return listOf(
        AgendaEvent(
            id = "1",
            title = "Morning Routine",
            location = "",
            startTimeInUTC = today.atTime(6, 30)
                .toInstant(TimeZone.UTC).epochSeconds,
            endTimeInUTC = today.atTime(7, 30)
                .toInstant(TimeZone.UTC).epochSeconds,
            allDay = false,
            epochDay = today.toEpochDays(),
            isRecurring = false,
            isHidden = false,
            startLocalDateTime = today.atTime(6, 30),
            endLocalDateTime = today.atTime(7, 30), // Assumed 1-hour duration
            uses24HourClock = uses24HourClock,
            isMultiDay = false
        ),
        AgendaEvent(
            id = "2",
            title = "Team Standup",
            location = "",
            startTimeInUTC = today.atTime(6, 45)
                .toInstant(TimeZone.UTC).epochSeconds,
            endTimeInUTC = today.atTime(7, 15)
                .toInstant(TimeZone.UTC).epochSeconds,
            allDay = false,
            epochDay = today.toEpochDays(),
            isRecurring = false,
            isHidden = false,
            startLocalDateTime = today.atTime(8, 45),
            endLocalDateTime = today.atTime(9, 15), // Assumed 30-minute duration
            uses24HourClock = uses24HourClock,
            isMultiDay = false
        ),
        AgendaEvent(
            id = "3",
            title = "Project Review Meeting",
            location = "",
            startTimeInUTC = today.atTime(10, 0)
                .toInstant(TimeZone.UTC).epochSeconds,
            endTimeInUTC = today.atTime(11, 0)
                .toInstant(TimeZone.UTC).epochSeconds,
            allDay = false,
            epochDay = today.toEpochDays(),
            isRecurring = false,
            isHidden = false,
            startLocalDateTime = today.atTime(10, 0),
            endLocalDateTime = today.atTime(11, 0), // Assumed 1-hour duration
            uses24HourClock = uses24HourClock,
            isMultiDay = false
        ),
        AgendaEvent(
            id = "4",
            title = "Client Call",
            location = "",
            startTimeInUTC = today.atTime(14, 0)
                .toInstant(TimeZone.UTC).epochSeconds,
            endTimeInUTC = today.atTime(15, 0)
                .toInstant(TimeZone.UTC).epochSeconds,
            allDay = false,
            epochDay = today.toEpochDays(),
            isRecurring = false,
            isHidden = false,
            startLocalDateTime = today.atTime(14, 0),
            endLocalDateTime = today.atTime(15, 0), // Assumed 1-hour duration
            uses24HourClock = uses24HourClock,
            isMultiDay = false
        ),
        AgendaEvent(
            id = "5",
            title = "Strategy Planning Session",
            location = "",
            startTimeInUTC = today.atTime(15, 15)
                .toInstant(TimeZone.UTC).epochSeconds,
            endTimeInUTC = today.atTime(16, 15)
                .toInstant(TimeZone.UTC).epochSeconds,
            allDay = false,
            epochDay = today.toEpochDays(),
            isRecurring = false,
            isHidden = false,
            startLocalDateTime = today.atTime(15, 15),
            endLocalDateTime = today.atTime(16, 15), // Assumed 1-hour duration
            uses24HourClock = uses24HourClock,
            isMultiDay = false
        ),
        AgendaEvent(
            id = "6",
            title = "Wrap-up Meeting",
            location = "",
            startTimeInUTC = today.atTime(17, 30)
                .toInstant(TimeZone.UTC).epochSeconds,
            endTimeInUTC = today.atTime(18, 30)
                .toInstant(TimeZone.UTC).epochSeconds,
            allDay = false,
            epochDay = today.toEpochDays(),
            isRecurring = false,
            isHidden = false,
            startLocalDateTime = today.atTime(17, 30),
            endLocalDateTime = today.atTime(18, 30), // Assumed 1-hour duration
            uses24HourClock = uses24HourClock,
            isMultiDay = false
        ),
        AgendaEvent(
            id = "7",
            title = "Personal Reading Time",
            location = "",
            startTimeInUTC = today.atTime(20, 45)
                .toInstant(TimeZone.UTC).epochSeconds,
            endTimeInUTC = today.atTime(21, 45)
                .toInstant(TimeZone.UTC).epochSeconds,
            allDay = false,
            epochDay = today.toEpochDays(),
            isRecurring = false,
            isHidden = false,
            startLocalDateTime = today.atTime(20, 45),
            endLocalDateTime = today.atTime(21, 45), // Assumed 1-hour duration
            uses24HourClock = uses24HourClock,
            isMultiDay = false
        )
    )
}