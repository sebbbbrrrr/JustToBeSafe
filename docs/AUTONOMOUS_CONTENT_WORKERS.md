# JustToBeSafe — Autonomous Offline Content Worker Contract

**Scope:** Five scheduled editorial-content workers, each with an exclusive topic domain. These create drafts for the offline knowledge library, not approved public emergency guidance.

## Mandatory EVERY-RUN quality gate
1. Read root `AGENTS.md`, `PRODUCT.md`, `DESIGN.md`, `docs/OFFLINE_SEARCH_BULK_ROADMAP.md`, `docs/OFFLINE_KNOWLEDGE_LIBRARY.md` and the local skill orchestrator `.agents/skills/justtobesafe-design-workflow/SKILL.md`.
2. Before authoring or publishing a batch, **read the pinned locally downloaded skills**, relevant to the planned content:
   - `.agents/skills/impeccable/SKILL.md` and its relevant `audit`, `critique`, `harden`, `polish` workflow references, if present, for readability, structured scanning, accessibility, consistent information hierarchy, and visual representation.
   - Emil Kowalski's installed design-engineering skills in `.agents/skills/`, especially `emil-design-eng`, `break-ui`, `review-animations`, when guide cards or illustrations need visual/interaction work.
   - Do not claim a skill was used unless its actual instruction file was read this run. Use the skill only for what it is designed for; these **design skills cannot substitute for medical, electrical, fire, public-health or other subject-matter checks**.
3. Check open branches/PRs and all `content/batches/*.json`, plus existing baseline and urban articles, for overlapping titles, IDs and concepts **before** creating a new batch. Avoid simultaneous edits to the same files. Never commit to `main` or another worker's feature branch.
4. Produce **up to 25–50 substantively useful ORIGINAL articles** in one independent batch only when quality allows. Fewer is better than redundant, shallow or invented material. Each article uses the schema from `scripts/import_knowledge_batches.py`: stable ID, title, category, summary, searchable tags/aliases, risk, illustration reference or null, review=`pending`, and at least 2 substantial sections with accurate and readable paragraphs. File location: `content/batches/<domain>-YYYYMMDD-<unique-run-id>.json`. Never overwrite an earlier batch.
5. Safety: keep medical, electrical/mains, structural, chemical, fire and other hazardous procedures at an informational, non-actionable overview/triage level unless independently professionally reviewed. All new content remains `internal_draft` and `review=pending`. Do not imply first-aid or repair procedures are approved. When source research is necessary for correctness, check dependable sources; do not invent exact thresholds, legal claims, rescue procedures or current alerts. Respect licensing; do not copy long passages or third-party artwork.
6. Illustrations: when needed, create an original, low-size vector/schematic with a text alternative (alt description). Do not put ambiguous or unreviewed step-by-step hazardous instructions in a visual. Reuse existing artwork where relevant. Check text and diagrams for 320dp width, large font accessibility, high contrast and offline rendering. If the currently available tool cannot create the image/asset, include a clearly scoped illustration specification rather than claiming a graphic exists.
7. **Before proposing a PR**, run, when available: `python3 scripts/import_knowledge_batches.py`, `python3 scripts/build_knowledge_packs.py`, `python3 scripts/check_knowledge_packs.py`, `python3 scripts/build_fulltext_index.py`, `python3 scripts/check_fulltext_index.py`, `python3 scripts/check_urban_guides.py`, `python3 scripts/check_offline_articles.py`, `python3 scripts/static_qa.py`, and relevant Android tests if infrastructure is available. Record successes and explicit failures. If shell or runtime tools aren't available, do source-level validation where possible, and report unrun checks as blocked.
8. **Skills QA checklist attached to each batch report/PR**: actual skill paths opened; content hierarchy/scanability; terminology/labels; accessibility; image alt/captions; mobile layout impact; reduced-motion relevance (usually none); duplication/scope; safety and rights; offline and compression integrity. Do not claim the skills proved technical accuracy.
9. Commit only to the worker's unique branch from the latest agreed integration base `feature/offline-fulltext-search-and-bulk-pipeline` or its successor. Open a **draft PR** targeting that base; don't merge automatically, don't deploy or publish reviewed claims, and avoid unnecessary GitHub Actions builds. If cannot write to GitHub, produce the batch in the current run output with exact blocker and do not claim it was committed.
10. End each run with count of unique articles actually committed, PR link or exact blockers, files changed, installed skills inspected, static/tests result, duplicate scan summary, estimated new compressed bytes if measured, and next unfilled topic gaps. Never manufacture counts or test evidence.

## Exclusive editorial domains
- **W1 first aid:** basic first-aid concepts, household health and hygiene; qualified clinical review required for any treatment procedures.
- **W2 home utilities:** nonhazardous home maintenance, equipment recognition, property and plumbing; mains electricity, gas, structural repairs need competent oversight.
- **W3 food & water:** shelf-stable food, storage, hygiene, sanitation, water provider notices; food-safety and public-health review for treatment advice.
- **W4 urban disruption:** common outages, weather, communication, transport, floods, household plans, emergency contacts; national and local distinctions.
- **W5 outdoors:** navigation, exposure awareness, equipment and practical outdoor preparation; hazardous survival methods need appropriate review.

## Conflict policy
- Use one unique branch + one unique batch path per worker/run.
- Re-fetch base and detect duplicates immediately before commit.
- Never force-push, modify another worker's branch or merge an unreviewed batch.
- If a prior run is still unresolved or a PR overlaps, evaluate and resolve duplicates before creating another PR.
- An automation is a **scheduled assistant execution**, not a guaranteed always-running server process. GitHub writes/builds and skill access depend on tool connectivity in that run.
