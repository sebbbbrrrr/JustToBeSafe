# JustToBeSafe — Offline Search & Bulk Knowledge Production Roadmap

## Current delivery: full-text search foundation (development branch)

The Android reader now performs **offline search over the complete article text** as well as titles, categories and aliases. The index is built on GitHub Actions; the runtime never calls a search API or opens a web page to find an answer.

Index architecture:
- One article ID is stable across releases and maps to the existing guide readers.
- Python compilation reads the validated, deterministic bulk-import result and all 12 existing urban guides.
- The full-text index uses Unicode alphanumeric tokens, a short explicit stopword list and 4–8-character word prefixes for simple related-form matching.
- Documents are indexed by token -> sorted article IDs.
- The 16 gzip index shards are keyed by SHA-256 first hex nibble; search requests read only necessary shards, then intersect postings for an **AND** match.
- Android retains only four inflated shards in an LRU cache, as well as its existing two decoded article packs. Avoid loading all articles into memory.
- Full text can match a guide even when its title and summary do not contain the query; category chips can still narrow results.
- Results come from installed APK assets and are available with airplane mode enabled.

This prototype has no language-specific morphological stemmer, spelling correction, typo tolerance, phrase proximity ranking or in-article highlighted snippets. Those belong to later stages. Search coverage cannot imply that a guide is medically or technically approved.

## Bulk content workflow

Source of truth:
- `content/offline_knowledge_articles.json` — initial independently authored library.
- `content/batches/*.json` — further editorial batches, each with a unique file name and no more than 500 entries. First example: `household-essentials-001.json`.
- `app/src/main/assets/guides/uk_urban_guide_*.json` — legacy urban library, indexed alongside the new packs.

Build steps executed by CI in order:
1. `python3 scripts/import_knowledge_batches.py`
2. `python3 scripts/build_knowledge_packs.py`
3. `python3 scripts/check_knowledge_packs.py`
4. `python3 scripts/build_fulltext_index.py`
5. `python3 scripts/check_fulltext_index.py`
6. Existing urban metadata, static QA, domain tests, Android unit tests and debug APK assembly.

Generated, not manually edited:
- `content/.generated/knowledge_articles.json` (validated merged source).
- `app/src/main/assets/knowledge/manifest.json` and `pack_NN.json.gz`.
- `app/src/main/assets/search/manifest.json` and `terms_N.json.gz`.

The import validates stable IDs, categories, tags, sections and paragraph completeness; it **rejects duplicate IDs** and content marked reviewed or published without an independent approval system. It does not automatically manufacture clinical, electrical, building or chemical procedures.

## Scaling plan

**S1 (this branch) — working full-body search and deterministic bulk importer:** first three new batch articles, offline index for 12 urban and 11 practical topics, byte-size measurement, GitHub Actions validation and APK test.

**S2 — relevance and search UX:** ranked title match then alias then body match; snippets with matching terms, recent searches only with clear privacy choices, keyboard navigation and search latency test.

**S3 — migration:** consolidate legacy urban JSON into the same compressed pack schema while preserving stable article links and source/review metadata. Do not duplicate article bodies in the APK during migration.

**S4 — 100 / 1,000 / 10,000 article benchmarks:** generate synthetic test fixtures **only in CI**, not user-visible guides. Benchmark index size, build time, cold query latency, low-end P30 memory, APK and installed sizes separately. Decide an explicit performance budget from measured device results.

**S5 — robust publication workflow:** editorial drafts -> technical and safety review (where applicable) -> approved revision -> signed release. Store per-claim source/reviewer notes privately in production editorial tools; the user-facing guide may use compact attribution if exact citations aren't required. Avoid implying unreviewed first aid/electrical advice is authoritative.

**S6 — media pipeline:** make original simple diagrams in vectors, share reusable diagram primitives and alt text, use compressed bitmaps only when diagrams cannot carry the information. Maintain a referenced media registry to eliminate duplicates across guides.

**S7 — optional advanced offline packs:** the essential safety baseline remains included in the app; optional large reference domains can be downloaded and versioned beforehand and used completely offline afterward. Make availability and version obvious. Downloads must have integrity verification and rollback.

**S8 — international expansion:** locale-specific packs with separate regional emergency numbers, licensing, professional review and search indexes; never silently mix UK guidance with other countries' procedures.

## Acceptance criteria before public launch

- All installed article bodies and required diagrams are readable in airplane mode with no login or external website.
- Search covers titles, aliases, category, heading and article paragraphs, with category filtering.
- Typing a query must not trigger external requests, permission prompts or analytics leakage of sensitive searches.
- Missing/corrupt index or pack gives honest fallback/diagnostic messaging rather than fictitious results.
- Cold startup, query response, RAM and final installed storage are tested on the Huawei P30 and other constrained Android devices.
- 100% stable-ID coverage between article sources, pack manifest and full-text index.
- Changes in tokenization invalidate and rebuild the index by version.
- Health, electrical, fire, hazardous material and other high-risk instructions receive qualified review before a public release. The current Android debug build is explicitly labelled an unreviewed development preview.

## Content priorities after the pipeline

1. Everyday household readiness, outages and accessible communications.
2. First aid concepts and escalation criteria, subject to clinical review.
3. Food hygiene, storage, indoor comfort, sanitation and water.
4. Safe maintenance, equipment checks and nonhazardous home skills.
5. Urban mobility, severe weather, evacuation and housing.
6. Outdoor navigation, basic shelter and remote-location preparations.
7. Specialised infrastructure or less common incidents with regional review.

Shipping more text is not equivalent to a useful handbook: depth, findability, readability, clear safety limits and verified functionality matter more than article count.
