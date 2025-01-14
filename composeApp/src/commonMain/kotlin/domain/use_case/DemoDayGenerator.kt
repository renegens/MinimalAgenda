package domain.use_case

import domain.model.FormattedTime
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