#!/usr/bin/env python3
"""Verify bulk import + full-text index coverage with real body and natural queries."""
import gzip
import hashlib
import importlib.util
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
INDEX = ROOT / "app/src/main/assets/search"
MERGED = ROOT / "content/.generated/knowledge_articles.json"
META = json.loads((INDEX / "manifest.json").read_text(encoding="utf-8"))
DATA = json.loads(MERGED.read_text(encoding="utf-8"))

assert META["schema_version"] == 1
assert META["tokenizer"] == "unicode-prefix-v2"
assert META["shards"] == 16
urban = json.loads((ROOT / "app/src/main/assets/guides/uk_urban_guide_articles.json").read_text())
expected_ids = {a["id"] for a in DATA["articles"]} | {a["topic_id"] for a in urban["articles"]}
assert set(META["documents"]) == expected_ids, "Index coverage must include every imported and urban article"
assert len(META["documents"]) >= 23, "Unexpected loss of baseline offline guides"
assert len(set(META["documents"])) == len(META["documents"])

STOPWORDS = {"how", "to", "a", "an", "the", "do", "i", "can", "what",
             "is", "for", "in", "of", "and", "my", "with", "on", "at"}
shards = {}
def query(text):
    import re
    words = [w for w in re.findall(r"[^\W_]+", text.lower()) if w not in STOPWORDS]
    if not words:
        return set(META["documents"])
    found = None
    for word in words:
        nibble = hashlib.sha256(word.encode("utf-8")).hexdigest()[0]
        if nibble not in shards:
            with gzip.open(INDEX / f"terms_{nibble}.json.gz", "rt", encoding="utf-8") as f:
                shards[nibble] = json.load(f)["terms"]
        postings = set(shards[nibble].get(word, []))
        found = postings if found is None else found.intersection(postings)
    return found or set()

assert "first-aid-burns" in query("how to treat a burn")
assert "home-damaged-cable" in query("how to fix a cable")
assert "prepare-important-documents" in query("insurance documents")
assert "power-cuts" in query("refrigeration")
assert "equipment-check-calendar" in query("equipment inspection")
if "test-w1-first-aid-01" in expected_ids:
    assert "test-w1-first-aid-01" in query("first aid kit stock")
if "test-w4-urban-01" in expected_ids:
    assert "test-w4-urban-01" in query("lift failure")
if "test-w5-outdoors-01" in expected_ids:
    assert "test-w5-outdoors-01" in query("walking route card")
assert query("unfindablezxy9876") == set()
for nibble in "0123456789abcdef":
    assert (INDEX / f"terms_{nibble}.json.gz").stat().st_size > 30
print(f"PASS: {len(META['documents'])} offline indexed articles, body and common-language queries")
