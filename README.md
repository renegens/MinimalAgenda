# Minimal Agenda

A minimal, private agenda for your device's own calendars.

- **Android:** supported (Google Play and a free FOSS build).
- **iOS:** the code is kept in the repository but is **unsupported** (the app is no longer in the App Store).

Built with Kotlin Multiplatform and a shared Compose Multiplatform UI. Shared control wrappers use Material 3
on Android and retain Cupertino controls on iOS; Cupertino dependencies are scoped to iOS only.

## Features

- Agenda view of your events, with month and year overviews
- Reads every calendar on the device (recurring and multi-day events included)
- Add, edit and delete events from within the app; tap a day header or empty day to start with that date
- Quick duration chips and Starts/Ends controls for longer and multi-day events; all-day end dates are inclusive
- Dates follow your locale, and times follow your system 12/24-hour setting
- Optional per-event reminders, with discreet notifications that hide event names on the lock screen
- Reminder timing chips for At start, 5, 10, 15, 30 and 60 minutes; existing custom offsets are preserved
- Light, dark and system themes, accent colors
- Creates a device-only calendar if you have none
- Every feature is free

## Privacy

Minimal Agenda collects no data. There is no analytics, no crash reporting and no ads, and the app uses
the network only to open a link in your browser when you tap one. Calendar data stays on the device and is
read and written through the system calendar. See [PRIVACY.md](PRIVACY.md).

## Install

- **Google Play** (paid, one-time purchase): TODO add Play Store link
- **GitHub Releases** (free FOSS build, signed APK): https://github.com/renegens/MinimalAgenda/releases
- **IzzyOnDroid** (free FOSS build): TODO add IzzyOnDroid link once listed

Both Android builds have the same package name (`eu.giant.agenda`) and are signed with the same key. They
differ only in that the FOSS build shows a "Support development" item in Settings; the Play build contains
no donation link.

## Build

Requirements: JDK 21 and Android SDK 37 (path in `local.properties`, which is gitignored).
The build pins Java toolchain 21 and targets JVM 17. Android supports API 26+, targeting API 36.

Toolchain: Kotlin 2.4.21, Compose Multiplatform 1.12.2, AGP 9.3.1 and Gradle 9.7.0.
AGP and Gradle follow Kotlin’s tested compatibility range. See [the code audit](docs/code-audit-2026-11-07.md)
for dependency versions, fixes and verification.

```bash
./gradlew :androidApp:assemblePlayDebug :androidApp:assembleFossDebug   # debug APKs
./gradlew :androidApp:assembleFossRelease                               # release APK (unsigned without keystore, see below)
./gradlew :shared:testAndroidHostTest                                   # Android host tests
./gradlew :shared:allTests                                              # all tests (iOS simulator tests need macOS)
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64                   # shared code for iOS (macOS only)
```

iOS: open `iosApp/iosApp.xcodeproj` in Xcode (unsupported). For `xcodebuild`, set
`DEVELOPER_DIR=/Applications/Xcode.app/Contents/Developer`. The supported build targets are arm64 iPhones
and Apple Silicon simulators; current dependencies no longer provide Intel simulator variants.

To check the Android UI on the project emulator:

```bash
~/.local/bin/android --no-metrics emulator start --headless minimal-agenda-api36
./gradlew :androidApp:installFossDebug
~/Library/Android/sdk/platform-tools/adb -s emulator-5554 shell am start -W -n eu.giant.agenda/.MainActivity
~/Library/Android/sdk/platform-tools/adb -s emulator-5554 exec-out screencap -p > /tmp/minimal-agenda.png
~/Library/Android/sdk/platform-tools/adb -s emulator-5554 emu kill
```

Always specify the emulator serial when using `adb` and stop the emulator after verification.

Flavors: `play` and `foss` (they differ only by `BuildConfig.SHOW_DONATE`).
`-PversionName=1.2.3 -PversionCode=10203` override the version.

## Project layout

```
shared/       KMP library: shared UI, domain and data code; platform actuals; builds the iOS ComposeApp framework
androidApp/   Android app (MainActivity, manifest, resources), flavors play and foss
iosApp/       Xcode project (SwiftUI host), unsupported
fastlane/     F-Droid / IzzyOnDroid store metadata
.github/      CI, release workflows and issue templates
docs/         accepted launch-polish decisions
```

The accepted launch changes and behavior conventions are recorded in
[Launch polish decisions](docs/launch-polish-decisions.md).

## Release

Release signing is configured with environment variables (or Gradle properties) of the same names; without
them, release builds are unsigned and the build still succeeds:

`MINIMAL_AGENDA_KEYSTORE` (path), `MINIMAL_AGENDA_KEYSTORE_PASSWORD`, `MINIMAL_AGENDA_KEY_ALIAS`, `MINIMAL_AGENDA_KEY_PASSWORD`

Pushing a tag such as `v1.2.3` runs `.github/workflows/release.yml`, which runs the tests, builds the signed
FOSS APK and the Play AAB, and creates a GitHub Release with the APK attached. The AAB is kept only as a
workflow artifact; upload it to Google Play by hand. `versionName` is the tag without the leading `v`, and
`versionCode` is `major*10000 + minor*100 + patch` (v1.2.3 gives 10203).

Repository secrets required by the workflow:

| Secret | Content |
| --- | --- |
| `KEYSTORE_BASE64` | the keystore file, base64-encoded (`base64 -i release.jks`) |
| `KEYSTORE_PASSWORD` | keystore password |
| `KEY_ALIAS` | key alias |
| `KEY_PASSWORD` | key password |

`.github/workflows/ci.yml` runs the Android host tests and debug builds on every push to `main`.

## Support

Report bugs and suggest features through [GitHub Issues](https://github.com/renegens/MinimalAgenda/issues/new/choose),
or email [ios@giant-solutions.eu](mailto:ios@giant-solutions.eu). Feature requests do not imply a commitment to implementation.

Minimal Agenda is free and open source. If you like it, you can support development through
[GitHub Sponsors](https://github.com/sponsors/renegens).

## License

Copyright (C) Rene Gens / Giant Solutions.

Minimal Agenda is licensed under the [GNU General Public License v3.0 only](LICENSE) (GPL-3.0-only).

**Branding:** the name "Minimal Agenda" and the app icon are not licensed under the GPL. Forks and
redistributed modified versions must use a different name and a different icon.
