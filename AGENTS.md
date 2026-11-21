# JustToBeSafe — agent instructions

Applies to all coding-agent work in this repository. Read `docs/PRODUCT_BRIEF.md`, `docs/PHASE_0_VALIDATION.md`, and `PRODUCT.md` before changing product direction.

## Current stage
- Founder-authorised exception (21 November 2026): build a **local, native Android Calm Utility concept prototype** on a separate branch as an interactive validation tool. This does not approve a production MVP, live alerts, deployment, subscription/paywall or unreviewed safety content. All other Phase 0 safeguards remain in force.
- Phase 0 is market validation. Prioritise an accessible, mobile-first landing page, clearly optional consent-based waitlist, customer research, and pricing experiments. Do not build a full mobile app or invent a final tech stack.
- "JustToBeSafe" is a provisional working name, not a cleared brand. Pricing, pilot region, partners and subscription boundaries remain unapproved hypotheses.
- This is a public repository: never commit secrets, customer emails, location information, real household plans or other personal data.

## Safety, privacy and claims
- Essential preparedness guidance and official warnings must not be paywalled.
- Do not claim that safety content is expert-reviewed, alerts are live, offline coverage is complete, or the app is already available unless independently verified.
- Safety guidance requires appropriate specialist review, a traceable primary source, permitted reuse licence, publication and review dates, and geographic applicability before it is published.
- Product content must distinguish offline reference material from live information and must not impersonate official warnings or emergency services.
- Waitlist launch requires explicit, understandable consent, an appropriate reviewed privacy notice, withdrawal/unsubscribe and secure storage. No tracking or data collection before approved.

## Design workflow
- Follow `DESIGN.md` as **provisional design principles**, not as a final brand identity. Do not overwrite or invent a fixed palette, logos, typography, or marketing claims without approval.
- Prefer Impeccable for product-level UI structure, design critique, responsive behaviour and accessibility. Prefer Emil Kowalski's skills for motion details, animation performance, gesture quality and edge-case checks. Do not run two competing redesigns on the same surface.
- The repo-local skill `.agents/skills/justtobesafe-design-workflow/SKILL.md` orchestrates the pinned, vendored Impeccable and Emil skill collections in `.agents/skills/`. Their contents and licences are recorded in `third_party/DESIGN_SKILLS_PROVENANCE.md`. Executable hooks, downloads and browser live-mode tools remain disabled unless explicitly reviewed.
- Prioritise readable typography, clear state labels, accessible keyboard/focus flows, reduced-motion support, 320px-wide mobile layouts and credible trust signals. Avoid manipulative fear-based visuals.
- Audit and document findings before making broad UI changes; ship small, testable improvements.

## Engineering / reporting
- Inspect existing code and tests before changes. Avoid unapproved third-party packages, CI workflows, or network-enabled hooks.
- Run only available local checks. Do not claim a visual review, mobile-device test, security assessment or successful build that was not actually performed.
- Work on feature branches with draft pull requests. Do not run GitHub Actions unnecessarily; no deployment or live data collection without explicit approval.
