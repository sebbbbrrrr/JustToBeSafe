#!/usr/bin/env python3
"""Fast offline content QA for every editorial batch: duplicates, diagrams, reporting.

No network, extra dependencies or APK compilation. Operates on the deterministic
merged source after import_knowledge_batches.py.
"""
import json
import re
import unicodedata
from collections import defaultdict
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
MERGED = ROOT / "content/.generated/knowledge_articles.json"
URBAN_META = ROOT / "app/src/main/assets/guides/uk_urban_guide_catalogue.json"
OUT = ROOT / "content/.generated/quality_report.json"

def tokens(text):
    cleaned = unicodedata.normalize("NFKC", text).casefold()
    return set(re.findall(r"[^\W_]+", cleaned, re.UNICODE)) - {
        "a", "an", "the", "and", "of", "to", "for", "in", "how", "with", "on"
    }

def main():
    data = json.loads(MERGED.read_text(encoding="utf-8"))
    urban = json.loads(URBAN_META.read_text(encoding="utf-8"))
    records = [(a["id"], a["title"], a["category"]) for a in data["articles"]]
    records += [(a["id"], a["title"], "Urban preparedness") for a in urban["topics"]]
    title_index = {}
    term_index = defaultdict(set)
    near_duplicates = set()
    for ident, title, category in records:
        words = frozenset(tokens(title))
        key = " ".join(sorted(words))
        if not words:
            raise ValueError(f"Empty searchable title: {ident}")
        if key in title_index:
            raise ValueError(f"Duplicate titles: {ident} / {title_index[key]}")
        title_index[key] = ident
        candidates = set().union(*(term_index[word] for word in words))
        for earlier_id, earlier_words in candidates:
            if len(words) >= 3 and len(earlier_words) >= 3:
                similarity = len(words & earlier_words) / len(words | earlier_words)
                if similarity >= .86:
                    near_duplicates.add(tuple(sorted((ident, earlier_id))))
        for word in words:
            term_index[word].add((ident, words))
    if near_duplicates:
        raise ValueError(f"Near-identical guide titles require deduplication: {sorted(near_duplicates)[:10]}")
    visual_count = sum(a.get("visual") is not None for a in data["articles"])
    visual_kinds = sorted({a["visual"]["kind"] for a in data["articles"] if a.get("visual")})
    report = {
        "practical_articles": len(data["articles"]),
        "urban_articles": len(urban["topics"]),
        "total": len(records),
        "native_diagrams": visual_count,
        "diagram_kinds": visual_kinds,
        "title_duplicates": 0,
        "near_duplicate_titles": 0,
        "publication_state": "internal_draft",
    }
    OUT.parent.mkdir(parents=True, exist_ok=True)
    OUT.write_text(json.dumps(report, indent=2, sort_keys=True) + "\n", encoding="utf-8")
    print("PASS: content and illustration QA: " + json.dumps(report, sort_keys=True))

if __name__ == "__main__":
    main()
