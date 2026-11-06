# Minimal Agenda launch polish decisions

Status: **Accepted**. Recorded on **2026-11-06** from the user's launch-polish instructions, including package H.

This is a local supplement to the [MinimalAgenda — Android Support Decisions](https://claude.ai/artifact/HjjwoK4A95C76Hm3YHXtTe)
artifact. The external artifact was inaccessible during this work and was not edited. The decisions below are
user-accepted work, not new proposals or claims about the external document's contents.

## A. Copy and cleanup

**Accepted — 2026-11-06.** Use “Minimal Agenda” in the onboarding welcome title. Explain that the app reads,
creates, edits and deletes events through the system calendar, keeps no event database of its own, and collects
no data. Rename the visibility menu states to “Show hidden events” and “Hide hidden events”. Remove root `ToDo.kt`.

## B. Locale-aware dates and times

**Accepted — 2026-11-06.** Use the system locale's date formatting and month labels. Keep the compact day header's
two numeric components and dot separator, with day/month order derived from localized patterns rather than a
country check. Editor and details dates use the same platform formatters. Time displays and pickers respect
the system 12/24-hour preference. Date-picker values represent UTC midnight of the selected calendar date;
formatting must not shift them to yesterday in time zones west of UTC.

## C. Create an event from the agenda

**Accepted — 2026-11-06.** Tapping a day header or “Day is free, enjoy it” opens the editor with that date filled
in. Today starts at the next full hour, including advancing to tomorrow at 00:00 during the last hour of today.
Other dates start at 09:00. Keep “New Event” in the menu and carry the optional selected date through both platform
presenters to the shared editor.

## D. Duration chips and explicit Ends controls

**Accepted — 2026-11-06.** Keep 10/15/30/60/90/120-minute chips and replace the duration text field with an Ends
date/time row. A chip sets end = start + elapsed duration. Direct end selection highlights an exact matching
chip, or none. Changing Starts preserves the current elapsed duration, including across midnight and daylight
saving transitions. Timed ends must be after starts; invalid ends show an error and disable Save.

All-day events show start/end dates only. Ends is the last covered date, inclusive, and may equal Starts.
Repositories convert this inclusive end to the exclusive next-day calendar boundary. EventKit reads normalize
that exclusive boundary back to the last covered day.

Events longer than two hours and multi-day events must save without truncation. Existing events retain their
real endpoints, including seconds. `EventTimeRange` carries exact timed instants to the repository so repeated
local times during clock rollback remain unambiguous. Domain event timestamps remain epoch seconds; Android
calendar provider timestamps remain milliseconds, with conversion at the repository boundary.

Android nonrecurring writes use DTSTART/DTEND and clear a previous DURATION when updating. Recurring Android
events remain restricted to title/location changes for the whole series after confirmation; never write series
times. iOS edits continue to use Apple's native EventKit editor; shared iOS creation supports the same time spans.

## E. Reminder time chips

**Accepted — 2026-11-06.** Replace the free-text notification offset field with At start (0), 5, 10, 15, 30 and
60-minute chips. Keep a stored nonpreset value as an extra selected chip until the user explicitly chooses
another value. Loading the screen must not reset that value. Reminders remain opt-in for each event.

## F. Remove immersive mode

**Accepted — 2026-11-06.** Remove the setting and its state, domain, persistence and UI handling. Always show
the app's bottom navigation. Ignore the legacy `hideBottomNavigation` JSON field without resetting theme,
accent color, notification privacy or reminder timing. The older settings format without that field remains
readable. Remove immersive mode from the published feature description.

## G. Support through GitHub

**Accepted — 2026-11-06.** “Contact Support” opens the
[GitHub issue chooser](https://github.com/renegens/MinimalAgenda/issues/new/choose) through the browser. Add
“or email us” using the existing [support address](mailto:ios@giant-solutions.eu). Keep useful email failure
feedback while the secondary action needs it. Neither action sends a report without the user's further action.

Provide bug-report and feature-request issue forms in `.github/ISSUE_TEMPLATE/`, with a chooser configuration.
Feature requests carry no commitment to implementation or a timeline. Bug reports ask for technical details
and reproduction steps using sample event names rather than personal calendar data.

## H. Cupertino only on iOS

**Accepted — 2026-11-06.** Put platform controls behind small common wrappers for icons, menus, alerts and
date/time pickers. Android implementations use Material 3 with the app's typography and colors. iOS retains
the existing Cupertino controls and behavior. Keep the existing shared Material 3 `AppSwitch` on both platforms.
Cupertino dependencies belong to `nativeMain`, which targets iOS in this project; common and Android source
must not import them.

Android compile/runtime dependency graphs and APKs must contain no Cupertino artifacts. Gradle's aggregate
`implementationDependenciesMetadata` report can still include native variant metadata; it is distinct from
the Android configurations and packaged APK contents.

When dismissing a confirmation dialog through Back or an outside tap, use its cancellation callback rather
than its confirmation callback. A single-action informational dialog uses its normal close callback.

## Verification and working conventions

Work directly on `main`, one package at a time. Review each package before committing; no branches, pull
requests or pushes are part of this launch-polish task. Preserve iOS compilation and demo mode.

Each package uses both Android debug builds, Android host tests and the iOS simulator framework link. Common
tests cover date order, event time/duration behavior, reminder options and legacy settings decoding. UI changes
are checked on `minimal-agenda-api36`, with screenshots. Use `--no-metrics` when starting it, always pass
`-s emulator-5554` to `adb`, and stop the emulator afterwards. See [AGENTS.md](../AGENTS.md) for exact commands.
