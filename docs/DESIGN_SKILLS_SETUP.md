# JustToBeSafe — Codex design skills setup

## What is in this PR
- `AGENTS.md`: global agent safety and product rules.
- `PRODUCT.md`: validation-stage context used by Impeccable and other coding agents.
- `DESIGN.md`: provisional design principles, **not** a locked design system.
- `.agents/skills/justtobesafe-design-workflow/SKILL.md`: a usable repository-local skill to coordinate design tasks.
- `scripts/install-design-skills.sh`: a **manual** installer for the upstream collections.
- `scripts/verify-design-skills.sh`: a file-presence check.

**Important distinction:** The original Impeccable and Emil collections are NOT vendored or installed by this GitHub-only PR. Their installers need a local or Codex development checkout with Node.js/npm and trusted execution permissions. The workflow skill is usable immediately when Codex reads this branch. GitHub connector access alone does not execute npm installations.

## Install the full upstream skills (optional)

From a trusted checkout of this repository (after reviewing this PR and the upstream projects):

```bash
bash scripts/install-design-skills.sh
```

The script asks permission, then runs:
```bash
npx skills@latest add emilkowalski/skills --agent codex
npx impeccable install --providers=codex --scope=project
```

Their project-scoped installations are expected in `.agents/skills/`. Review the generated files before committing them; keep meaningful source/licence attribution. Installer behaviour and flags can change, so resolve any new prompts with care rather than bypassing them blindly. The verification script checks entrypoints but does not prove that binaries, references or hooks function correctly.

### Security
Impeccable offers optional executable design hooks and a browser live mode. Treat these as third-party code. **Decline hooks initially** and inspect their scripts and command permissions before enabling them. On Codex, project hooks require explicit trust via `/hooks`. Impeccable live mode has an Apply fallback that may launch a local coding CLI with sandbox/approval bypass; keep live mode disabled until separately reviewed. Avoid this installation in unattended jobs or CI.

## First use in Codex

1. Checkout the branch containing this setup and reload Codex.
2. Start with `$justtobesafe-design-workflow` on a narrow task (e.g. *shape the Phase 0 landing-page hero*).
3. After full upstream install, verify with `bash scripts/verify-design-skills.sh`, then open `/skills` or invoke `$impeccable` / `$emil-design-eng` explicitly.
4. Impeccable can use `$impeccable init` to read or complete product context and `$impeccable document` to document an existing design. **Do not overwrite the already created PRODUCT.md / provisional DESIGN.md without reviewing changes.** There is currently no finished UI to document.
5. For landing-page design, run `$impeccable shape the validation landing page`, then implement a first page after direction is selected, then `$impeccable critique the landing page`.
6. Use Emil's motion skills only when there is actual UI interaction to review; use `$break-ui` on the waitlist form before launch.

## Division of responsibility

| Main question | Preferred workflow |
|---|---|
| Product structure, visual direction, typography, layout, accessible responsiveness | Impeccable |
| Motion timing, easing, gesture craft, interaction details | Emil Kowalski skills |
| Long labels, edge cases, broken layout | Emil `break-ui` and Impeccable `harden` (different passes) |
| Frame drops in transitions | Emil `animation-performance`; verify on real hardware |
| Global project constraints, claims, consent and Phase 0 release gates | AGENTS.md + PRODUCT.md + project-local orchestration skill |

## Phase 0 acceptance checklist
- [x] Product and design context committed to a dedicated branch.
- [x] Repository-local Codex design workflow created.
- [x] Manual install and file-verification scripts added.
- [ ] Third-party package installation executed in a trusted development checkout.
- [ ] Generated upstream skills reviewed and committed if desired.
- [ ] Codex skill discovery and Impeccable executable command verified in the actual runtime.
- [ ] First landing-page design proposed, reviewed and approved.
- [ ] UI implementation, visual checks, privacy/legal review and waitlist release.

## Upstream references
- Emil: https://github.com/emilkowalski/skills
- Impeccable: https://github.com/pbakaus/impeccable
- Impeccable docs: https://impeccable.style/docs
