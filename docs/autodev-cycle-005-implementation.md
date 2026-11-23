# Cycle 005 — Android offline search snippets (implementation)

Frozen task: JTS-AUTODEV-005. Branch: `autodev/cycle-005` from `feature/offline-illustration-pipeline-throughput`.

## Changes
- `OfflineSearchPresentation.kt`: pure bounded (180 characters maximum) paragraph excerpt from installed article text; matching tokens reuse the existing full-text tokenizer, with no index format changes. Missing bodies and unmatched paragraphs return null rather than fabricating a quote. Explicit catalogue, full-text and metadata-only index states.
- `GuidesScreen.kt`: visible result rows only read knowledge bodies when the ID matched the index but metadata did not. Urban rows read already-installed urban article sections. Preserve existing ID navigation, filters, draft notices and offline-only behaviour. A missing or failed index produces a title/topic-only notice; empty full-text matches preserve no-match display.
- `OfflineSearchPresentationTest.kt`: deterministic installed-body fixture, urban fixture, Unicode/case, prefix, middle/end, truncation, missing body, whitespace, blank query, empty result and corrupt-index states.
- Read-only Android QA workflow on the task branch. Central reusable Android job was attempted, but failed before Gradle because the approved integration base has no `gradlew`; task caller now provisions Gradle 8.13 directly without adding a binary wrapper or modifying shared CI.

## Scope and review
No network calls, index schema/assets, safety guide publication, new dependencies, permissions, analytics or production changes. No article bodies are eagerly loaded: only rows composed by LazyColumn perform on-demand pack retrieval, retaining the existing two-pack LRU. Snippet display uses two-line ellipsis and preserves unreviewed draft labels. This is a local development prototype, not reviewed emergency guidance.

## Validation
Initial central reusable Android CI: [run 45172971644](https://github.com/sebbbbrrrr/JustToBeSafe/actions/runs/45172971644) failed with exit 1 at `test -f gradlew`, before any unit tests or compilation. It is an execution configuration defect, not a confirmed Kotlin defect. Replacement task-specific CI must be checked on its exact head; until then Kotlin tests, APK build, UI rendering and Huawei P30 remain unverified. Vendored skill consulted: `.agents/skills/justtobesafe-design-workflow/SKILL.md`; no external design hooks executed.

## Worker 3 refinement — 23 November 2026
- Reviewed frozen cycle-005 acceptance criteria and Android QA profile against the installed-body implementation, accessibility labels, category filters, index error states and unreviewed safety notices.
- For urban body-only search matches whose local article cannot yield a real excerpt, show an explicit non-quoted availability fallback instead of a blank snippet; preserve title, priority, draft badge and navigation.
- Corrected a regression fixture that previously used a literal backslash-n instead of a real newline; added a surrogate-pair boundary test for Unicode excerpt clipping.
- Reviewed repo-local `.agents/skills/justtobesafe-design-workflow/SKILL.md`, `.agents/skills/impeccable/SKILL.md`, `.agents/skills/impeccable/reference/harden.md`, `.agents/skills/impeccable/reference/craft-floor.md`, and `.agents/skills/impeccable/reference/android.md`. No executable hooks or downloads were run.
- Refinement commits: `c54be7cb602a7eb51d8241e2f164e2c3993ffb16`, `bd7d2b97e6c6ac8af810eaa370ca1a6176498622`, `1f92db73a0eb2f305a58917c539919138778374d`.
- Exact-head Gradle unit tests and `:app:assembleDebug` remain **unverified** pending public Actions evidence on the final head. Earlier run `45173120364` failed at a previous head and is not a valid pass for this refinement. Physical Huawei P30, emulator screenshots, font scaling and screen reader remain unverified.
- QA must confirm exact-head Actions run, job logs, command exit codes, Android compile, deterministic tests, and static scope/accessibility review. No merge or release is authorised.
