# JustToBeSafe — UK Urban Guide Database Roadmap
**Status:** Editorial database foundation committed as an internal draft, 21 November 2026. No public guidance has been reviewed or released.

## Prioritisation policy
Prioritise everyday or foreseeable household disruptions and highly actionable preparations, then significant lower-frequency risks. Weight:
1. Plausible local occurrence and geographic relevance.
2. Direct impact on household routines, health, accessibility and critical services.
3. Useful preparation that the app can actually support.
4. Cascading effects across food, water, mobility, communications and housing.
5. Quality of official UK source material, rights clearance, and review costs.

This is an editorial order, **not** statistical risk probabilities. Do not derive numeric probabilities from the UK National Risk Register; its national reasonable worst-case risks do not directly indicate a given urban household's event frequency. Re-rank where building type, local council / resilience forum information, flood exposure or household needs justify it.

## Rollout order

| Rank | Topic | Why included | Content dependency / gate | Suggested links |
| ---: | --- | --- | --- | --- |
| 1 | Local power cuts | Common local-service disruption; links to lights, communications and essential equipment | Confirm UK-wide and network-operator sources; medical and electrical safety specialist | inventory: torches, batteries, power banks; plans: power outage |
| 2 | Severe weather, storms, heat, cold | Direct and cascading local effects; regional and seasonal variation | Met Office attribution and relevant health guidance review | plans: severe weather; stock: warmth/cooling |
| 3 | Water supply interruption | Critical household service; drinking, sanitation and medical dependencies | Water provider/local notices, public-health safety review | reserves: water; plans: water outage |
| 4 | Flat/high-rise fire safety | Critical consequences even if household-specific occurrence varies | Fire specialist and the building's **actual fire strategy**; never generalise stay-put vs evacuate | home profile: building policy |
| 5 | Urban flooding and drainage | Flood exposure depends strongly on postcode, geography and building | Environment Agency / devolved administrations and local flood data; local scope | plans: flooding |
| 6 | Mobile / internet outages | Disrupts navigation, payment, family contact and access to warnings | Communications providers / official guidance review | emergency contacts; offline pack |
| 7 | Food, medicine and supply interruption | Useful ongoing household-preparation topic | Food and medical specialist review where procedures involved | pantry inventory; equipment |
| 8 | Public transport disruption | Urban-specific mobility dependency | Local transport data; operational provider licences | household meeting points |
| 9 | Urban evacuation and temporary accommodation | High consequence; requires multi-agency contextualisation | Local emergency authorities / building staff / evacuation specialist | grab bag; dependants |
| 10 | Shared building infrastructure | High-rise lifts, water pumps, intercoms, access | Building-specific property management information | home profile |
| 11 | Public safety incidents and cordons | Less routine; needs trustworthy official instructions | Police / government source and specialist safety review | external verified info |
| 12 | Industrial, chemical, major national infrastructure events | Low utility as a first generic guide, high specialist review burden | Official hazard-specific advice and locality; distinguish live alerts | external verified info |

## Module 1 — Urban Power Cuts: content development backlog

### Audience and boundaries
UK city-dwellers, renters, residents of flats and high-rise properties, and people who may depend on powered medical or accessibility equipment. Guide text must make no unsupported safety or reliability claims. Offline material is not a live power-status or warning feed.

### Article structure and internal editorial tasks
1. **Overview:** Which domestic and communal facilities may rely on electricity; distinguish a household circuit problem from an area outage only with source-approved wording.
2. **Prepare beforehand:** Information to store, household equipment to inventory, personal communications plan, reminders for checking equipment.
3. **During an outage:** Reserve until qualified review. Source electrical safety, food refrigeration advice and support for powered medical/telecare equipment from appropriate official or clinical providers.
4. **Restoration:** Reserve until qualified review. Confirm return-of-service precautions, food safety and contact escalation from source authorities.
5. **Urban building specifics:** Communal doors, lifts, lighting, water pumps, stairwell accessibility, landlord/management responsibilities, no assumption that particular building equipment has backup power.
6. **Household plan fields:** Network operator contact, electricity dependent devices, agreed meeting method, backup communications, building management contacts. All user values must be stored in private household storage, not this public guide seed.
7. **Linked inventory IDs (future):** torch, batteries, power_bank, radio, drinking_water, shelf_stable_food. Never infer safety from stock quantity or task completion alone.
8. **Sources and offline status:** Show link, author/publisher, applicable UK region, checked-at timestamp, rights status, review and next-due date; external source links require internet.

### Source candidates (rights NOT yet cleared)
- UK government household preparation: https://beready.campaign.gov.uk/be-ready-for-emergencies/
- UK government power cuts: https://beready.campaign.gov.uk/be-informed-about-hazards/power-cuts/
- UK government National Risk Register 2026: https://www.gov.uk/government/publications/national-risk-register-2026
- Met Office warnings and climate context: https://weather.metoffice.gov.uk/warnings-and-advice/uk-warnings

Source links are reference pointers only; they do NOT grant permission to reproduce text or prove specialist approval.

### Publication gate (all mandatory)
- [ ] Assign author/editor and qualified safety reviewers (electrical, fire, clinical, public health as applicable).
- [ ] Verify every operational claim against primary official documentation.
- [ ] Check source licences and reuse rights (including images and diagrams).
- [ ] Review for England, Scotland, Wales, Northern Ireland distinctions and building/local exceptions.
- [ ] Capture version, first published, last reviewed, next review date and change triggers.
- [ ] Sign off offline guide snapshots and stale/update messaging.
- [ ] Review accessibility and plain-English reading level.
- [ ] Human QA on Huawei P30 with airplane mode, navigation and large font sizes.

Until ALL publication gates pass, render only draft-safe topic summaries with an explicit unreviewed label. Keep any detailed operational text out of the public UI.

## Data contract

Source seed: `app/src/main/assets/guides/uk_urban_guide_catalogue.json`.

Each topic has an immutable `id`, editorial priority, title, category, geographic scope, tags, guide version, editorial state, review and rights states, source URLs, and typed section stubs. The seed is **bundled static editorial metadata**, not a household database, not a remote alert database and not yet wired into the app's Guides UI.

Future backed content tables:
- `guide_topics`: id, title, category, priority, scope, tags, public state, canonical version.
- `guide_sections`: id, topic_id, ordered section type, reviewed markdown body, revision, offline eligibility.
- `guide_sources`: id, issuer, URL, publication/review date, geographic applicability, access date, attribution and licence.
- `guide_claim_sources`: claim/section to source relations and evidence strength.
- `guide_reviews`: reviewer identity/qualification, sign-off, status, scope, review timestamps.
- `guide_versions`: immutable release versions, checksums and rollback metadata.
- `guide_attachments`: licensed charts/images and required captions/attribution.
- `guide_related_items`: mapping to generic inventory categories and task templates, not user-specific stock.
- `guide_region_rules`: regional applicability and exceptions.
- `guide_update_events`: triggers for stale-content review, corrections and withdrawal.
- `guide_offline_manifest`: signed version/hash references, download size and last verified status.

Household records, precise user location, private member data and inventories **must not** be written into this static public guide library. User-to-guide connections should resolve privately on device or via household-scoped permissions.

## Implementation sequence
**D1 (this branch):** seed 12 internal-draft urban topics with detailed power-cut section stubs and transparent source/review metadata.
**D2 (implemented foundation):** JSON schema-version checks, static catalogue validation, read-only parsing guardrails and regression checks. Full schema migration and content-signing remain future work.
**D3 (implemented for Android prototype):** read-only bundled catalogue loader, searchable Guides UI, Power Cuts section headings, offline index, official external source links, and draft/review labels. No procedure text is displayed.
**D4:** commission specialist-reviewed Power Cuts guide copy; rights check, source-claim mapping and UK regional review.
**D5:** ship versioned offline Power Cuts guide with accessible reading, stale alerts and external official links.
**D6:** repeat for weather/water, then fire, flood and rest of ranked backlog; log sign-offs and corrections.
**D7:** integrate the catalogue with plans, inventory and member-specific tasks without sharing personal data into public content.
**D8:** add remote version updates and rollback only with privacy/security review; never confuse editorial updates with real-time official emergency warnings.

## Verification from this delivery
The seed defines 12 unique topic IDs and 7 Power Cuts section stubs, with `publication_state=internal_draft`, `offline_body_available=false`, `safety_review=pending`. Source and review fields are placeholders; none claim review has occurred.

Android GitHub Actions runs Python metadata validation, static checks, existing domain checks, JVM unit tests and debug APK assembly for this branch. A passing build is required before calling the integrated APK available; physical P30 UX tests and professional content review remain outstanding.

## Evidence
- https://www.gov.uk/government/publications/national-risk-register-2026
- https://beready.campaign.gov.uk/be-ready-for-emergencies/
- https://beready.campaign.gov.uk/be-informed-about-hazards/power-cuts/
- https://www.metoffice.gov.uk/research/climate/understanding-climate/uk-and-global-extreme-events-heavy-rainfall-and-floods
