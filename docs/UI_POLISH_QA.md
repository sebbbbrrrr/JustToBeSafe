# JustToBeSafe — Calm Utility Android UI QA

**Status:** prototype design pass, not a device-verified release. **Build has not been run on Android.**

## Applied source-based design standards

- **Impeccable** `android`, `operate`, `craft-floor`, `animate`, `audit.native`: Material 3 components, bottom navigation / rail by width, contrast-aware colour roles and dark theme, consistent card shape and type scale, error/empty states, honest preview copy.
- **Emil Kowalski** `emil-design-eng`, `review-animations`, `break-ui`: purposeful 170–250 ms motion that acknowledges state or preserves navigation continuity; no forced page-loading sequences; minimum 48dp touch surfaces; long text allowed to wrap; reduced-motion support.

## Native QA scenarios to run after debug build

| Area | Steps | Expected |
| --- | --- | --- |
| Navigation | Tap Home → Guides → Supplies → Plan → Home; system Back; rapid repeated tabs | No missed taps or stuck transitions; Back returns from detail and non-Home tabs |
| Guides | Search `flood`, search unmatched query, clear, select topic, Back | Correct filtered cards, helpful empty state, no stale query or screen overlay |
| Supplies | Mark/unmark multiple items, rotate, kill/restart app | Single toggle per tap; percent matches checked IDs; persisted state |
| Plan | Enter meeting point; add/delete contact; restart app | Encrypted fields persist; no permissions requested; errors recover without data corruption |
| Offline | Airplane mode, restart, navigate all tabs | Locally stored checklist and topic index work; official external links indicate network requirement |
| Contrast | Light/dark system theme; inspect each screen | Dark image scrim legible, no near-white text on white cards; no colour-only state |
| Accessibility | TalkBack; 1.0x and 1.3x font scales; 320dp width | Readable labels; no overlap; minimum 48dp actions; row toggle announced once |
| Motion | Android Developer Options: animator scale 1x, then off | State changes remain immediate with no forced choreography; no loops |
| Device | Huawei P30 (Android 10/EMUI 12), long content scroll | Fluid taps, no jank on 60Hz, correct system gesture and keyboard insets |
| Tablet | 600dp+ width, landscape | Navigation rail replaces bottom nav, cards remain readable |

## Pantry extension checks

- Verify **Supplies → Food pantry** and checklist tabs remain operable at 320dp, dark mode and 1.3x font scale.
- Open barcode scanner with camera granted / denied and ensure permission rationale and close action remain visible.
- Check editor scrolling with keyboard raised, long product names, numeric bounds, edit/delete confirmation and offline fallback.
- Confirm camera is released on close and inventory persists through restart; test on P30 before representing it as usable hardware footage.
- The pantry branch remains unbuilt and unverified; see `docs/PANTRY_MVP.md` for detailed matrix.

## P0 launch restrictions

- This app **does not** report current hazard probabilities, official live alerts or expert-reviewed instructions.
- Do not publish the mockup's illustrative percentage, fake review dates, phone numbers or location claims as factual.
- No product release without expert/rights review of safety content, privacy/legal checks, Android APK build and real device QA.

## Engineering commands

```bash
bash scripts/check_domain.sh
python3 scripts/static_qa.py
# Android SDK + Gradle 8.13 required:
gradle :app:testDebugUnitTest :app:assembleDebug --no-daemon
```

GitHub Actions includes a manually dispatched Android debug workflow only. It should not be triggered unnecessarily.
