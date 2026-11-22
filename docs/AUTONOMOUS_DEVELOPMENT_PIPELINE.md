# Five-stage autonomous development pipeline — pilot
Status: enabled for **JustToBeSafe only**, on isolated coordination branch `automation/five-stage-development-pipeline`. Other repositories require explicit approval. This document supplements, never overrides, AGENTS.md, PRODUCT.md, DESIGN.md, licensing/safety requirements, or any project roadmap. Existing editorial worker branches and pending PRs are left unchanged.

## State source
`automation/development-state.json` on this coordination branch is authoritative. Read its latest contents AND blob SHA immediately before writing; update with GitHub's compare-and-swap SHA via update_file. If SHA changed, reread and abort/reconcile; NEVER overwrite newer state. Every stage MUST create an auditable git commit or PR and a short report referencing evidence. Do not claim progress without actual tool results.

## Run contract
At each hourly run, inspect the state. Do nothing if `paused=true`, `blocked=true`, or `next_worker` differs from your worker number. An in-progress worker can continue its own unfinished work on subsequent runs, subject to locks and budgets. Only the rightful worker can set `next_worker` to the next stage. Use `cycle_id` and immutable acceptance criteria. Store UTC timestamps for a lease in state; claims must be exclusive with compare-and-swap, expire after two hours, and only the same worker may renew its own lease. A stale or suspicious lease should be marked blocked for inspection, not blindly stolen. Limit one active development cycle; keep history append-only, never erase errors.

## Stages
1. Architect: read actual repo, approved roadmaps, existing open PRs, previous failures, project guardrails. Pick ONE small scoped independently verifiable task aligned with roadmap and legal/safety phase; avoid dependent/unapproved work. Freeze requirements + measurable tests + excluded scope. Set next_worker=2.
2. Developer: implement in a NEW uniquely named `autodev/cycle-<id>` branch from the current approved integration base. Commit incremental changes; include tests, exact refs, report; don't merge. When implementation complete set next_worker=3.
3. Integrator: review diffs against frozen acceptance criteria. Improve quality, coherence, accessible UI, performance and missing coverage WITHIN frozen scope; no new product concepts, packages or network hooks. Rebase or reconcile if needed, commit report, set next_worker=4.
4. QA: check original goal point-by-point; independently inspect diffs, perform available tests/builds/static checks and report their exact outputs. Tests NOT EXECUTED are 'unverified', never pass. On failures set next_worker=2 and increment retry_count, attaching actionable defects; if retries>=3 or material safety issue set blocked=true (next_worker=none) rather than looping. On success, create/update DRAFT PR for human approval, mark `qa_passed=true`, `awaiting_merge=true`, next_worker=5. NEVER merge or deploy automatically.
5. Roadmap: verify QA evidence and PR; mark task `verified_in_pr` only (never 'released' before merge). Select next eligible dependency-safe task; if an earlier task requires human merge, avoid incompatible overlaps. Complete current ledger entry, queue one next objective for Worker 1, set next_worker=1. If no safe task, set blocked=true, explain blocker. Don't unilaterally change product direction.

## Constraints / protections
- Never modify `main` or existing worker branches or merge PRs without approval. Never change production infrastructure, payments, secrets, publishing, sensitive personal data, safety-critical public instructions, external network hooks or GitHub Actions workflows. Do not trigger GitHub Actions deliberately while usage is exhausted; local or low-cost static checks are preferable. Never claim a device-tested APK without device test evidence.
- Respect Phase 0 prototype scope and actual repo AGENTS.md. Safety guides always remain review=pending/internal_draft until separately expert-reviewed; design skills cannot establish technical or medical correctness.
- Before coding consult local vendored design skills when relevant and quote EXACT skill paths read; otherwise say not read. If source of truth absent, do not invent requirements.
- Every handoff includes task_id, branch, commit SHA(s), change list, tests run, pass/fail/unverified, outstanding defects, next stage, and any permissions/budget blocker.
- No fake approvals, no automatic production release, no unlimited self-improvement. Prefer stopping safely with actionable evidence.
- Control plane is GitHub file state, not memory or inter-automation chat. Scheduling is periodic polling, not guaranteed sequential triggering. Update conflicts or missing tool access require a blocked report.

## Pilot acceptance
First prove ONE entire five-stage cycle without a merge, accidental CI run or invented test result. If ChatGPT scheduled tasks lack GitHub write/test access in execution context, record blocker and stop rather than suggesting the pipeline is operational.
