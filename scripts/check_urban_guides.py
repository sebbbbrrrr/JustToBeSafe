#!/usr/bin/env python3
"""Validate the versioned, non-public UK urban guide seed without external dependencies."""
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / "app/src/main/assets/guides/uk_urban_guide_catalogue.json"


def validate_catalogue(data):
    issues = []
    if data.get("schema_version") != 1:
        issues.append("schema_version must equal 1")
    if data.get("publication_state") != "internal_draft":
        issues.append("collection must remain internal_draft until separately reviewed")
    topics = data.get("topics")
    if not isinstance(topics, list) or not topics:
        return ["topics must be a non-empty list"]
    ids = set()
    priorities = set()
    mandatory = (
        "id", "priority", "title", "category", "geographic_scope", "state",
        "guide_version", "offline_body_available", "safety_review",
        "reuse_rights", "source_urls", "reviewed_at", "next_review_due",
        "reviewer", "content_sections"
    )
    for index, topic in enumerate(topics):
        prefix = f"topics[{index}]"
        missing = [key for key in mandatory if key not in topic]
        if missing:
            issues.append(f"{prefix}: missing {missing}")
            continue
        topic_id = topic["id"]
        if not isinstance(topic_id, str) or not topic_id:
            issues.append(f"{prefix}: invalid id")
        if topic_id in ids:
            issues.append(f"{prefix}: duplicate id {topic_id}")
        ids.add(topic_id)
        if topic["priority"] in priorities or not isinstance(topic["priority"], int):
            issues.append(f"{prefix}: duplicate or invalid priority")
        priorities.add(topic["priority"])
        if topic["state"] != "internal_draft" or topic["safety_review"] != "pending":
            issues.append(f"{prefix}: unreviewed topic incorrectly marked publishable")
        if topic["offline_body_available"] is not False:
            issues.append(f"{prefix}: draft cannot claim a reviewed offline body")
        if topic["reuse_rights"] != "not_assessed":
            issues.append(f"{prefix}: rights status must not be presumed")
        if any(topic[key] is not None for key in ("reviewed_at", "next_review_due", "reviewer")):
            issues.append(f"{prefix}: fabricated review metadata")
        if not isinstance(topic["source_urls"], list) or not topic["source_urls"] or any(
            not isinstance(url, str) or not url.startswith("https://") for url in topic["source_urls"]
        ):
            issues.append(f"{prefix}: official reference URL required")
        for section in topic["content_sections"]:
            for key in ("id", "title", "kind", "draft_prompt", "status"):
                if key not in section:
                    issues.append(f"{prefix}: section missing {key}")
            if section.get("status") not in ("draft", "blocked_expert_review"):
                issues.append(f"{prefix}: unreviewed section has invalid status")
    if priorities != set(range(1, len(topics) + 1)):
        issues.append("priorities must be 1..N without gaps")
    first = topics[0]
    if first.get("id") != "power-cuts":
        issues.append("the first editorial module must be urban power cuts")
    if len(first.get("content_sections", [])) != 7:
        issues.append("power cut draft must include seven editorial sections")
    return issues


def main():
    data = json.loads(SOURCE.read_text(encoding="utf-8"))
    issues = validate_catalogue(data)
    if issues:
        for issue in issues:
            print("FAIL:", issue)
        raise SystemExit(1)
    print(f"PASS: {len(data['topics'])} draft urban topics, 7 power-cut section stubs, no unreviewed content marked publishable")


if __name__ == "__main__":
    main()
