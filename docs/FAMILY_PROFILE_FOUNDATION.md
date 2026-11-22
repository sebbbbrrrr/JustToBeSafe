# Family/Profile foundation — first accelerated Android slice

Status: source changes on an isolated branch. No Gradle build, APK install, camera-device test, authenticated sharing, or multi-user cloud connection has been verified.

## Delivered in this slice
- Five primary navigation entries: Home, Inventory, Scan, Plans, Profile.
- Guides remains available from Home (not deleted).
- Inventory defaults to the existing pantry view; the existing supplies checklist remains a secondary section.
- Scan opens the existing native barcode scanner; when an item is saved from Scan, scanning resumes. Closing scanner returns to Inventory.
- Profile includes a Household screen, a locally editable household name (60-character validation) and a plainly identified single-device owner.
- Household name persists in app-private preferences. Existing pantry SQLite and encrypted plan storage are untouched.
- Family invitations and role management are deliberately described as unavailable instead of faking multi-user synchronisation.

## Known prototype limitations
- Barcode scanning from the main Scan destination still uses the existing editor dialog; the designed compact continuous bottom-sheet and deliberate identical-rescan counting are future work.
- Household name is a non-sensitive local display label, not a secure identity.
- No accounts, invitations, household permission enforcement, sharing, dependent profiles, notifications, or family sync are implemented.
- The existing guides and safety content remain prototype material; no official live alerts are connected.
- No backend, subscriptions, payment integration, new dangerous permissions or GitHub Actions jobs have been introduced.

## QA checklist before merge or any APK claim
1. Build with JDK 17 / Android SDK 36: `gradle :app:testDebugUnitTest :app:assembleDebug --no-daemon`.
2. Run `bash scripts/check_domain.sh` and `python3 scripts/static_qa.py`.
3. Test navigation on Huawei P30: Home -> Guides -> Back, Inventory -> Pantry/Checklist, Scan -> Camera -> Save -> Scan, Scan -> Close -> Inventory, Plans, Profile.
4. Verify household name persistence after process death and app restart, invalid/empty name rejection, 60-character names, Android Back, landscape, large font and dark theme.
5. Check scanner permission denial, offline barcode lookup fallback, multiple scans and proper camera release.
6. Check all touch targets, Material 3 insets, reduced motion, screen readers and smaller display widths.
7. Before building real family accounts: threat-model invitations, RLS, shared stock event sync, revocation, offline cached data and child/dependant privacy.

## Next slices
F2: secure account, invitation and membership model (requires approved privacy/security architecture).
F3: household-scoped inventory and offline sync.
F4: shared plans/tasks with permissions.
F5: dependant/pet planning with minimised data.
F6: ownership transfer, deletion/export, revocation and security QA.

Design sources: vendored Impeccable native Android / Operate / Shape guidance and Emil design-engineering interaction guidance. This is not a verified screenshot-based design review.
