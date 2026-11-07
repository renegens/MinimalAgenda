# Code audit — 2026-11-07

Audit fixes were implemented directly on `main`. The verification below was completed before committing.

## Scope and fixes

Reviewed shared UI/ViewModels, calendar models/provider boundaries, settings storage, reminders,
Android lifecycle/permissions/manifest, iOS EventKit integration, build configuration and release workflows.
Code review and regression/smoke checks cannot cover every device/provider combination.

- **Event integrity:** intermediate days of timed multi-day events retain their true calendar type when edited.
  Displaying a day as all-day no longer changes the editor's input. Span expansion is clipped to the generated
  calendar, preventing huge allocations for long provider events.
- **State consistency:** base state updates are atomic. Settings writes are serialized and show an error if
  storage fails. Hidden-event updates use a DataStore transaction and remove duplicates. Hiding updates
  agenda rows and month/year indicators. Reloads cancel stale loads, and permission revocation refreshes demo state.
- **Startup/storage:** initial navigation resolves asynchronously without blocking the UI thread. Onboarding
  dismissal removes it from the back stack. Preference read errors emit a fallback; cancellation/programming
  errors propagate. Malformed settings/hidden-event JSON uses defaults instead of crashing startup.
- **Editor writes:** repeated actions are ignored during save/delete. Provider exceptions use the existing
  retry/error state, while cancellation propagates.
- **Reminders/privacy:** Android delivery applies the current discreet-notification setting/current event title.
  Scheduling propagates cancellation. Android backup and app-data transfer exclude preferences/reminder data,
  including saved event names, matching the device-only policy.
- **iOS:** calendar queries no longer request iOS-17-only permission APIs themselves. The permission handler
  retains the legacy API path. Calendar selection/writability is respected and EventKit's actual save result is
  returned. The native editor runs on the main dispatcher and retains its weak delegate until dismissal.
- **Time:** ended-event checks read the current clock instead of a timestamp frozen at launch.

## Dependencies and compiler

Checked official Maven publisher metadata and upstream releases, excluding previews. Selected versions:

| Component | Before | Selected |
| --- | --- | --- |
| Kotlin / Compose compiler | 2.2.20 | 2.4.21 |
| Compose Multiplatform | 1.9.3 | 1.12.2 |
| Android Gradle plugin | 8.13.1 | 9.3.1 |
| Gradle | 8.13 | 9.7.0 |
| Activity Compose | 1.10.1 | 1.13.0 |
| DataStore | 1.1.7 | 1.3.0 |
| kotlinx-datetime | 0.7.1 | 0.8.0 |
| Serialization JSON | 1.9.0 | 1.12.0 |
| Coroutines | 1.8.1 | 1.11.0 |
| JetBrains Navigation | 2.9.2 | 2.10.0 |
| JetBrains Lifecycle | 2.9.6 | 2.11.0 |
| Koin | 4.1.1 | 4.2.2 |
| Cupertino (iOS only) | 2.3.1 | 2.3.1 |
| Material 3 | Compose default | 1.9.0 stable |
| Material icons | Compose default | 1.7.3 stable |

AGP 9.3.1 and Gradle 9.7.0 are ceilings from the
[Kotlin 2.4.21 tested compatibility matrix](https://kotlinlang.org/docs/multiplatform/multiplatform-compatibility-guide.html).
Newer stable AGP 9.5.1 / Gradle 9.9.0 exist, outside that tested range. Other selected library/compiler versions
are the latest stable found. Material 3 is pinned explicitly rather than inheriting Compose's preview default.

- Migrated shared Android DSL to `android {}` and the app to AGP's built-in Kotlin.
- Java toolchain 21; Java/Kotlin bytecode target 17. No experimental language version or prerelease compiler.
- compileSdk 37 is required by updated AndroidX; targetSdk remains 36 and minSdk 26.
- Refreshed Gradle wrapper scripts/JAR and pinned the official distribution SHA-256.
- Migrated deprecated Compose preview artifact/import, removed a redundant exhaustive-when branch.
- Declared coroutine core/lifecycle ViewModel Compose directly; removed unused Material 2 dependency.
- Current dependencies no longer publish Intel iOS simulator variants: targets are iPhone arm64 and Apple
  Silicon simulator arm64; removed `iosX64`. iOS remains unsupported as a product.
- CI actions: checkout 7.0.1, setup-java 6.2.0, setup-gradle 6.4.1, upload-artifact 7.0.2. No remote workflow triggered.

References: [Kotlin compatibility](https://kotlinlang.org/docs/multiplatform/multiplatform-compatibility-guide.html),
[AGP Kotlin migration](https://developer.android.com/build/migrate-to-built-in-kotlin),
[Android KMP plugin](https://developer.android.com/kotlin/multiplatform/plugin),
[Android backup behavior](https://developer.android.com/identity/data/autobackup),
[checkout](https://github.com/actions/checkout/releases/tag/v7.0.1),
[setup-java](https://github.com/actions/setup-java/releases/tag/v6.2.0),
[setup-gradle](https://github.com/gradle/actions/releases/tag/v6.4.1),
[upload-artifact](https://github.com/actions/upload-artifact/releases/tag/v7.0.2).

## Verification

Passed:

- `./gradlew :androidApp:assemblePlayDebug :androidApp:assembleFossDebug :shared:testAndroidHostTest :shared:linkDebugFrameworkIosSimulatorArm64`
- `./gradlew :shared:linkDebugFrameworkIosArm64 :shared:allTests`
- `./gradlew :androidApp:lintFossDebug :androidApp:assembleFossRelease :androidApp:bundlePlayRelease`
- `./gradlew :androidApp:dependencies --configuration fossReleaseRuntimeClasspath`
- `./gradlew :androidApp:installFossDebug`
- 168 host tests and 168 iOS simulator tests, zero failures (155 per platform before the audit).
- FOSS runtime graph has no Cupertino, GMS, Firebase, Billing, RevenueCat, Mixpanel or analytics matches.
- `git diff --check` passes.

Logs are in `/tmp/minimal-agenda-audit-final-{check,android,ios}.log`.
Release packaging succeeded locally; signing secrets, store upload and remote workflows were not exercised.

Emulator (`minimal-agenda-api36`, serial `emulator-5554`):

- Fresh-install onboarding, calendar permission grant, agenda, Back/relaunch without returning to onboarding.
- Empty day opens a date-prefilled editor; Material date picker and long-event save work.
- Created a disposable timed event from Nov 7 at 01:00 to Nov 9 at 02:00 (49 hours). Provider timestamps were
  `1794006000000` / `1794182400000`, `allDay=0`. Editing/saving from Nov 8 retained all three values.
- Enabled discreet notifications and selected 30 minutes in quick succession; both values persisted.
- Reviewed editor/Settings screenshots; crash buffer contained emulator Bluetooth crashes, no app crash.
- Deleted only the disposable event (id 15), restored original app preferences, verified the restored values,
  and stopped the emulator.

Screenshot gallery: `/tmp/minimal-agenda-audit-screenshots/index.html`.

## Remaining limits / follow-up

- Lint: zero errors, five warnings: target API 36 is below the installed API 37, two obsolete resource-folder
  qualifiers, and two adaptive icons missing monochrome layers. Target API migration and a themed icon asset
  remain separate release/design work; existing icons still render normally.
- iOS frameworks link for both arm64 targets and shared simulator tests pass. Real EventKit permissions,
  calendar-provider writes and native editor interactions were reviewed but not exercised on an iPhone.
  Installed Xcode 27 is newer than Kotlin's published Xcode 26.4 compatibility entry; local compilation succeeds.
- Physical Android hardware, API 26, manufacturer-specific providers and actual cloud/device-transfer services
  were not exercised. Backup exclusions were checked through manifest/resource validation and lint.
- Android applies privacy changes at notification delivery. iOS notifications retain the content supplied when
  scheduled; changing the preference does not rewrite previously scheduled iOS notifications.
- The external decisions artifact remains inaccessible; accepted launch decisions remain in the local document.

