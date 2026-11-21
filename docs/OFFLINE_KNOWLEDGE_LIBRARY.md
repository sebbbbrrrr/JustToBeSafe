# JustToBeSafe — Offline Knowledge Library v1

## Product principle
The product is a **native, searchable, encyclopaedic preparedness library** covering household skills, first aid, utilities, food and water, equipment, navigation, weather, outdoor skills and other life-disruption scenarios. The full body of every installed article must be available with airplane mode on, without login, browser, subscription or an internet connection.

Unlike a bookmarks app, article content is bundled locally. Official live warnings and incident-specific instructions necessarily remain separate, because a static package cannot know current conditions.

## Current implementation (v1)
- Eight additional multi-section practical articles, across five categories: First aid, Home & utilities, Food & water, Communication and Equipment.
- Twelve existing urban disruption articles preserved in their original APK assets. Combined developer library: twenty topics.
- New articles written as **original editorial drafts** to avoid copying third-party published content. No claim of professional review or government endorsement.
- A small searchable metadata index (`manifest.json`) generated at build time.
- Category-specific gzip-compressed packs generated from `content/offline_knowledge_articles.json`.
- Lazy loading: only when a selected practical article is opened. An LRU cache retains at most two decoded category packs.
- Categories and search match titles, categories, summaries and synonyms/tags, case-insensitively. Full-body FTS is **not** in v1.
- Two compact original Android vector warning illustrations: burns/scalds and damaged mains cable. These are awareness graphics, not operational medical or electrical instructions.
- Clear visible distinction between offline editorial drafts and reviewed emergency instructions.
- Build validation: all article IDs, sections, complete pack reconstruction, draft gates and Android compilation.

## Compression strategy
1. **Text-first semantic publishing:** store paragraphs and headings as UTF-8; avoid full HTML, embedded Base64 media, repeated layout and duplicated source details.
2. **Compile from author-friendly JSON:** canonical compact JSON encoding with sorted keys and no redundant whitespace, then gzip level 9 with a reproducible timestamp (mtime 0). Category pack bundling allows repeated words to compress together.
3. **Small index, lazy article packs:** keep only summary/tags/title/category searchable in memory at startup. Decode one category at a time, retaining two packs. This is about RAM performance as much as file size.
4. **Original vectors for schematic illustrations:** Android VectorDrawable XML scales without bitmap compression artifacts and consumes very little storage. For photo-quality material in later phases use modern compressed imagery and measured density sizes, but prefer diagrams when genuinely useful.
5. **Deduplicate reusable icons and diagrams:** future articles should reference shared asset IDs instead of storing a separate graphic per page.
6. **Do not count apparent raw-vs-gzip savings as APK savings:** Android packaging also compresses suitable assets. Measure release/debug APK output and installed size, which are different. Compression is neither unlimited nor free: decoding and random access have costs.
7. **Future large-scale content:** add read-only SQLite FTS5 or compact inverted indexes for full text search, sharded by language/topic. Benchmarks needed before adopting Brotli, Zstandard or custom dictionary formats; avoid new native libraries until measured.
8. **Incremental packs:** core essentials must be present in the base APK and load entirely offline. Optional advanced subjects may be delivered as opt-in offline downloads; once downloaded, their articles and images must remain readable offline. The user should see offline/installed state before a crisis.
9. **Versioning:** add approved immutable article revisions, hashes, signatures for downloads, geographic applicability, reviewer/author metadata, corrections and rollback. Source rights and expert review remain release gates.

## Editorial development order
1. Local utility disruption; essential household equipment, communication, sanitation.
2. Basic first aid and home safety, **with qualified review before publishing actionable clinical/electrical procedures**.
3. Food and water storage, household repairs and personal documents.
4. Housing and building systems, local transport and evacuation planning.
5. Outdoors, navigation and weather exposure; specialist review where needed.
6. Advanced technical reference and niche contingency scenarios, after safety/legal risk classification.
7. Broader regions, languages and specialist packs only after core library completeness and offline usability.

## Content schema
`content/offline_knowledge_articles.json`: `id`, `title`, `category`, `summary`, `tags`, `risk`, `illustration`, `review`, `sections[{id,title,paragraphs[]}]`. IDs remain stable across releases.

Compiled `app/src/main/assets/knowledge/manifest.json`: lightweight entries with `pack` file mappings.

Compiled `app/src/main/assets/knowledge/pack_NN.json.gz`: versioned category shards, including section content. Only derived assets are produced in CI before compilation; the plain source is retained in Git for editorial review.

## Future authoring and artwork
- Structured guides may grow instructional diagrams, flow charts, checklists, printable summaries, alt text, supported accessibility formats and internal cross-links.
- Prefer original, locally bundled artwork. Don't reproduce copyrighted photographs or guides without permission.
- Medical, electrical, structural, fire, chemical and other hazardous **how-to** advice needs appropriate independent professional review, regardless of whether the founder requires formal bibliographies.
- For a mains cable, do not present user-facing rewiring procedures without a defensible qualified safety process. For burns, medical first-aid procedures and illustrations should be clinically reviewed.

## QA acceptance
- All selected articles and illustrations load in airplane mode after cold start.
- Search works without network or account.
- Selected article never silently falls back to an external website.
- Browser links are supplemental only and clearly labelled online.
- Compress/decompress round trip, article ID completeness and pack paths are validated in CI.
- Faulty/missing packs show an error rather than invent content.
- Long articles, font scale >= 1.3, Android Back, rotation, dark mode and TalkBack reading order are tested on Huawei P30.
- Build APK size reported separately from content pack size; monitor memory peaks and latency.
- No published safety instruction appears with draft review status.

## Current limitations
Only eight new practical articles added in this slice; broad coverage and full body search are future work. The twelve urban guides are still in their older JSON format, and will be migrated into the unified compiler after the reader is proven. Content remains **unreviewed** and should not be marketed as verified first aid or electrical repair instructions. No physical P30 testing has occurred in this branch.
