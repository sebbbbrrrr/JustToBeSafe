# DESIGN — JustToBeSafe (provisional)

**Status:** Design principles, NOT a final approved brand system. No production app or established UI exists yet. The first designed surface should be the Phase 0 validation landing page. Record visual directions and obtain founder approval before locking visual tokens.

## Product design objective
Help ordinary households feel **calm, informed and capable** rather than alarmed. Build trust through accuracy, legibility, honest status and restrained polish. The design should feel like a reliable everyday household tool, not a doomsday campaign or an emergency-services authority impersonation.

## Surface modes
- **Phase 0 landing page — Persuade:** explain the proposed value clearly, state the product is not launched, offer transparent optional waitlist participation, and avoid misleading urgency or fake testimonials.
- **Future app tools — Operate:** quick scanning, practical checklists, accessible steps, sensible offline/online indicators and clear recovery states.
- **Future preparedness articles — Read:** short hierarchy, sources and last-reviewed metadata, geographic scope, print/offline-friendly structure.

## Visual guardrails (not committed tokens)
- Strong type hierarchy and readable paragraph measure; never rely on colour alone to convey urgency or progress.
- Prefer restrained, practical imagery, clear iconography and generous touch targets. Avoid unsupported claims of official status.
- Decorative motion is optional. Motion should communicate transitions or feedback, respect reduced-motion settings and never delay urgent information.
- Distinguish provisional mockups, unverified placeholder content, reviewed guidance and real-time warnings visually and textually.
- Test the first landing-page design at narrow phone widths, portrait and desktop, with long labels, no images, and keyboard navigation.
- Forms must make consent explicit, with visible errors, success confirmation and an easy opt-out path.
- Avoid card-in-card clutter, excessive glass effects, fear-inducing red warning treatments, stock emergency imagery and inaccessible low-contrast text.

## To define after design exploration
Typography and font licensing; brand colours and contrast tokens; grid/breakpoints; spacing and radii; illustration language; motion tokens; icon system; content voice; component inventory; approved reference screens.

## Workflow
1. Consult `PRODUCT.md` and the Phase 0 validation plan.
2. Use Impeccable to shape and critique page structure and candidate visual directions.
3. Use Emil's `emil-design-eng`, `animate`, `review-animations`, `break-ui` and performance-oriented skills for targeted details after direction is approved.
4. Record evidence and visual decisions here; don't rebrand established choices during a polish pass.
5. Verify actual renders if a runnable frontend exists; otherwise report design-only or code-only checks accurately.

## Selected Android prototype direction — Calm Utility (21 November 2026)

The founder selected **Calm Utility** from three Android app concepts. Implement as a native Material 3 **Operate** UI: ocean teal `#087C72`, mist backgrounds, slate secondary text, clean cards and high-contrast typography. The countryside vignette is a lightweight decorative illustration, not a hazard forecast. Use 170ms/100ms fade-through for destination continuity and restrained progress animation (~260ms), with native ripple feedback and Android system animation-duration settings respected. Follow `.agents/skills/impeccable/reference/android.md` and Emil's motion review rules; avoid ornamental loops, claims of readiness or false live status. Dark mode should use semantic colour roles. This selects the **prototype design only**, not a trademark-cleared final brand identity.
