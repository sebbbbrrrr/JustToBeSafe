#!/usr/bin/env python3
"""Validate offline guide coverage and draft safety boundaries, without network."""
import json
from pathlib import Path

root = Path(__file__).resolve().parents[1] / "app/src/main/assets/guides"
catalogue = json.loads((root / "uk_urban_guide_catalogue.json").read_text(encoding="utf-8"))
library = json.loads((root / "uk_urban_guide_articles.json").read_text(encoding="utf-8"))
assert library["schema_version"] == 1
assert library["publication_state"] == "internal_draft"
assert catalogue["publication_state"] == "internal_draft"
topics = {t["id"]: t for t in catalogue["topics"]}
articles = library["articles"]
ids = [a["topic_id"] for a in articles]
assert len(set(ids)) == len(ids), "Duplicate offline article IDs"
assert set(ids) == set(topics), "Every catalogue topic must have one offline article"
for article in articles:
    ident = article["topic_id"]
    assert article["status"] == "internal_draft", ident
    assert article["safety_review"] == "pending", ident
    assert "reviewed" in article["display_notice"].lower(), ident
    sections = article["sections"]
    assert len(sections) >= 4, f"{ident}: incomplete offline article"
    assert len({section["id"] for section in sections}) == len(sections), ident
    for section in sections:
        assert section["title"].strip(), ident
        assert section["paragraphs"], ident
        assert all(isinstance(p, str) and len(p.strip()) >= 40
                   for p in section["paragraphs"]), ident
    assert topics[ident]["offline_body_available"] is False, (
        f"{ident}: a draft is not reviewed offline safety guidance"
    )
power = next(a for a in articles if a["topic_id"] == "power-cuts")
assert len(power["sections"]) == 7
print(f"Offline article validation passed: {len(articles)} complete draft articles, "
      f"{sum(len(a['sections']) for a in articles)} sections")
