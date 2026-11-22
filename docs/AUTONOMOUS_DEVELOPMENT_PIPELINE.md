# Five-stage autonomous development pipeline — pilot
Status: enabled for **JustToBeSafe only**, on isolated coordination branch `automation/five-stage-development-pipeline`. Other repositories require explicit approval. This document supplements, never overrides, AGENTS.md, PRODUCT.md, DESIGN.md, licensing/safety requirements, or any project roadmap. Existing editorial worker branches and pending PRs are left unchanged.

## State source
`automation/development-state.json` on this coordination branch is authoritative. Read its latest contents AND blob SHA immediately before writing; update with GitHub's compare-and-swap SHA via update_file. If SHA changed, reread and abort/reconcile; NEVER overwrite newer state. Every stage MUST create an auditable git commit or PR and a short report referencing evidence. Do not claim progress without actual tool results.

## Run contract
At each hourly run, inspect the state. Do nothing if `paused=true`, `blocked=true`, or `next_worker` differs from your worker number. Only a confirmed defect in code/behaviour consumes a repair attempt; unavailable execution or tooling is a separate verification blocker. An in-progress worker can continue its own unfinished work on subsequent runs, subject to locks and budgets. Only the rightful worker can set `next_worker` to the next stage. Use `cycle_id` and immutable acceptance criteria. Store UTC timestamps for a lease in state; claims must be exclusive with compare-and-swap, expire after two hours, and only the same worker may renew its own lease. A stale or suspicious lease should be marked blocked for inspection, not blindly stolen. Limit one active development cycle; keep history append-only, never erase errors.

## Stages
1. Architect: read actual repo, approved roadmaps, existing open PRs, previous failures, project guardrails. Pick ONE small scoped independently verifiable task aligned with roadmap and legal/safety phase; avoid dependent/unapproved work. Freeze requirements + measurable tests + excluded scope. Set next_worker=2.
2. Developer: implement in a NEW uniquely named `autodev/cycle-<id>` branch from the current approved integration base. Commit incremental changes; include tests, exact refs, report; don't merge. When implementation complete set next_worker=3.
3. Integrator: review diffs against frozen acceptance criteria. Improve quality, coherence, accessible UI, performance and missing coverage WITHIN frozen scope; no new product concepts, packages or network hooks. Rebase or reconcile if needed, commit report, set next_worker=4.
4. QA: check original goal point-by-point; independently inspect diffs, perform available tests/builds/static checks and report their exact outputs. Tests NOT EXECUTED are 'unverified', never pass. On failures set next_worker=2 and increment retry_count, attaching actionable defects; if retries>=3 or material safety issue set blocked=true (next_worker=none) rather than looping. If required tests CANNOT RUN owing to missing checkout, SDK, tools, permissions or quota, record partial executed evidence, set status=awaiting_validation, blocked=true, next_worker=4 and verification_blocker with exact missing capability; DO NOT increment retry_count and DO NOT hand back as a code defect. Do not advance to Worker 5. When the missing capability becomes available, operator may clear blocked and QA rechecks. Fixture-only tests establish isolated logic, not full integration or Android compatibility. On success, create/update DRAFT PR for human approval, mark `qa_passed=true`, `awaiting_merge=true`, next_worker=5. NEVER merge or deploy automatically.
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

## Scheduling and execution caveats (found in pilot)
- A `run_now` response that says `inline_run` asks the current conversation to do the worker's work; it does **not** establish that an independent scheduled worker ran successfully. Test unattended execution separately at the next scheduled occurrence.
- The GitHub connector can fetch/edit sources, but does **not** itself supply a local repository checkout or Python/Android execution runtime. Run tests only where executable tooling is actually accessible; where necessary use a dedicated approved test runner later, with quota and approval guardrails.
- Separate verification statuses: `passed_isolated`, `passed_integration`, `unverified_integration`, `failed_code`, `blocked_infrastructure`. Never collapse `blocked_infrastructure` into `failed_code`.
- A manual isolated test from a faithfully copied source is evidence for the isolated function only, not proof that the full repository tests succeeded.
- Worker lease must be acquired using the current file SHA before edits. If the lease cannot be claimed, fail closed. Leases are cleared after an atomic handoff. Never fabricate a lease or use stale state.

## Operational validation runner and evidence (added 22 Nov 2026)
The coordination branch contains `automation/run_validation.py` and `automation/check_state.py`. The current cycle branch `autodev/cycle-001` ALSO includes `automation/run_validation.py`. For future cycles, Worker 2 must copy the runner to its **isolated** work branch when the task needs Python offline-content/index validation, or use an approved existing repo runner. Never assume files on the coordination branch exist on the work branch.

From a disposable checkout of the cycle branch, with Python 3 available, execute:
```sh
python3 automation/run_validation.py --output validation-report.json --timeout 120
```
This runs seven ordered Python checks: search shard regression, batch importer, quality, pack build/check, search index build/check. It writes machine-readable statuses `passed_integration`, `failed_code` or `blocked_infrastructure`, stdout/stderr tails and individual return codes. A PASS is scoped to this **Python offline pipeline** only, NOT Android build or device test. Do not publish the generated report if it could contain secrets; reports must be inspected first. Do not check in generated content unless reviewed.

On the coordination branch, run:
```sh
python3 automation/check_state.py
```
before and after each handoff whenever a Python execution environment is available. For no-execution GitHub connector runs, inspect invariants explicitly and record "not executed".

There is currently **no connected, available unattended project checkout/runner**. GitHub API read/write tools do not run shell commands. ChatGPT scheduled runs with `inline_run` are still manual execution in the current conversation, not proof of independently scheduled workers. GitHub Actions quota is exhausted until replenishment, and no Actions workflows have been created or dispatched for this pilot. Therefore the current `blocked_infrastructure` state must NOT be cleared just because the scripts exist. To resume: (1) connect and approve a genuine execution environment or restore available CI, (2) run the validation runner on the cycle branch, (3) record report plus exact commit SHA and environment, (4) clear blocked with compare-and-swap, and (5) rerun QA. Never auto-unblock based on a proposed command or invented log.

### Security and scheduling limits
- Never allow workflow runs from untrusted PRs to access write tokens or secrets; use read-only checkout and isolated disposable runners.
- Do not use `pull_request_target` to execute feature-branch code. Avoid network-enabled scripts and unapproved dependencies. Prefer manual approval of CI budget consumption.
- GitHub content write responses are not proof that source executes correctly; Python tests do not establish Android performance.
- The platform schedules each worker hourly at :00, :12, :24, :36, :48 (Europe/London); it does not trigger immediately on commits.
- Each task must acquire a compare-and-swap lease before mutation; verify `cycle_id`, `next_worker` and active lease; clear it on successful handoff. A stale lease is escalated rather than silently stolen.
