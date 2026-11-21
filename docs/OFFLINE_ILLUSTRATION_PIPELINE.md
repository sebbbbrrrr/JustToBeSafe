# JustToBeSafe — Offline Illustration Pipeline v1

## Purpose
Create **actual readable native diagrams** for articles where a visual helps users interpret a workflow or spatial arrangement. Illustrations are produced directly by Android Compose from a compact per-article data object. No network, image API, image-hosting service, external font, or raster file is required to display a generated diagram.

This is a schematic template engine, **not** AI-drawn custom realistic anatomy, electrical diagrams or comprehensive technical schematics. It supports the simple, safe illustration needs of large-scale reference content with tiny file-size overhead. Specialist diagrams may be authored separately as reusable original vectors when appropriate.

## Authoring schema
Every new worker article must actively assess if a visual materially helps understanding. When it does, put an optional `visual` object on the article:

```json
{
  "id": "equipment-check-calendar",
  "visual": {
    "kind": "sequence",
    "labels": ["Locate", "Inspect", "Record"],
    "alt": "Three circles connected left to right: locate equipment, inspect its condition, and record a follow-up.",
    "caption": "Equipment-review workflow. An inventory checkbox does not prove that the equipment works."
  }
}
```

All other article fields and review status remain required. Existing older articles are backward compatible without a visual. For guides where a diagram would be misleading or redundant, omit `visual`. For specialist medical, mains electrical, chemical or public-health procedures, defer any proposed treatment or repair diagram until qualified review.

## Native templates
- `sequence`: up to four labelled connected phases, shown visually left to right.
- `checklist`: a small illustrated checklist with readable, separate native text labels.
- `shelves`: labelled storage zones for safe equipment/supply organisation.
- `route`: conceptual waypoints and path connections; not a real map or routing guarantee.

Every template renders in the native Android article reader, responds to Material theme colours, and uses standard Compose text for labels so accessibility sizing and selection don't depend on bitmap pixels. The Canvas exposes an alt description for screen-reader users. Diagrams must stay at informational scope and cannot imply current incident conditions.

## Size and speed decisions
- Diagrams need no separate bitmap or illustration file per article.
- The illustration JSON is gzip-compressed with the article's existing category pack.
- No independent illustration build job is needed for each worker run.
- Labels/captions/alt are included in the offline full-text index.
- Only one visual is parsed when its packed category is opened; the existing category LRU is preserved.
- Workers read their pinned downloaded design skills once per run and reuse the schema/templates, instead of repeatedly generating equivalent artwork.
- Shared persistent worker branches and standing draft PRs avoid hundreds of PRs per week.
- For text-only hourly batches, run lightweight Python validations; Android APK build is reserved for UI/schema changes or periodic integration checks.

## Quality gates
`scripts/import_knowledge_batches.py` rejects unknown templates, invalid label counts, missing accessibility text and unreviewed visuals in high-risk categories. `scripts/check_content_quality.py` checks for duplicate/near-identical titles and reports how many native diagrams exist.

For every illustration, inspect semantic fit, original wording, label association, Android 320dp and large-font behaviour, colour contrast, alt completeness, and how the diagram might be misunderstood. Compliance with Impeccable and Emil design skills cannot replace qualified medical/electrical/first-aid review.

## Current demonstrated examples
Three existing guides in `content/batches/household-essentials-001.json` include real renderable `visual` data:
1. Important documents: illustrated checklist.
2. Household equipment checks: sequence.
3. Storage in a small flat: storage-zone schematic.

These diagrams are bundled in the development build after CI compilation. Existing two hazard-warning VectorDrawable illustrations are retained separately. No real-device visual QA has yet been completed, so don't claim a Huawei P30 screenshot or verified large-font behaviour until tested.

## Future phases
1. Add small original vector patterns for equipment identification, mechanical components and map symbols.
2. Maintain a globally deduplicated illustration registry with stable IDs and local text alternatives.
3. Add diagrams to appropriate reviewed first-aid and technical guides only after subject expert sign-off.
4. Build snapshot / TalkBack accessibility tests and differential pack size measurements at 100, 1000 and 10,000 articles.
5. Consider optimised WebP reference photographs only when a diagram cannot adequately explain the object. These remain stored offline and rights-cleared.
