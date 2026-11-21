---
name: justtobesafe-design-workflow
description: Use for JustToBeSafe landing-page, onboarding, app UI, design-system, accessibility, responsive, motion and UI-review tasks. Coordinates Impeccable's design workflow and Emil Kowalski's animation/interactions skills while preserving Phase 0 validation, safety, privacy and brand decisions.
---

# JustToBeSafe design workflow

## Load context first
Read the root `AGENTS.md`, `PRODUCT.md`, `DESIGN.md`, `docs/PRODUCT_BRIEF.md`, and `docs/PHASE_0_VALIDATION.md`. `DESIGN.md` is **provisional**: do not imply a final brand has been approved. If an upstream skill is not installed, do not claim to have used it; continue with general engineering best practices or report the missing dependency.

## Select the right specialist
- For page structure, visual directions, responsive design, UX and critique: invoke the installed `impeccable` skill. Prefer `shape` for planning, `critique` before changes, `polish` for finishing, `harden` for failures and `audit` for technical review.
- For motion decisions and micro-interactions: use Emil's `emil-design-eng`, `animate`, `choreograph`, or `review-animations`. Diagnose dropped animation frames using `animation-performance`.
- For cramped layouts, long translations, unusual names, absent optional fields and extreme values: use `break-ui` before shipping, when installed.
- Prefer a single lead workflow at a time. Do not blindly run overlapping `animate` or `audit` flows or make one tool contradict another.

## Phase 0 work sequence
1. Inspect relevant existing code, design decisions and test scripts; don't assume a frontend or build command exists.
2. State whether the target is a concept, an implementation or a review, and select a narrow page/section.
3. For landing pages, optimise clear value proposition, honest `not yet launched` status and easy-to-understand optional waitlist consent, **not pressure, alarmism or fabricated trust signals**.
4. Propose two or three distinct visual directions if none is established. Record decisions, approved tokens and brand assets in `DESIGN.md` only with founder confirmation.
5. Implement targeted changes when asked and when code exists. Do not publish, deploy, start tracking, collect emails or accept payments without separately approved privacy/security/release steps.
6. Test real device widths, keyboard/focus, reduced motion, contrast, non-ideal text and offline/error states as applicable. Report unavailable tests explicitly.
7. Summarise observed before/after changes, checks, open decisions and risks. Preserve product truth and Phase 0 status.

## Safety and licensing
- All emergency-procedure copy requires expert approval and rights verification before public release.
- Do not download, install or run third-party hooks or scripts without explicit human review of their permissions and contents.
- Do not commit visitor/customer data, API keys or private location information.
- Use `docs/DESIGN_SKILLS_SETUP.md` for the **optional upstream installation**, rather than assuming these collections are embedded in this repository.
