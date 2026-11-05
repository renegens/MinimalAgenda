# AGENTS.md

Guidance for AI coding agents working in this repository.

## Project

MinimalAgenda (display name "Daily Planner") is a minimalist agenda app that shows events from the
device's own calendars. It is a Kotlin Multiplatform project with a shared Compose Multiplatform UI.

**Work on `main`** (trunk-based; `develop` was merged into `main` and deleted in Nov 2026).

- **iOS:** was shipped (now removed from the App Store; kept in the repo as unsupported) and complete (EventKit, native event editor and pickers, local notifications,
  onboarding, demo mode, multi-day events).
- **Android:** prototype only. `AndroidCalendarRepository.getEvents()` returns 8000 fake events, and
  `getEvent`, `getBuildVersion`, notification permission, notification scheduling and event editing are
  `TODO()` (they crash at runtime).

Android support is being planned. Decisions and open questions are tracked in the
"MinimalAgenda — Android Support Decisions" doc: https://claude.ai/artifact/HjjwoK4A95C76Hm3YHXtTe
Check it before making structural changes, and don't implement a decision that is still marked
Proposed or Blocked unless the user asks. Work through the doc's Phases in order.

## Stack

Versions live in `gradle/libs.versions.toml`.

- Kotlin 2.2.20, Compose Multiplatform 1.9.3, AGP 8.13.1, Gradle 8.13
- Koin 4.1 for DI, AndroidX DataStore for settings, kotlinx-datetime 0.7.1, kotlinx-serialization
- Navigation 2.9.2 and lifecycle 2.9.6 from JetBrains
- Cupertino UI components (`io.github.schott12521:cupertino*` 2.3.1)
- No billing, analytics or crash-reporting SDKs (decisions D10, D11)
- iOS: static `ComposeApp` framework, no Swift packages. No CocoaPods.
- Android: minSdk 24, compile/targetSdk 36, namespace and applicationId `eu.giant.agenda`

## Layout

```
composeApp/src/
  commonMain/kotlin/
    data/            entities, constants, DataStore-backed local data sources
    domain/model/    UI-facing models (AgendaEvent, AgendaDay, ...)
    domain/use_case/ business logic (GetAllEventsUseCase, CalendarGenerator, DemoDayGenerator, ...)
    repository/      interfaces + common implementations (settings, hidden events, demo events)
    platform/        interfaces/expect declarations for platform services
    presentation/    base MVI classes, components, theme, screens:
                     main, calendar (day/months/years), add_event, onboarding, settings
    di/              Koin sharedModule + `expect val platformModule`
  androidMain/       Android actuals, AndroidCalendarRepository, Android Koin module
  nativeMain/        iOS actuals, iOSCalendarRepository (EventKit), iOS Koin module
  iosMain/           MainViewController entry point
iosApp/              Xcode project (SwiftUI host)
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
- **Register new classes in Koin** (`di/Modules.kt` for shared, platform modules for actuals).
- **Time:** the app stores event times as epoch **seconds** and days as `epochDay` (`LocalDate.toEpochDays()`).
  Android's `CalendarContract` uses **milliseconds**, so convert at the repository boundary.
  Use `kotlin.time.Instant` / `kotlin.time.Clock`, not the deprecated `kotlinx.datetime` ones.
- **Every feature is free** (decision D11). RevenueCat, the paywall and premium gating have been removed on all
  platforms; don't add billing, paywall or premium code back.
- **Demo mode** shows generated demo events (`DemoDayGenerator`, `DemoEventsRepository`) until the user
  hides it; keep it working when touching event loading.
- `extensions.takingScreenshots` freezes "today" for store screenshots; it must stay `false` in commits.

## Android plans (accepted in the decisions doc, 2026-11-05; not yet implemented)

- Play build is paid upfront (2.49€), so there is no billing code anywhere. No analytics or crash SDKs in any build.
- Module split into `shared/` + `androidApp/` + `iosApp/`. `androidApp` has flavors `play` and `foss` that differ
  only by `BuildConfig.SHOW_DONATE` (true in `foss`; no donation links in `play`).
- Both flavors use applicationId `eu.giant.agenda`, signed with the same key. minSdk 26.
- Read events from `CalendarContract.Instances`, not `Events`, so recurring events are expanded.
  All-day events are UTC midnight; compute their day in UTC.
- Add/edit events via `ACTION_INSERT` / `ACTION_EDIT` intents only (no `WRITE_CALENDAR`).
- Reminders are opt-in per event: `AlarmManager` (`USE_EXACT_ALARM`) + `NotificationManager`, persisted in DataStore,
  rescheduled after boot, and the event is re-checked when the alarm fires.
- Nothing in the `foss` dependency graph may pull in GMS, Firebase, Play Billing, RevenueCat or analytics SDKs.
- iOS stays in the repo as unsupported, but shared-code changes should still keep it building.
- App will be renamed "Minimal Agenda", licensed GPL-3.0 (name and icon reserved).

## Build and run

```bash
./gradlew :composeApp:assembleDebug       # Android (prototype, shows fake data)
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64   # shared code for iOS
open iosApp/iosApp.xcodeproj              # iOS: build and run from Xcode
```

Gradle needs JDK 21; it fails on JDK 25. The Android SDK path goes in `local.properties` (gitignored).

There are no tests yet. When adding logic, add `commonTest` tests for use cases where possible.

## Working rules

- Keep changes minimal and focused on what was asked; don't refactor unrelated code.
- Don't break iOS while working on Android: shared code changes must keep the iOS build working.
- Don't add secrets to the repo. The old RevenueCat and Mixpanel client keys stay in git history and will be
  invalidated by deleting those projects before the repo goes public.
- Trunk-based development: commit directly on `main`; no feature branches or pull requests.
- Commit or push only when asked.
