# Offline food pantry + barcode scanning prototype

**Status:** Source implementation on a feature branch; no verified Android build, APK or P30 hardware test yet. This is a founder-authorised Android prototype expansion; it does not approve a production launch or change Phase 0 validation gates.

## User flows

1. Open **Supplies → Food pantry**. The original eight-item supplies checklist remains under **Supplies checklist**.
2. **Scan** requests camera permission contextually, displays a native preview and recognises standard food EAN/UPC codes with bundled ML Kit + CameraX. Barcode detection works offline and does not require Google Play Services. Closing scanner releases camera resources.
3. After a scan, if that barcode already exists locally, show its edit dialog with quantity proposed as +1 (never silently save duplicates). Otherwise, make a best-effort Open Food Facts lookup and prefill name, brand and package size. If no result/network, present a blank name with scanned barcode retained for manual completion.
4. **Add item** allows entirely offline manual entry. Edit name, brand, package size, quantity (1–9999), optional barcode, date on packaging (ISO YYYY-MM-DD), storage location and emergency-reserve marker.
5. Search stock; increase/decrease quantity; edit; confirm deletion; track number of packages and reserve packages. Items persist in an app-private SQLite database across process death and device restart.
6. Entered date status is displayed as passed / within 30 days / future. No expiry push notifications, automatic camera expiry OCR, or dietary-sufficiency calculations have been implemented.

## Data, privacy, licensing

- All personal stock information stays in local SQLite, protected by the Android application sandbox. No analytics, accounts, sync, backup or personal data are uploaded. Android backup and device transfer are disabled app-wide.
- CAMERA is requested in context and used only for barcode image analysis. Frames are processed on-device; they are not uploaded.
- INTERNET is used for a **barcode-only** product lookup at `https://world.openfoodfacts.org/api/v2/product/{code}.json`. Requests include a descriptive User-Agent and a six-second connect/read timeout. Open Food Facts returns user-contributed data that may be incomplete, wrong or unavailable. Do not present its product descriptions as verified.
- Open Food Facts data is available under the **Open Database License (ODbL)**. Attribution is visible in-app. Reuse obligations, sharing/exports, and license compliance must be evaluated before distribution.
- Manual entry and offline barcode detection work without network; product identification is online-only. The app never infers expiry date from the barcode.
- Food-count statistics are package counts, **not** calories, meals, days of supplies, food safety or a measure of preparedness.
- A date entered from the package is a user reminder, not food safety advice. No automated disposal recommendations. Always consult the packaging.

## Developer validation

Requires Android SDK 36, Gradle 8.13, JDK 17. Commands:

```bash
bash scripts/check_domain.sh
python3 scripts/static_qa.py
gradle :app:testDebugUnitTest :app:assembleDebug --no-daemon
```

The Actions debug workflow is **manual-only** and must be deliberately dispatched; do not trigger it merely by pushing. The generated APK is a debug artifact, not a verified release.

### P30 manual smoke test checklist

- Grant and deny camera permission; scan EAN-13, EAN-8, UPC-A / UPC-E on actual food packs; close scanner during active recognition.
- Airplane mode: scan barcode, wait for failed lookup, enter product by hand and save; verify no crash when network later returns.
- Scan the same barcode twice; verify quantity proposal is not automatically saved until user confirms. Reopen and verify count.
- Enter quantities 0/1/9999/10000; invalid and malformed dates; long names; emoji; empty search; 100+ products.
- Restart app; reopen Pantry and original Supplies checklist; confirm both sets of data persist without overwriting one another.
- Verify date reminder boundaries and no misleading 'days of food' claims.
- Test dark mode, 1.3x font scaling, 320dp width, talkback, system Back, screen rotation, and permission revocation while app is running.

### Known limitations / next phase

No household sync or CSV export; no multi-lot expiry support (one date per inventory line), no numeric servings/calories or stock valuation, no item photo downloads or live product database caching; no expiry OCR or background reminders. Those require additional design/privacy/safety review. Current code is not represented as hardware-verified.
