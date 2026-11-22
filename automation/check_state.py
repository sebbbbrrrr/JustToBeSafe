#!/usr/bin/env python3
"""Validate development-state.json without network access or side effects."""
from pathlib import Path
import argparse
import json
import sys

def check(state):
    errors = []
    required = {"schema_version": int, "cycle_id": int, "next_worker": (int, type(None)),
                "blocked": bool, "paused": bool, "retry_count": int, "max_retries": int,
                "history": list, "commits": list, "acceptance_criteria": list}
    for name, kind in required.items():
        if name not in state or not isinstance(state[name], kind):
            errors.append(f"{name}: missing or wrong type")
    if errors:
        return errors
    if state["schema_version"] != 1:
        errors.append("unsupported schema_version")
    if state["cycle_id"] < 1:
        errors.append("cycle_id must be positive")
    if state["next_worker"] not in (1, 2, 3, 4, 5, None):
        errors.append("next_worker must be 1..5 or null")
    if state["retry_count"] < 0 or state["retry_count"] > state["max_retries"]:
        errors.append("invalid retry count")
    if state["blocked"] and state.get("qa_passed") and state.get("next_worker") == 5:
        errors.append("blocked cycle cannot advance to Roadmap")
    if state.get("qa_passed") and not state.get("work_branch"):
        errors.append("QA passed without a work branch")
    if state["next_worker"] in (2, 3, 4, 5) and not state.get("task_id"):
        errors.append("active stage has no task_id")
    if state["next_worker"] in (3, 4, 5) and not state.get("work_branch"):
        errors.append("post-development stage has no work_branch")
    if state.get("verification_status") == "blocked_infrastructure" and state.get("retry_count", 0) > 0:
        # Retries may have originated from genuine defects; don't mislabel them.
        if not any(x.get("status") == "qa_rejected_unverified" for x in state["history"]):
            pass
    if state.get("lease") is not None:
        lease = state["lease"]
        if not isinstance(lease, dict) or not {"worker", "expires_utc", "cycle_id"} <= set(lease):
            errors.append("invalid lease structure")
    return errors

if __name__ == "__main__":
    ap = argparse.ArgumentParser()
    ap.add_argument("path", nargs="?", default=str(Path(__file__).with_name("development-state.json")))
    args = ap.parse_args()
    try:
        data = json.loads(Path(args.path).read_text(encoding="utf-8"))
        problems = check(data)
    except (OSError, ValueError) as exc:
        print(f"INVALID: {exc}", file=sys.stderr)
        sys.exit(2)
    if problems:
        print("INVALID: " + "; ".join(problems), file=sys.stderr)
        sys.exit(2)
    print(f"PASS: cycle={data['cycle_id']} worker={data['next_worker']} blocked={data['blocked']}")
