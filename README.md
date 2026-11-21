# JustToBeSafe

**Working title — brand clearance pending.**

JustToBeSafe is a proposed UK-first, Android-first household emergency preparedness app. It aims to make ordinary preparation more approachable, with region-appropriate guidance, household planning and supply organisation.

> **Status: Phase 0 — validation.** This repository currently holds the product brief and validation plan. There is no production app, expert-approved safety content, or published legal copy yet.

## Product principles

- **Safety first:** Essential emergency guidance and official warnings should not depend on a paid subscription. Basic emergency guidance should be accessible offline.
- **Offline-first:** Prioritise reliable core functionality without a connection; distinguish cached material from live alerts.
- **Household-focused:** Preparedness plans should be practical for ordinary families and individuals, not survivalist-only audiences.
- **Privacy by default:** Minimise collection of personal, household and location data. Never commit real customer data, credentials or location records.
- **Trust:** Cite sources and review dates for all safety information. Require appropriately qualified expert approval before publication.
- **Honest boundaries:** The app is a supplement to, never a replacement for, official emergency guidance or emergency services.
- **Validation before scale:** Test demand, retention and willingness to pay before large development or international expansion.

## Roadmap

1. **Phase 0 — UK validation:** Identify target households, study competitors, create a landing page/waitlist, interview prospective users and test willingness to pay.
2. **Phase 1 — Android UK MVP:** One pilot region; reviewed guides and essential contacts, robust offline access, household plan builder.
3. **Phase 2 — Engagement and subscription experiments:** Inventory, expiry reminders, household collaboration, safe alert integration (subject to feasibility and review).
4. **Phase 3 — Growth:** Family/group plans, selective B2B pilots, iOS before US, then appropriately localised European markets.

**Phase 0 does not assume the MVP is approved.** See [Product Brief](docs/PRODUCT_BRIEF.md) and [Validation Plan](docs/PHASE_0_VALIDATION.md).

## Decisions not yet made

- Final brand / legal trademark availability (JustToBeSafe remains provisional).
- Subscription pricing and tier boundaries.
- Final tech stack (Flutter is a candidate, not a commitment).
- Expert partners, content licensing and any legal wording.
- Exact pilot UK region and user acquisition budget.

These decisions require founder approval and, where appropriate, qualified external review.

## Public repository hygiene

This is a public repository. Do not store secrets, personal contact lists, household emergency plans, emails from the waitlist, API keys or private location information here. Keep credentials in an approved secret manager and any customer records in appropriately secured systems.

## Contribution workflow

Plan changes on feature branches and review via pull requests. Preserve source provenance, licensing evidence, expert-review status and last-reviewed dates for each safety content item once the content pipeline begins.


## Android Calm Utility UI prototype

A native **Kotlin / Jetpack Compose** Android proof of concept is available on branch `feature/android-calm-utility-prototype`; its visual refinement is proposed on `feature/android-calm-utility-visual-polish`, following the selected Calm Utility concept. See [Android prototype documentation](docs/ANDROID_PROTOTYPE.md) for build commands, scope, security boundaries and unverified checks. The [UI refinement QA checklist](docs/UI_POLISH_QA.md) details the new screens, native previews and device tests still required. The prototype includes Home, Guides, Supplies and an encrypted local Household Plan; it is not a live emergency-alert service and publishes no unreviewed safety procedures.

To compile in an Android development environment with Android SDK 36, Java 17 and Gradle 8.13: `gradle :app:testDebugUnitTest :app:assembleDebug`. A GitHub Actions workflow is supplied for **manual** debug APK builds; it does not trigger automatically.

## Prototype pantry inventory (development branch)

The separate feature branch `feature/pantry-barcode-inventory-mvp` extends the Calm Utility Android prototype's Supplies page with a searchable offline pantry, manual stock updates, an on-device CameraX/ML Kit barcode scanner, and optional online Open Food Facts product-name lookup. Permission prompts occur only when scanning; only the barcode is sent to the lookup provider. No emergency-duration or nutritional adequacy claims are made.

This feature is **prototype source code awaiting an Android build and physical-device QA**, not an already-shipped application. See [Pantry MVP implementation and test notes](docs/PANTRY_MVP.md).
