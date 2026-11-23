# AutoDev QA selection matrix (risk based)
Version 1 — JustToBeSafe development prototype. This policy does not waive expert review of public safety instructions or release approvals.

## Rule: test what changed, plus its real dependencies
Worker 1 chooses exactly one primary QA profile and records `qa_plan` in the frozen ledger BEFORE Worker 2 begins:
```json
{"profile":"python-index","required_checks":["..."],"optional_checks":["..."],"evidence_scope":"...", "run_budget_minutes":12, "risk":"low|medium|high"}
```
Worker 2 may add stronger checks but cannot silently remove frozen required checks. Worker 3 reviews coverage. Worker 4 checks the plan against the ACTUAL diff and may strengthen it if risk was underestimated; record reason. If significant requirements differ from the frozen plan, send back to Architect for approval rather than silently weakening tests. Do not mark QA passed without required exact-head CI evidence. Missing unrelated test suites do not block when their affected modules are unchanged.

## Profiles
| Profile | Use | Required evidence | Not implied |
|---|---|---|---|
| docs | Documentation/roadmap-only text | Documentation integrity, diff check, factual/source review where needed | Python/Android build |
| python-isolated | Standalone Python helper, benchmarks, deterministic fixtures | Focused unittest/CLI tests and side-effect checks on exact SHA | Android functionality |
| python-index | Index builder, verifier, tokenizer or offline search data formats | Focused fixtures AND full source import/pack/index build/validation on exact SHA, or reasoned narrow integration alternative proven equivalent | Android UI/device |
| python-content | Offline article pack/importer/quality/publishing pipeline | Relevant corruption and clean-source integration tests, publication safety gate | Clinical/editorial expert review |
| android | Android Kotlin/Gradle, navigation, persistence, assets consumed by app | Affected unit tests, Gradle compile/debug APK, static/layout checks where possible; device claims only after device proof | Physical P30 test |
| safety-content | First aid/electrical/fire/hazardous instructions or public guidance | Automated validation PLUS qualified human review, provenance and licensing; NEVER automatic release | Safety correctness from CI |
| mixed | Multi-domain change | Union of affected profiles and risk-related checks | Skipping any affected integration |

Example: isolated synthetic benchmark requires focused deterministic tests (and scale run), NOT an unrelated seven-check article pipeline. Index checker modifications affecting real assets require full index integration tests even if isolated fixtures pass. QA may classify historical Cycle 004 correctly as python-index; the full runner is therefore relevant there.

## What counts as a verified handoff
Attach exact branch/head SHA; Actions run URL/ID; job conclusion; test command(s), counts and exit status; artifact when applicable; unverified scopes and limitations; draft PR link. `awaiting_ci` is NOT a blocking failure while the run is queued/running; Worker 4 may poll. Failure with actionable code defect -> Worker 2, consume one of three genuine retries. Unavailable infrastructure/permissions -> blocked_infrastructure and alert without consuming code retries. Prevent duplicate runs and alert spam.

## Feature and throughput priority
Only one code task per cycle, but select substantial vertical slices (visible feature + wiring + tests + docs) where achievable. Suggested rolling mix: **60% Android app features/UX**, **20% reliability/performance**, **15% offline content infrastructure**, **5% housekeeping**. These are planning targets, NOT a mandate to invent features or waive prerequisites.
Select by user-visible impact, roadmap approval, dependency safety and testability. Avoid repetitive validators unless a defect or regression proves necessary. Check draft PRs #22+ and existing features before creating a new task; if an old draft must merge for a new feature, require human approval instead of copying unmerged work or stacking it unsafely. All app changes remain founder-authorised local prototype, not launch permission.

## Shared CI
Reusable workflow: `.github/workflows/autodev-reusable-qa.yml` on `automation/five-stage-development-pipeline`. Branch-scoped callers can use:
```yaml
jobs:
  qa:
    uses: sebbbbrrrr/JustToBeSafe/.github/workflows/autodev-reusable-qa.yml@automation/five-stage-development-pipeline
    with:
      profile: python-index
    permissions:
      contents: read
```
Use profile only if it applies and its scripts actually exist on the caller branch. For temporary files or independent tests, run a small profile-specific supplemental job. Run workflow in public standard GitHub-hosted runners, read-only permissions, <= 15 minute job budget, no secrets and no auto-merge. Workflow files only become actionable when GitHub recognizes them for the specific caller; commit URL is not evidence of an Actions run.
