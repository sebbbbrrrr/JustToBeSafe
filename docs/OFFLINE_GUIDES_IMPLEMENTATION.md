# JustToBeSafe: full in-app offline guides (development implementation)

## Product decision
Users must be able to read the **complete guide content** inside the native Android app without an internet connection, webview, third-party browser, sign-in or subscription. Source attribution, dates and publication/review status should also appear locally. Optional links to live official sources are supplementary and must never be the only way to obtain the guide's body.

## Implemented in this branch
- The APK bundles original English UK urban preparedness article text at `app/src/main/assets/guides/uk_urban_guide_articles.json`.
- All 12 catalogue topic IDs have an article; the Power Cuts article includes seven substantive sections.
- `OfflineGuideLibrary` loads the assets through `Context.assets`, validates their internal-draft state, checks duplicate topic/section IDs and refuses unsupported schema versions.
- `GuidesScreen` displays every article section and paragraph as standard accessible Compose text, with search at catalogue level.
- The article body is independent of the network, logged-in account, URL availability and source-page changes. The app displays recorded source URLs and an explicit offline draft-review status.
- Existing optional links out to the UK government Prepare website are supplemental rather than required to read the article.
- `scripts/check_offline_articles.py` checks complete article coverage, meaningful section text and unreviewed draft status before the Android build.
- All other prototype tabs are preserved.

## Content and licensing boundaries
This is **originally drafted explanatory text**, not mirrored or scraped third-party articles. It does not imply government endorsement or permission to reproduce individual publisher's diagrams, photographs, illustrations or full texts. The UK Open Government Licence frequently permits appropriate reuse of qualifying Crown copyright with conditions, but applicability must be verified for each actual source, including material owned by third parties.

The content has **not** passed professional safety review. This branch retains the explicit unreviewed draft labelling in the UI. It is a development test artifact, not an approved public-safety publication.

The reference URLs in the catalogue are currently editorial source candidates. They are provided as offline-readable metadata, not as verified citations supporting each individual claim. Before release add:
- A documented original publication / last-verified date for each source.
- Publisher name, license and attribution statement, with reuse exceptions addressed.
- Claim-level source mapping and subject-matter reviewer sign-off.
- Final geographic applicability and appropriate national/regional variations.
- Update triggers, next review date, withdrawal mechanism and immutable releases.

## Required future release architecture
1. Keep a local, versioned **offline baseline** of every public article and the source/review details needed for understanding it.
2. Store approved articles as immutable snapshots with per-release content hashes; maintain a signed, verified update manifest so downloaded changes cannot silently alter safety procedures.
3. Download updated review-approved packs when connectivity returns, validate before activation and retain a last-known-good version for rollback.
4. Clearly separate content publication/review dates from **live incident warnings**, which require current connectivity and suitable providers.
5. Mark stale, superseded or withdrawn advice prominently and avoid implying offline content reflects today's conditions.
6. Let users manage downloaded storage without removing essential baseline guides.
7. Protect access to household-specific personalised details separately; personal contacts and medical data must not be embedded in the globally shipped content catalogue.
8. Test cold launches in airplane mode, fresh installs, device restarts, missing files, low storage, expanded accessibility fonts and screen-reader reading order.

## QA
Use `python3 scripts/check_urban_guides.py` and `python3 scripts/check_offline_articles.py` before compiling. Then use GitHub Actions' Android APK build and verify actual navigation, accessibility and offline behaviour on a Huawei P30. A passing Actions build verifies compilation and included content, not physical-device behaviour or external safety-review status.

## Trusted primary source candidates
- https://beready.campaign.gov.uk/be-informed-about-hazards/power-cuts/
- https://beready.campaign.gov.uk/be-ready-for-emergencies/
- https://www.gov.uk/government/publications/food-safety-in-a-power-cut-advice-for-consumers/food-safety-in-a-power-cut-advice-for-consumers
- https://www.foodstandards.gov.scot/consumer-advice/food-safety/food-safety-in-the-kitchen/power-cuts
- https://www.nationalarchives.gov.uk/doc/open-government-licence/version/3/
