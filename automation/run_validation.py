#!/usr/bin/env python3
"""Offline validation runner for autonomous development handoffs.

Usage, from a real checked-out repository:
 python3 automation/run_validation.py --output validation-report.json

No network, GitHub API, Gradle, Android SDK, secrets, or branch mutations.
Produces machine-readable evidence. Does NOT claim Android device coverage.
"""
from __future__ import annotations

import argparse
import datetime as dt
import json
import os
from pathlib import Path
import subprocess
import sys
import time

ROOT = Path(__file__).resolve().parents[1]
BASE_CHECKS = [
    ("shard-corruption-regressions", [sys.executable, "scripts/test_search_shard_integrity.py"]),
    ("batch-import", [sys.executable, "scripts/import_knowledge_batches.py"]),
    ("knowledge-quality", [sys.executable, "scripts/check_content_quality.py"]),
    ("knowledge-packs-build", [sys.executable, "scripts/build_knowledge_packs.py"]),
    ("knowledge-packs-check", [sys.executable, "scripts/check_knowledge_packs.py"]),
    ("index-build", [sys.executable, "scripts/build_fulltext_index.py"]),
    ("index-integrity-and-queries", [sys.executable, "scripts/check_fulltext_index.py"]),
]

def run(output: Path, timeout_seconds: int) -> int:
    results = []
    for name, command in BASE_CHECKS:
        path = ROOT / command[1]
        if not path.is_file():
            results.append({"name": name, "status": "blocked_infrastructure",
                            "error": f"Missing prerequisite {path.relative_to(ROOT)}",
                            "command": command, "elapsed_seconds": 0})
            break
        started = time.monotonic()
        try:
            p = subprocess.run(command, cwd=ROOT, timeout=timeout_seconds,
                               stdout=subprocess.PIPE, stderr=subprocess.PIPE,
                               text=True, errors="replace", check=False,
                               env={**os.environ, "PYTHONDONTWRITEBYTECODE": "1"})
            item = {"name": name, "status": "passed" if p.returncode == 0 else "failed",
                    "returncode": p.returncode, "command": command,
                    "elapsed_seconds": round(time.monotonic() - started, 2),
                    "stdout_tail": p.stdout[-6000:], "stderr_tail": p.stderr[-6000:]}
        except subprocess.TimeoutExpired:
            item = {"name": name, "status": "blocked_infrastructure",
                    "error": "Command exceeded per-step time budget",
                    "command": command, "elapsed_seconds": timeout_seconds}
        results.append(item)
        if item["status"] != "passed":
            break
    expected = len(BASE_CHECKS)
    status = ("passed_integration" if len(results) == expected
              and all(x["status"] == "passed" for x in results)
              else "blocked_infrastructure" if any(x["status"] == "blocked_infrastructure"
              for x in results) else "failed_code")
    report = {"schema_version": 1, "scope": "offline-content-and-index-Python-only",
              "status": status, "generated_utc": dt.datetime.now(dt.timezone.utc).isoformat(),
              "checks_passed": sum(x["status"] == "passed" for x in results),
              "checks_expected": expected, "results": results,
              "caveats": ["No Android SDK, device, APK or UI checks performed",
                          "Runner changes generated assets in local checkout; use disposable checkout"]}
    output.parent.mkdir(parents=True, exist_ok=True)
    output.write_text(json.dumps(report, indent=2) + "\n", encoding="utf-8")
    print(f"{status}: {report['checks_passed']}/{expected} checks; report={output}")
    return 0 if status == "passed_integration" else 2

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--output", type=Path, default=ROOT / "validation-report.json")
    parser.add_argument("--timeout", type=int, default=120)
    args = parser.parse_args()
    if args.timeout < 1 or args.timeout > 600:
        parser.error("--timeout must be 1..600 seconds")
    sys.exit(run(args.output, args.timeout))
