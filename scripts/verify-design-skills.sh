#!/usr/bin/env bash
# Static file-presence verification. Does not run upstream engines or network hooks.
set -euo pipefail
cd "$(dirname "$0")/.."
required=(
  "AGENTS.md"
  "PRODUCT.md"
  "DESIGN.md"
  ".agents/skills/justtobesafe-design-workflow/SKILL.md"
  ".agents/skills/impeccable/SKILL.md"
  ".agents/skills/impeccable/reference/critique.md"
  ".agents/skills/impeccable/reference/craft-floor.md"
  ".agents/skills/impeccable/scripts/impeccable"
  ".agents/skills/emil-design-eng/SKILL.md"
  ".agents/skills/animate/SKILL.md"
  ".agents/skills/animation-performance/SKILL.md"
  ".agents/skills/review-animations/SKILL.md"
  ".agents/skills/break-ui/SKILL.md"
  "third_party/DESIGN_SKILLS_PROVENANCE.md"
  "third_party/licenses/emilkowalski-skills-LICENSE.txt"
  "third_party/licenses/impeccable-LICENSE.txt"
)
failed=0
for file in "${required[@]}"; do
  if [[ ! -f "$file" ]]; then
    echo "MISSING: $file" >&2
    failed=1
  elif [[ "$file" == */SKILL.md ]]; then
    if [[ "$(head -n 1 "$file")" != "---" ]] || ! grep -q '^name: ' "$file"; then
      echo "BAD SKILL FRONTMATTER: $file" >&2
      failed=1
    fi
  fi
done
if [[ "$failed" != 0 ]]; then
  echo "Static verification failed." >&2
  exit 1
fi
echo "Static verification passed: expected skill entrypoints, playbooks and licences are present."
echo "Runtime checks, executable downloads, UI rendering and Codex skill discovery still need separate validation."
