# JustToBeSafe — Codex design skills

## Status: repository installation complete

This branch now contains the two **original**, version-pinned Codex design skill collections, plus a JustToBeSafe-specific orchestration skill. No manual copying, Codespaces setup or npm installation is necessary for Codex to **discover the skill instructions** after this branch is checked out.

- `.agents/skills/impeccable/` — Impeccable Codex skill, playbooks, supporting scripts and agent configurations.
- `.agents/skills/` — all 20 Emil Kowalski skill directories (29 files, including recipes and reviews).
- `.agents/skills/justtobesafe-design-workflow/` — our own orchestration skill and project rules.
- `PRODUCT.md`, `DESIGN.md`, `AGENTS.md` — JustToBeSafe product context, provisional design principles and persistent agent constraints.
- `third_party/DESIGN_SKILLS_PROVENANCE.md` — exact upstream Git revisions, licence locations and security caveats.

The initial setup PR had a manual installer script. **It is no longer needed.** It now prints a deprecation message rather than fetching or running external code.

## Recommended workflow

1. Review/merge the stacked PRs: PR #1 (Phase 0) first, then PR #2 (design skills). Codex can also work directly from this branch before merging.
2. Start a new Codex task on the `chore/codex-design-skills-foundation` branch and reload the session to discover repository-scoped skills.
3. Invoke `$justtobesafe-design-workflow` and ask to shape the **Phase 0 landing page** with truthful status and optional waitlist consent.
4. Use `$impeccable` for structure, visual direction, critique, layout and accessibility; use `$emil-design-eng`, `$animate`, `$review-animations` and `$break-ui` for animation/interaction and edge-case reviews.
5. Keep `DESIGN.md` provisional until a direction is reviewed. Do not claim features, alerts, expert approvals, or finished safety information exist when they do not.
6. Before deployment, test actual rendered mobile and desktop screens, accessibility and consent flows. Seek relevant safety/privacy reviews before launch.

Example design prompt:

> Use $justtobesafe-design-workflow and $impeccable to shape three distinctive, calm and trustworthy visual directions for JustToBeSafe's UK Phase 0 landing page. Preserve PRODUCT.md, DESIGN.md and Phase 0 scope. Show the differences in content hierarchy, visual identity and mobile usability. Make no false product or safety claims. Do not deploy or collect emails. Present the design proposal before implementing.

## Verification and executable features

```bash
bash scripts/verify-design-skills.sh
```

This checks expected files and metadata from a working checkout. We also compared original Git blob SHA hashes while transferring both collections. **Neither the full skill runtime nor the Impeccable executable engine has been run**, since this GitHub-only session does not provide a trusted Codex project execution environment. There is no finished website to visually test yet.

Impeccable's scripts may download or execute a binary at first invocation. Its optional design hook, live browser tool and Apply fallback can execute additional commands. **No hooks were installed or activated by this PR.** Inspect their permissions before enabling, and avoid sandbox/approval bypass for unattended tasks.

No app source, GitHub Actions workflow, tracking service, deployment, payment form or live waitlist has been introduced.

## Update policy

Both source collections are vendored at pinned versions, so they do not auto-update. Future updates should be a separate reviewed PR, comparing upstream changes and licences. Do not run the old `npx` installation commands over the existing vendor directories without checking for conflicts.

## Main references

- [Emil Kowalski Skills](https://github.com/emilkowalski/skills)
- [Impeccable](https://github.com/pbakaus/impeccable)
- [Source/version provenance](../third_party/DESIGN_SKILLS_PROVENANCE.md)
