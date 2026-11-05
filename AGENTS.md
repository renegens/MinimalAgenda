# AGENTS.md

Guidance for AI coding agents working in this repository.

## Project

MinimalAgenda (display name "Daily Planner") is a minimalist agenda app that shows events from the
device's own calendars. It is a Kotlin Multiplatform project with a shared Compose Multiplatform UI.

**Active branch is `develop`.** `main` was last merged in Nov 2024 and is far behind; base work on `develop`.

- **iOS:** shipped and complete (EventKit, native event editor and pickers, local notifications,
  onboarding, demo mode, multi-day events, RevenueCat paywall UI, Mixpanel analytics in Swift).
- **Android:** prototype only. `AndroidCalendarRepository.getEvents()` returns 8000 fake events, and
  `getEvent`, `getBuildVersion`, notification permission, notification scheduling and event editing are
  `TODO()` (they crash at runtime).

Android support is being planned. Decisions and open questions are tracked in the
"MinimalAgenda — Android Support Decisions" doc: https://claude.ai/artifact/HjjwoK4A95C76Hm3YHXtTe
Check it before making structural changes, and don't implement a decision that is still marked
Proposed or Blocked unless the user asks.

## Stack

Versions live in `gradle/libs.versions.toml`.

- Kotlin 2.2.20, Compose Multiplatform 1.9.3, AGP 8.13.1, Gradle 8.13
- Koin 4.1 for DI, AndroidX DataStore for settings, kotlinx-datetime 0.6.1, kotlinx-serialization
- Navigation and lifecycle from JetBrains, currently `2.9.0-rc02`
- RevenueCat `purchases-kmp-core` + `purchases-kmp-ui` (`2.0.0+15.0.0`) for the `Unlocked` entitlement and paywall
- Cupertino UI components (`io.github.schott12521:cupertino*` 2.3.1)
- iOS: Swift Package Manager (RevenueCat, Mixpanel), static `ComposeApp` framework. No CocoaPods.
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
iosApp/              Xcode project (SwiftUI host, Mixpanel init in iOSApp.swift)
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
- **Premium gating** goes through `guardPremium()` / `isPremium()` in `extensions/RevenueCatExtensions.kt`,
  and the RevenueCat `Paywall` composable is used directly in `App.kt` and `Onboarding.kt`. The plan is to
  put these behind injected interfaces so a FOSS Android build can drop RevenueCat. Don't add new direct
  RevenueCat calls or imports in common code.
- **Demo mode** shows generated demo events (`DemoDayGenerator`, `DemoEventsRepository`) until the user
  hides it; keep it working when touching event loading.
- `extensions.takingScreenshots` freezes "today" for store screenshots; it must stay `false` in commits.

## Android plans (from the decisions doc, not yet implemented)

- Two flavors: `play` (paid, Google Play) and `foss` (free, F-Droid, no Play Services or proprietary deps).
- Likely module split into `shared/` + `androidApp/` (flavors) + `iosApp/` before upgrading to AGP 9.
- Read events from `CalendarContract.Instances`, not `Events`, so recurring events are expanded.
  All-day events are UTC midnight; compute their day in UTC.
- Reminders via `AlarmManager` + `NotificationManager`, rescheduled after boot.
- Nothing in the `foss` dependency graph may pull in GMS, Firebase, Play Billing, RevenueCat or analytics SDKs.

## Build and run

```bash
./gradlew :composeApp:assembleDebug       # Android (prototype, shows fake data; build not verified)
open iosApp/iosApp.xcodeproj              # iOS: build and run from Xcode
```

There are no tests yet. When adding logic, add `commonTest` tests for use cases where possible.

## Working rules

- Keep changes minimal and focused on what was asked; don't refactor unrelated code.
- Don't break iOS while working on Android: shared code changes must keep the iOS build working.
- The RevenueCat key in `extensions/RevenueCatExtensions.kt` and the Mixpanel token in `iOSApp.swift` are
  client-side keys; don't add other secrets to the repo.
- Commit or push only when asked.
