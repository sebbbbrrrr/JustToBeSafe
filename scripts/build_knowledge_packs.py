#!/usr/bin/env python3
"""Deterministically compile editorial JSON into compressed, category-sharded APK assets.

Only requires Python's standard library. Author sources live in content/; generated
bundles live under app/src/main/assets/knowledge/ and are not committed.
"""
from __future__ import annotations

import gzip
import json
from pathlib import Path
import re
import shutil

ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / "content/.generated/knowledge_articles.json"
OUT = ROOT / "app/src/main/assets/knowledge"


def compact(obj):
    return json.dumps(obj, ensure_ascii=False, separators=(",", ":"), sort_keys=True).encode("utf-8")


def validate(source):
    if source.get("schema_version") != 1 or source.get("publication_state") != "internal_draft":
        raise ValueError("This builder accepts only internal draft catalogue v1")
    records = source.get("articles")
    if not isinstance(records, list) or not records:
        raise ValueError("No knowledge articles")
    ids = set()
    for article in records:
        ident = article.get("id", "")
        if not re.fullmatch(r"[a-z0-9]+(?:-[a-z0-9]+)*", ident) or ident in ids:
            raise ValueError("Invalid or duplicate article ID: " + ident)
        ids.add(ident)
        if article.get("review") != "pending":
            raise ValueError("Unreviewed content cannot be marked as published")
        for key in ("title", "category", "summary"):
            if not isinstance(article.get(key), str) or not article[key].strip():
                raise ValueError(ident + ": missing " + key)
        if not isinstance(article.get("tags"), list):
            raise ValueError(ident + ": tags missing")
        if not isinstance(article.get("sections"), list) or len(article["sections"]) < 2:
            raise ValueError(ident + ": missing substantive sections")
        for section in article["sections"]:
            if not section.get("title") or not section.get("paragraphs"):
                raise ValueError(ident + ": empty section")
            if not all(isinstance(p, str) and len(p.strip()) > 25 for p in section["paragraphs"]):
                raise ValueError(ident + ": empty or short paragraph")
    return records


def build():
    if not SOURCE.exists():
        raise FileNotFoundError("Run import_knowledge_batches.py first")
    source = json.loads(SOURCE.read_text(encoding="utf-8"))
    records = validate(source)
    if OUT.exists():
        shutil.rmtree(OUT)
    OUT.mkdir(parents=True)
    by_category = {}
    for article in records:
        by_category.setdefault(article["category"], []).append(article)
    entries = []
    original_bytes = len(compact(source))
    packed_bytes = 0
    for ix, (category, articles) in enumerate(sorted(by_category.items()), 1):
        # Stable names avoid accidental cross-APK collisions.
        pack_name = f"pack_{ix:02}.json.gz"
        payload = compact({"schema_version": 1, "articles": articles})
        destination = OUT / pack_name
        destination.write_bytes(gzip.compress(payload, compresslevel=9, mtime=0))
        packed_bytes += destination.stat().st_size
        for article in articles:
            entries.append({
                "id": article["id"], "title": article["title"],
                "category": category, "summary": article["summary"],
                "tags": article["tags"], "risk": article["risk"],
                "review": article["review"], "illustration": article["illustration"],
                "pack": pack_name,
            })
    entries.sort(key=lambda a: a["title"].casefold())
    manifest = {
        "schema_version": 1,
        "edition": source["edition"],
        "publication_state": "internal_draft",
        "entries": entries,
    }
    (OUT / "manifest.json").write_bytes(compact(manifest))
    packed_bytes += (OUT / "manifest.json").stat().st_size
    # Do not promise compression savings against an APK baseline: APK assets
    # are normally compressed too. Category sharding primarily saves RAM.
    print(f"Knowledge pack: {len(records)} articles in {len(by_category)} shards; "
          f"{original_bytes} original compact bytes, {packed_bytes} bundled bytes "
          f"(including manifest); ratio {packed_bytes/original_bytes:.2f}")
    if set(a["id"] for a in entries) != set(a["id"] for a in records):
        raise ValueError("Manifest article ID mismatch")


if __name__ == "__main__":
    build()
