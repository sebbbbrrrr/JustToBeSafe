# Third-party Codex design skills provenance

These upstream files were copied verbatim from pinned public GitHub versions through the GitHub connector, with Git blob hashes verified on transfer. They have not been executed here.

- Emil Kowalski's skills: https://github.com/emilkowalski/skills at commit `64eb3deb9a5fbfa041faf783e0bc3fd635872e9c` (MIT). Source `skills/*` installed in `.agents/skills/*`. License: `third_party/licenses/emilkowalski-skills-LICENSE.txt`.
- Impeccable: https://github.com/pbakaus/impeccable at commit `b49177d32788f2dc585c42831d845dacf1ebed2c` (Apache-2.0). Source `.agents/skills/impeccable/*` installed without modification. License: `third_party/licenses/impeccable-LICENSE.txt`.

**Security and limitations:** Third-party code, scripts and references are stored but no npm package, CLI, design hook or downloaded binary has been run or enabled. Impeccable's launcher may fetch an executable on first use. Review script contents, permissions and expected network behaviour first. Do not enable its `live` Apply fallback or sandbox-bypass modes for unattended work. Keep hooks disabled unless explicitly reviewed and approved. GitHub file-presence checks do not replace actual Codex/runtime tests.

Updates should compare upstream changes and licenses; do not silently auto-update either pinned collection.
