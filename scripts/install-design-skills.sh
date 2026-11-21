#!/usr/bin/env bash
# Project-local installer for Codex. Run manually after reviewing the upstream
# repositories and the security notes in docs/DESIGN_SKILLS_SETUP.md.
set -euo pipefail

if [[ ! -f "docs/PRODUCT_BRIEF.md" || ! -f "AGENTS.md" ]]; then
  echo "Run this command from the JustToBeSafe repository root on the design-skills branch." >&2
  exit 1
fi

if [[ ! -t 0 ]]; then
  echo "Interactive confirmation required. Do not run this installer unattended or in CI." >&2
  exit 1
fi

if ! command -v npx >/dev/null 2>&1; then
  echo "Node.js / npm with npx is required. Install a supported Node.js version first." >&2
  exit 1
fi

cat <<'NOTICE'
This will fetch third-party tooling and install project-scoped Codex skills:
  • emilkowalski/skills (the full skills collection)
  • pbakaus/impeccable (Impeccable)
Review upstream code, licences, install output and any suggested hook.
Do NOT enable a hook or bypass agent sandboxing without understanding its permissions.
This script does not run builds, deploy, or enable GitHub Actions.
NOTICE
read -r -p "Continue with interactive third-party installs? [y/N] " response
if [[ "$response" != "y" && "$response" != "Y" ]]; then
  echo "Cancelled."
  exit 0
fi

echo "Installing Emil Kowalski skills for Codex (project scope)..."
npx skills@latest add emilkowalski/skills --agent codex

echo "Installing Impeccable for Codex (project scope)..."
npx impeccable install --providers=codex --scope=project

echo "Installation commands completed; checking presence..."
bash scripts/verify-design-skills.sh
echo "Review 'git status' and all generated files/hooks before committing."
echo "Reload Codex, then inspect PRODUCT.md / DESIGN.md before invoking Impeccable init."
