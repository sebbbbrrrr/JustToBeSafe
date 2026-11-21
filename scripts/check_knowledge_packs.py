#!/usr/bin/env python3
"""Verify sharded offline APK library can be reconstructed without network."""
import gzip
import json
from pathlib import Path

root = Path(__file__).resolve().parents[1]
source = json.loads((root / "content/.generated/knowledge_articles.json").read_text())
assets = root / "app/src/main/assets/knowledge"
manifest = json.loads((assets / "manifest.json").read_text())
assert manifest["schema_version"] == 1
assert manifest["publication_state"] == "internal_draft"
assert len(source["articles"]) == len(manifest["entries"])
ids = {article["id"] for article in source["articles"]}
assert ids == {article["id"] for article in manifest["entries"]}
assert len(ids) == len(source["articles"])
for file_name in {item["pack"] for item in manifest["entries"]}:
    assert file_name.startswith("pack_") and file_name.endswith(".json.gz")
    with gzip.open(assets / file_name, "rb") as handle:
        contents = json.load(handle)
    assert contents["schema_version"] == 1
    records = {item["id"]: item for item in contents["articles"]}
    for entry in (x for x in manifest["entries"] if x["pack"] == file_name):
        assert entry["id"] in records
        record = records[entry["id"]]
        assert record["title"] == entry["title"]
        assert record["review"] == "pending"
        assert record["sections"]
print(f"PASS: {len(ids)} searchable offline knowledge articles in "
      f"{len(set(x['pack'] for x in manifest['entries']))} compressed packs")
