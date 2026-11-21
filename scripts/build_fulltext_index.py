#!/usr/bin/env python3
"""Compile a small, reproducible offline full-text inverted index across all guide bodies.

Search index lives inside the APK. Term postings are partitioned across 16 gzip
shards by SHA-256 first nibble so a query reads only its required shard(s).
No runtime internet, database migration, external packages or API keys.
"""
from __future__ import annotations

from collections import defaultdict
import gzip
import hashlib
import json
from pathlib import Path
import re
import shutil
import sys

ROOT = Path(__file__).resolve().parents[1]
PRACTICAL = ROOT / "content/offline_knowledge_articles.json"
URBAN = ROOT / "app/src/main/assets/guides/uk_urban_guide_articles.json"
URBAN_META = ROOT / "app/src/main/assets/guides/uk_urban_guide_catalogue.json"
OUT = ROOT / "app/src/main/assets/search"

def compact(value):
    return json.dumps(value, sort_keys=True, ensure_ascii=False, separators=(",", ":")).encode("utf-8")

def terms(text):
    # Align with Kotlin's Unicode letter-or-digit tokenizer. Do not use stemming
    # without a versioned migration, as changed normalisation breaks old packs.
    return set(re.findall(r"[^\W_]+", text.lower(), flags=re.UNICODE))

def documents():
    practical = json.loads(PRACTICAL.read_text(encoding="utf-8"))
    urban = json.loads(URBAN.read_text(encoding="utf-8"))
    meta = json.loads(URBAN_META.read_text(encoding="utf-8"))
    assert practical["publication_state"] == urban["publication_state"] == "internal_draft"
    urban_info = {v["id"]: v for v in meta["topics"]}
    out = []
    for item in practical["articles"]:
        text = " ".join([item["title"], item["category"], item["summary"], *item["tags"]] +
                        [section["title"] + " " + " ".join(section["paragraphs"])
                         for section in item["sections"]])
        out.append((item["id"], text))
    for article in urban["articles"]:
        item = urban_info[article["topic_id"]]
        text = " ".join([item["title"], item["category"], *item["tags"]] +
                        [section["title"] + " " + " ".join(section["paragraphs"])
                         for section in article["sections"]])
        out.append((item["id"], text))
    ids = [ident for ident, _ in out]
    assert len(ids) == len(set(ids)), "Colliding practical/urban topic IDs"
    return out

def build():
    docs = documents()
    shards = defaultdict(dict)
    for doc_id, text in docs:
        for word in terms(text):
            number = hashlib.sha256(word.encode("utf-8")).hexdigest()[0]
            postings = shards[number].setdefault(word, [])
            postings.append(doc_id)
    if OUT.exists():
        shutil.rmtree(OUT)
    OUT.mkdir(parents=True)
    compressed_bytes = 0
    for digit in "0123456789abcdef":
        path = OUT / f"terms_{digit}.json.gz"
        payload = compact({"version": 1, "terms": {
            word: sorted(postings) for word, postings in sorted(shards[digit].items())
        }})
        path.write_bytes(gzip.compress(payload, compresslevel=9, mtime=0))
        compressed_bytes += path.stat().st_size
    manifest = {"schema_version": 1, "documents": sorted(doc_id for doc_id, _ in docs),
                "shards": 16, "tokenizer": "unicode-alphanumeric-lowercase-v1",
                "publication_state": "internal_draft"}
    (OUT / "manifest.json").write_bytes(compact(manifest))
    compressed_bytes += (OUT / "manifest.json").stat().st_size
    print(f"Offline full-text index: {len(docs)} documents, "
          f"{sum(len(d) for d in shards.values())} unique terms, "
          f"16 shards, {compressed_bytes} bytes compressed incl. manifest")
    return compressed_bytes

if __name__ == "__main__":
    build()
