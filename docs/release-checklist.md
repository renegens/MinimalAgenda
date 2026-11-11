# First release checklist

Status on 2026-11-10. The repository side is ready: the release workflow, signing, versioning, store
metadata, screenshots, launcher icon and Play upload step are in place and were verified locally with a
throwaway key. The steps below need your accounts, keys or decisions.

## 1. GitHub (terminal)

1. Choose the release keystore. Both flavors must be signed with the same key, and it can never change after
   the first release. `~/new-upload-key.jks` exists, but the same file is also in the ZoneQuest project, so
   check that it is the key you want for this app (`keytool -list -v -keystore <file>`).
2. Set the signing secrets:

   ```bash
   base64 -i <release.jks> | gh secret set KEYSTORE_BASE64
   gh secret set KEYSTORE_PASSWORD
   gh secret set KEY_ALIAS
   gh secret set KEY_PASSWORD
   ```

3. Re-enable CI if you want it on every push to `main` (it was disabled manually on GitHub):
   `gh workflow enable ci.yml`
4. Before making the repository public, delete the old RevenueCat and Mixpanel projects (their client keys are
   in git history), then: `gh repo edit --visibility public --accept-visibility-change-consequences`.
   GitHub Release downloads and IzzyOnDroid only work once the repository is public.

## 2. Google Play Console (web)

1. Create the app: name "Minimal Agenda", default language English (United States), **App**, **Paid**.
   A paid app needs a payments profile (merchant account) linked to the developer account.
2. Set the price to 2.99 € (Monetize > Products > App pricing). Play adds VAT to the price you enter, so check that
   eurozone countries show 2.99 € and edit outliers per country.
3. **App signing:** keep FOSS and Play builds on the same signature by choosing to use your own app signing
   key ("Export and upload a key from Java keystore", using Google's PEPK tool) instead of a Google-generated
   key. If Google generates the key, Play installs and GitHub/IzzyOnDroid APKs cannot update each other.
4. App content:
   - Privacy policy URL: `https://github.com/renegens/MinimalAgenda/blob/main/PRIVACY.md` (needs a public repo)
   - Ads: no ads
   - Data safety: no data collected, no data shared (calendar data stays on the device and the app has no
     INTERNET permission; verified on the release APK)
   - Content rating questionnaire (utility app, no user content): expect "Everyone"
   - Target audience: 18+ is the simplest choice (not designed for children)
   - Exact alarms: the app uses `USE_EXACT_ALARM` for event reminders, which is allowed for calendar apps;
     answer the declaration if Play Console asks for one
5. Store listing: use the texts below and upload from `fastlane/metadata/android/en-US/images/`:
   `icon.png` (512×512), `featureGraphic.png` (1024×500, a draft, replace if you want) and
   `phoneScreenshots/*.png` (1080×1920, 9:16).
6. **Upload the first AAB by hand.** Google Play's API cannot create the first release of a new app. Take the
   AAB from the `play-release-aab` artifact of the release workflow run (or build it locally with the signing
   variables set: `./gradlew :androidApp:bundlePlayRelease -PversionName=1.0.0 -PversionCode=10000`) and
   upload it to the internal testing track.
7. If the developer account is a personal account created after November 2023, Google requires a closed test
   with at least 12 testers for 14 days before production access.

### Store listing text (Play)

The F-Droid description in `full_description.txt` links to GitHub Sponsors. The Play build has no donation
link (decision D10/D11), so use this version for Play.

- Title: `Minimal Agenda`
- Short description: `A minimal, private agenda for your device's calendars. No tracking.`
- Full description:

```text
Minimal Agenda is a minimalist agenda that shows the events from the calendars already on your device, in one clean, distraction-free list.

Features
• Agenda view of your upcoming events, plus month and year overviews
• Works with every calendar on your device (Google, Nextcloud/DAVx5, local and others)
• Add, edit and delete events; recurring events and multi-day events are supported
• Optional reminders for individual events, with discreet notifications that hide event names on the lock screen
• Light, dark and system themes, accent colors
• If your device has no calendar, you can create a device-only one

Privacy
• No data collection, no analytics, no crash reporting, no ads
• No network access: your events never leave your device
• Calendar data is read and written only through the Android system calendar

Open source
Minimal Agenda is licensed under the GPL-3.0. Source code: https://github.com/renegens/MinimalAgenda
```

## 3. Automate Play uploads (Google Cloud + Play Console)

1. In Google Cloud Console, create (or pick) a project, enable the **Google Play Android Developer API**, create
   a service account and download a JSON key.
2. In Play Console > Users and permissions, invite the service account email and grant it release permissions
   for Minimal Agenda.
3. `gh secret set PLAY_SERVICE_ACCOUNT_JSON < service-account.json`, then delete the local JSON file.
4. Optional repository variables (defaults: `internal`, `draft`):
   `gh variable set PLAY_TRACK --body internal` and, once the app has been published once,
   `gh variable set PLAY_RELEASE_STATUS --body completed`.

## 4. Release

1. Add `fastlane/metadata/android/en-US/changelogs/<versionCode>.txt` (v1.0.0 → `10000.txt`, already present).
2. `git tag v1.0.0 && git push origin v1.0.0`
3. The workflow creates the GitHub Release with the signed FOSS APK and, if the Play secret is set, a draft
   release on the internal track. Review and roll it out in Play Console.
4. After launch, replace the Play and IzzyOnDroid TODO links in `README.md`.

## Optional local tools

- `brew install fastlane`: `fastlane supply` can upload listings and images from `fastlane/metadata/android`
  and promote releases between tracks (`--track internal --track_promote_to production`).
- `brew install bundletool`: build APKs from the AAB to test the Play build locally.
