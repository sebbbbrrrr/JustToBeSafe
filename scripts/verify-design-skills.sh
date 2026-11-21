#!/usr/bin/env bash
# File-level checks only; not a runtime test or upstream security audit.
set -euo pipefail

if [[ ! -f "AGENTS.md" || ! -f "PRODUCT.md" || ! -f "DESIGN.md" ]]; then
  echo "Missing repository design context." >&2
  exit 1
fi

for file in \
  .agents/skills/justtobesafe-design-workflow/SKILL.md \
  .agents/skills/impeccable/SKILL.md \
  .agents/skills/emil-design-eng/SKILL.md; do
  if [[ -f "$file" ]]; then
    echo "Present: $file"
  else
    echo "Missing: $file"
    missing=1
  fi
done

if [[ "${missing:-0}" -ne 0 ]]; then
  echo "The third-party installations are not yet complete. See docs/DESIGN_SKILLS_SETUP.md." >&2
  exit 1
fi

echo "Found core skill entrypoints. Check all other installed skills and dependencies manually."
