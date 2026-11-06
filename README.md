# Minimal Agenda

A minimal, private agenda that shows the events from your device's own calendars.

- **Android:** supported (Google Play and a free FOSS build).
- **iOS:** the code is kept in the repository but is **unsupported** (the app is no longer in the App Store).

Built with Kotlin Multiplatform and a shared Compose Multiplatform UI.

## Features

- Agenda view of your events, with month and year overviews
- Reads every calendar on the device (recurring and multi-day events included)
- Add, edit and delete events from within the app
- Optional per-event reminders, with discreet notifications that hide event names on the lock screen
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

Requirements: JDK 21 (Gradle fails on JDK 25) and the Android SDK (path in `local.properties`, which is gitignored).

```bash
./gradlew :androidApp:assemblePlayDebug :androidApp:assembleFossDebug   # debug APKs
./gradlew :androidApp:assembleFossRelease                               # release APK (unsigned without keystore, see below)
./gradlew :shared:testAndroidHostTest                                   # Android host tests
./gradlew :shared:allTests                                              # all tests (iOS simulator tests need macOS)
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64                   # shared code for iOS (macOS only)
```

iOS: open `iosApp/iosApp.xcodeproj` in Xcode (unsupported).

Flavors: `play` and `foss` (they differ only by `BuildConfig.SHOW_DONATE`).
`-PversionName=1.2.3 -PversionCode=10203` override the version.

## Project layout

```
shared/       KMP library: shared UI, domain and data code; platform actuals; builds the iOS ComposeApp framework
androidApp/   Android app (MainActivity, manifest, resources), flavors play and foss
iosApp/       Xcode project (SwiftUI host), unsupported
fastlane/     F-Droid / IzzyOnDroid store metadata
.github/      CI and release workflows
```

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

Minimal Agenda is free and open source. If you like it, you can support development through
[GitHub Sponsors](https://github.com/sponsors/renegens).

## License

Copyright (C) Rene Gens / Giant Solutions.

Minimal Agenda is licensed under the [GNU General Public License v3.0 only](LICENSE) (GPL-3.0-only).

**Branding:** the name "Minimal Agenda" and the app icon are not licensed under the GPL. Forks and
redistributed modified versions must use a different name and a different icon.
