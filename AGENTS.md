# AGENTS.md

Guidance for AI coding agents working in this repository.

## Project

MinimalAgenda (display name "Minimal Agenda") is a minimalist agenda app that reads and writes events through the
device's own calendars. It is a Kotlin Multiplatform project with a shared Compose Multiplatform UI.

**Work on `main`** (trunk-based; `develop` was merged into `main` and deleted in Nov 2026).

- **iOS:** was shipped (now removed from the App Store; kept in the repo as unsupported) and complete (EventKit, native event editor and pickers, local notifications,
  onboarding, demo mode, multi-day events).
- **Android:** feature-complete, not yet released. Reads real calendars (`CalendarContract.Instances`), offers a
  device-only calendar when none exist, adds/edits/deletes events in the shared editor, and schedules per-event
  reminders. Release prep (rename, license, signing, CI) is done; see "Release" below.

Decisions and open questions are tracked in the
"MinimalAgenda — Android Support Decisions" doc: https://claude.ai/artifact/HjjwoK4A95C76Hm3YHXtTe
Check it before making structural changes, and don't implement a decision that is still marked
Proposed or Blocked unless the user asks. Work through the doc's Phases in order.

The user-accepted launch polish is recorded in [Launch polish decisions](docs/launch-polish-decisions.md)
(A–H, Accepted on 2026-11-06). This local record supplements the external doc; the external artifact was
inaccessible during the work and was not edited. These local decisions are Accepted, not proposals.

## Stack

Versions live in `gradle/libs.versions.toml`. The [code audit](docs/code-audit-2026-11-07.md)
records the Nov 2026 dependency update, compatibility ceilings, verification and remaining limits.

- Kotlin 2.4.21, Compose Multiplatform 1.12.2, AGP 9.3.1, Gradle 9.7.0
- Koin 4.2.2 for DI, AndroidX DataStore 1.3.0 for settings, kotlinx-datetime 0.8.0, kotlinx-serialization 1.12.0
- Navigation 2.10.0 and lifecycle 2.11.0 from JetBrains
- Cupertino UI components (`io.github.schott12521:cupertino*` 2.3.1) are iOS-only dependencies in `nativeMain`.
  Android uses Material 3 through shared control wrappers; keep Cupertino imports out of common and Android code.
- No billing, analytics or crash-reporting SDKs (decisions D10, D11)
- iOS: static `ComposeApp` framework, no Swift packages. No CocoaPods.
- Android: minSdk 26, compileSdk 37 / targetSdk 36, applicationId `eu.giant.agenda` for both flavors
- `shared` uses the KMP Android library plugin (`com.android.kotlin.multiplatform.library`); compose resources keep
  the package `minimalagenda.composeapp.generated.resources` via `packageOfResClass`

## Layout

```
shared/src/         KMP library: all shared code; also builds the iOS `ComposeApp` framework
  commonMain/kotlin/
    data/            entities, constants, DataStore-backed local data sources
    domain/model/    UI-facing models (AgendaEvent, AgendaDay, ...)
    domain/use_case/ business logic (GetAllEventsUseCase, CalendarGenerator, DemoDayGenerator, ...)
    repository/      interfaces + common implementations (settings, hidden events, demo events)
    platform/        interfaces/expect declarations for platform services
    presentation/    base MVI classes, components, theme, screens:
                     main, calendar (day/months/years), add_event, onboarding, settings
    di/              Koin sharedModule + `expect val platformModule`
  commonTest/        use-case and extension tests, repository fakes in testutil/
  androidMain/       Android actuals, AndroidCalendarRepository, Android Koin module
  nativeMain/        iOS actuals, iOSCalendarRepository (EventKit), iOS Koin module
  iosMain/           MainViewController entry point
androidApp/          Android app: MainActivity, MyApplication, manifest, launcher resources;
                     flavors `play` and `foss` (differ only by BuildConfig.SHOW_DONATE)
iosApp/              Xcode project (SwiftUI host), runs :shared:embedAndSignAppleFrameworkForXcode
```

## Architecture conventions

- **Screens follow Contract / ViewModel / Screen** (`presentation/screens/<name>/`).
  The Contract holds `State : UiState`, `Event : UiEvent`, `Effect : UiEffect`.
  ViewModels extend `BaseViewModel<Event, State, Effect>`: update state with `setState { copy(...) }`,
  emit one-off effects with `setEffect { ... }`, handle input in `handleEvent`.
- **Use cases** are classes with `suspend operator fun invoke(...)` (or a named suspend function) that
  return a sealed `State` or a plain value.
- **Platform services** (calendar, permissions, notifications, modals, browser, settings) are interfaces or
  `expect` functions in `commonMain/platform`, implemented per platform and bound in
  `di/Modules.android.kt` / `di/Modules.native.kt`. Add new platform features the same way; don't call
  platform APIs from common code.
- **Platform UI controls** use common `expect` declarations in `presentation/components/PlatformControls.kt`
  and `AppAlertDialog.kt`, with actuals in `androidMain` and `nativeMain`. Preserve picker callbacks and model
  synchronization when changing them. `AppSwitch` remains the existing shared Material 3 control on both platforms.
- **Register new classes in Koin** (`di/Modules.kt` for shared, platform modules for actuals).
- **Time:** the app stores event times as epoch **seconds** and days as `epochDay` (`LocalDate.toEpochDays()`).
  Android's `CalendarContract` uses **milliseconds**, so convert at the repository boundary.
  Use `kotlin.time.Instant` / `kotlin.time.Clock`, not the deprecated `kotlinx.datetime` ones.
  The editor passes `EventTimeRange` exact instants for timed writes so elapsed durations and existing events
  survive repeated daylight-saving hours. Date-picker values are UTC-midnight **milliseconds** for a calendar date.
  The editor's all-day Ends date is inclusive; repositories convert it to an exclusive next-day calendar boundary.
- **Every feature is free** (decision D11). RevenueCat, the paywall and premium gating have been removed on all
  platforms; don't add billing, paywall or premium code back.
- **Demo mode** shows generated demo events (`DemoDayGenerator`, `DemoEventsRepository`) until the user
  hides it; keep it working when touching event loading.
- `extensions.takingScreenshots` freezes "today" for store screenshots; it must stay `false` in commits.

## Android plans (accepted in the decisions doc, 2026-11-05)

- Play build is paid upfront (2.49€), so there is no billing code anywhere. No analytics or crash SDKs in any build.
- Modules `shared/` + `androidApp/` + `iosApp/` (done). `androidApp` has flavors `play` and `foss` that differ
  only by `BuildConfig.SHOW_DONATE` (true in `foss`; no donation links in `play`).
- Both flavors use applicationId `eu.giant.agenda`, signed with the same key. minSdk 26.
- Read events from `CalendarContract.Instances`, not `Events`, so recurring events are expanded.
  All-day events are UTC midnight; compute their day in UTC (helpers in `data/calendar/CalendarMath.kt`).
  An Android event id is the instance id `"<EVENT_ID>/<BEGIN ms>"`.
- Add, edit and delete events directly (`WRITE_CALENDAR`) from the shared editor (`add_event`), like iOS (D23,
  replaces the intents-only D7). Recurring events: only title/location change, for the whole series, after a
  confirmation; never write DTSTART/DTEND on a series. When the device has no calendars, offer to create a
  device-only calendar (`ACCOUNT_TYPE_LOCAL`).
- Reminders are opt-in per event: `AlarmManager` (`USE_EXACT_ALARM`) + `NotificationManager`, persisted in DataStore
  (`data/reminder/`), restored on boot/update/time change/app start, and re-checked when the alarm fires
  (`ReminderPlanner`: show, reschedule if moved, drop if deleted or ended).
- Android permissions go through `PermissionRequester` (ActivityResult bridge registered in `MainActivity`).
- Test on the emulator `minimal-agenda-api36` with Google's `android` CLI using `--no-metrics`.
  Always pass `-s emulator-5554` to `adb`, take screenshots for UI changes, and stop the emulator when done
  (commands below).
- Nothing in the `foss` dependency graph may pull in GMS, Firebase, Play Billing, RevenueCat or analytics SDKs.
- iOS stays in the repo as unsupported, but shared-code changes should still keep it building.
- App is named "Minimal Agenda", licensed GPL-3.0-only (name and icon reserved).

## Launch behavior

- Day headers and "Day is free, enjoy it" open a new event for that date. Today starts at the next full hour,
  rolling into the next date at midnight; other dates start at 09:00. Keep "New Event" in the menu.
- Date displays follow the system locale; compact day headers keep two numbers separated by a dot in the
  locale's order. Time displays and pickers respect the system 12/24-hour preference.
- Duration chips (10/15/30/60/90/120 minutes) and the Ends pickers edit the same elapsed duration. Moving Starts
  preserves that duration. Timed ends must be after starts; multi-day and longer events are supported.
  All-day events show dates only, and the inclusive end date may equal the start date.
- Reminder timing uses At start (0), 5, 10, 15, 30 and 60-minute chips. A nonpreset stored value stays as an extra
  selected chip until the user chooses another value; never reset it just because it is not a preset.
- Bottom navigation is always visible. Immersive mode has been removed; legacy JSON fields are ignored while
  preserving the remaining preferences.
- "Contact Support" opens the GitHub issue chooser; "or email us" uses the existing support address.
  Templates live in `.github/ISSUE_TEMPLATE/`. Feature requests carry no implementation commitment.

## Build and run

```bash
./gradlew :androidApp:assemblePlayDebug :androidApp:assembleFossDebug :shared:testAndroidHostTest # Android checks
./gradlew :shared:allTests                                             # Android host + iOS simulator tests
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64                  # shared code for iOS
./gradlew :androidApp:dependencies --configuration fossReleaseRuntimeClasspath   # check foss has no proprietary deps
open iosApp/iosApp.xcodeproj                                           # iOS: build and run from Xcode
```

Use JDK 21 (the pinned Java toolchain) and Android SDK 37. Java/Kotlin bytecode targets JVM 17.
The Android SDK path goes in `local.properties` (gitignored). AGP uses built-in Kotlin in `androidApp`.
AGP and Gradle stay within Kotlin’s published tested compatibility range; see the code audit report.
iOS targets are device arm64 and Apple Silicon simulator arm64; current dependencies no longer publish Intel simulator variants.

For `xcodebuild`, set `DEVELOPER_DIR=/Applications/Xcode.app/Contents/Developer`.

For emulator UI verification:

```bash
~/.local/bin/android --no-metrics emulator start --headless minimal-agenda-api36
./gradlew :androidApp:installFossDebug
~/Library/Android/sdk/platform-tools/adb -s emulator-5554 shell am start -W -n eu.giant.agenda/.MainActivity
~/Library/Android/sdk/platform-tools/adb -s emulator-5554 exec-out screencap -p > /tmp/minimal-agenda.png
~/Library/Android/sdk/platform-tools/adb -s emulator-5554 emu kill
```

Tests live in `shared/src/commonTest` (hand-written fakes, no mocking library). When adding logic, add tests for it there.
Android compile/runtime dependency graphs must contain no Cupertino artifacts. The full Gradle dependency report's
aggregate `implementationDependenciesMetadata` can still list native variant dependencies; check Android configurations
and APK contents when distinguishing metadata from packaged Android dependencies.

## Release

- Release signing reads `MINIMAL_AGENDA_KEYSTORE`, `MINIMAL_AGENDA_KEYSTORE_PASSWORD`, `MINIMAL_AGENDA_KEY_ALIAS`,
  `MINIMAL_AGENDA_KEY_PASSWORD` (env vars or Gradle properties); without them release builds are unsigned.
  Never commit a keystore (`*.jks`, `*.keystore` are gitignored).
- `-PversionName=... -PversionCode=...` override the defaults in `androidApp/build.gradle.kts`.
- Pushing a `v*` tag runs `.github/workflows/release.yml` (tests, signed FOSS APK attached to a GitHub Release,
  Play AAB as a workflow artifact). `ci.yml` runs tests and debug builds on `main`. Details in README.

## Working rules

- Keep changes minimal and focused on what was asked; don't refactor unrelated code.
- Don't break iOS while working on Android: shared code changes must keep the iOS build working.
- Don't add secrets to the repo. The old RevenueCat and Mixpanel client keys stay in git history and will be
  invalidated by deleting those projects before the repo goes public.
- Trunk-based development: commit directly on `main`; no feature branches or pull requests.
- Commit or push only when asked.
