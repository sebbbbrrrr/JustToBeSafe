# JustToBeSafe — Calm Utility Android prototype

## What was implemented

A **native Android** Kotlin / Jetpack Compose prototype (not a web app), following the founder-selected Calm Utility concept. Its UI is designed around Material 3, and the 4 bottom destinations are **Home, Guides, Supplies, Plan**. The concept source depicts a readiness metric and regional hazards, but both are represented truthfully here:

- **Home:** Calm Utility visual identity, original lightweight Compose countryside hero, animated supplies progress ring, shortcuts, honest local status and an external link to official warnings.
- **Guides:** Offline topic index, topic details and external official-resource links. **No unreviewed emergency procedures are published**. Topic pages clearly indicate specialist review is pending.
- **Supplies:** Eight interactive checklist items, derived completion percentage, locally persisted on/off status, smooth 260ms progress animation.
- **Plan:** Add/delete household contacts and edit a meeting point. Records are encrypted at rest via Android Keystore AES-GCM, kept entirely on device, with no plaintext fallback. A secure-save failure is reported rather than silently dropped.
- **Design:** Android Material 3 navigation bar or rail (larger layouts), 48dp+ primary touch targets, light/dark palette, screen transitions using 170ms/100ms fade-through, native feedback, status/keyboard/navigation insets handled through Compose Scaffold and Activity edge-to-edge. Compose honors the platform animation-duration scale.
- **Permissions:** No INTERNET, LOCATION, CONTACTS, SMS, CALL_PHONE or notification permissions. External official links use the user's browser. Automatic backup and device transfer of plan files are disabled.

## Stage & restrictions

This is a **founder-authorised Phase 0 interface prototype** for concept validation, not the launched emergency-preparedness service, a reliable live-alert client, or a claim of specialist-reviewed content. It stores locally entered data on the device; it does not operate a waitlist, pricing experiment, cloud backend or live hazard feed. The JustToBeSafe name remains provisional. The product brief and Phase 0 research work are unchanged.

The supply checkboxes are **inventory reminders**. The percentage is always labelled "supplies progress", **not a safety score**. The saved guide index is not a complete, reviewed offline safety content pack. Any claims in the earlier visual mockup (such as 78% ready, specific hazards, dates, source verification or authoritative status) are treated as illustrative, and are not reproduced as real-world facts.

Official-resource links included as external links:
- https://beready.campaign.gov.uk/
- https://weather.metoffice.gov.uk/warnings-and-advice/uk-warnings

Neither government nor meteorological guidance is copied into the APK. Public release of any substantive emergency, medical or hazard-specific instruction needs source/licence/expert review.

## Android build

- JDK 17; Android SDK API 36; Android SDK Build Tools for API 36.
- Gradle 8.13; Android Gradle Plugin 8.13.2; Kotlin + Compose plugin 2.2.20; Compose BOM 2026.09.00; Android minSdk 26, targetSdk 36.
- Open the repository folder in Android Studio and sync Gradle. If a Gradle wrapper is absent in the checkout, use your installed Gradle 8.13 (or run `gradle wrapper --gradle-version 8.13` once in a trusted development environment).
- Compile: `gradle :app:assembleDebug` from the repository root.
- Unit tests: `gradle :app:testDebugUnitTest`.
- On a device: install `app/build/outputs/apk/debug/app-debug.apk` using Android Studio or `adb install -r`.
- For CI, the `Android debug APK (manual)` GitHub Actions workflow only runs when manually dispatched, and uploads the debug APK as an artifact. It is **not triggered on every commit**.

Standalone dependency-free domain check: `bash scripts/check_domain.sh` (requires `kotlinc` + Java). It tests unique checklist IDs, empty state, invalid IDs, progress bounds and 75% calculation.

## QA status

- [x] Standalone Kotlin checklist-domain smoke tests (local Kotlin 1.9, pure domain code; compatibility subset) passed.
- [x] XML structural checks, manifest permission audit and static content boundary checks (local Python).
- [ ] Android SDK/Gradle compilation — not run in this environment (Android SDK and Gradle installation unavailable; container network disabled).
- [ ] Android emulator/hardware installation, screenshots and TalkBack/manual font-scaling tests — not run.
- [ ] Native runtime animations / 60fps profiling / dark-theme screen verification — not run.
- [ ] Expert review of emergency content / legal or privacy launch review — not applicable to current placeholders; required before release.

Do not describe this code as an APK already built or verified on hardware until those steps are completed.

## UI design decision record

The Calm Utility direction is selected for the Android **prototype** only: soft teal (#087C72), slate-blue and mist surfaces, restrained elevation, no decorative motion loops and no fear-based visual identity. Inspired by the founder-approved concept image. Original icons are Material icons, rendered by Android; the countryside hero is procedurally drawn with native Compose Canvas (no external image assets or licensing risk). Type uses Android/Material's native sans-serif. The final brand identity still requires formal approval.

**Impeccable reference applied:** `.agents/skills/impeccable/reference/android.md`, `operate.md`, `craft-floor.md`, `animate.md`, `audit.native.md` (source inspection; no binary/hook execution).

**Emil reference applied:** `.agents/skills/emil-design-eng/SKILL.md`, `.agents/skills/review-animations/SKILL.md` (motion that communicates state; limited to restrained fade and progress feedback; avoid bouncy, non-interruptible sequences).

## Known next steps

1. Run the manual Android build/test workflow, inspect errors and fix any build issues.
2. Test on physical Android device with Android font scale 1.0 and 1.3, TalkBack, dark mode, screen rotation, back gestures and no network.
3. Verify secure plan read/write/reopen and failure scenarios, including rapid simultaneous taps, low storage, restart and app updates.
4. Review typography and contrast in **actual emulator/device screenshots**, then refine spacing and illustration to better match the selected mockup.
5. Secure expert content pipeline, legal review, real official-source metadata and privacy notice before a wider pilot. No live alert claims unless independently implemented and verified.
