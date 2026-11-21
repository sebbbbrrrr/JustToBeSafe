#!/usr/bin/env python3
"""Validate and combine baseline plus independently authored editorial batches.

Input: content/offline_knowledge_articles.json and content/batches/*.json.
Output: content/.generated/knowledge_articles.json, reproducibly assembled.
No network, APIs, AI-generated mass replication or safety-status auto-promotion.
"""
from __future__ import annotations
import json
from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[1]
BASE = ROOT / "content/offline_knowledge_articles.json"
BATCHES = ROOT / "content/batches"
OUT = ROOT / "content/.generated/knowledge_articles.json"
MAX_ARTICLES_PER_BATCH = 500

def validate_article(article: dict, origin: str):
    ident = article.get("id", "")
    if not isinstance(ident, str) or not re.fullmatch(r"[a-z0-9]+(?:-[a-z0-9]+)*", ident):
        raise ValueError(f"{origin}: invalid article ID {ident!r}")
    for key in ("title", "category", "summary", "risk"):
        if not isinstance(article.get(key), str) or not article[key].strip():
            raise ValueError(f"{origin}/{ident}: missing {key}")
    if article.get("review") != "pending":
        raise ValueError(f"{origin}/{ident}: content cannot be promoted without independent review")
    if not isinstance(article.get("tags"), list) or any(
        not isinstance(s, str) or not s.strip() for s in article["tags"]
    ):
        raise ValueError(f"{origin}/{ident}: invalid tags")
    sections = article.get("sections")
    if not isinstance(sections, list) or len(sections) < 2:
        raise ValueError(f"{origin}/{ident}: expected 2+ substantive sections")
    keys = set()
    for section in sections:
        sid = section.get("id")
        if not isinstance(sid, str) or not sid or sid in keys:
            raise ValueError(f"{origin}/{ident}: duplicate/invalid section ID")
        keys.add(sid)
        if not section.get("title") or not isinstance(section.get("paragraphs"), list):
            raise ValueError(f"{origin}/{ident}: invalid section")
        if not section["paragraphs"] or any(
            not isinstance(p, str) or len(p.strip()) <= 25 for p in section["paragraphs"]
        ):
            raise ValueError(f"{origin}/{ident}: empty/short paragraph")
    visual = article.get("visual")
    if visual is not None:
        if not isinstance(visual, dict):
            raise ValueError(f"{origin}/{ident}: visual must be a diagram object")
        if article["risk"] in {"medical", "electrical", "chemical", "public-health"}:
            raise ValueError(f"{origin}/{ident}: hazardous instructional diagrams require separate review")
        if visual.get("kind") not in {"sequence", "checklist", "shelves", "route"}:
            raise ValueError(f"{origin}/{ident}: unsupported visual template")
        labels = visual.get("labels")
        if not isinstance(labels, list) or len(labels) not in (2, 3, 4) or any(
            not isinstance(label, str) or not 1 <= len(label.strip()) <= 36 for label in labels
        ):
            raise ValueError(f"{origin}/{ident}: diagram labels must be 2–4 short accessible strings")
        for field, minimum in (("alt", 30), ("caption", 15)):
            value = visual.get(field)
            if not isinstance(value, str) or not minimum <= len(value.strip()) <= 240:
                raise ValueError(f"{origin}/{ident}: missing accessible diagram {field}")
    return ident

def read_collection(path: Path):
    data = json.loads(path.read_text(encoding="utf-8"))
    if data.get("schema_version") != 1 or data.get("publication_state") != "internal_draft":
        raise ValueError(f"{path}: unsupported schema/status")
    articles = data.get("articles")
    if not isinstance(articles, list) or not articles or len(articles) > MAX_ARTICLES_PER_BATCH:
        raise ValueError(f"{path}: invalid number of articles")
    return articles

def compile_batches():
    seen = set()
    combined = []
    batch_files = [BASE] + (sorted(BATCHES.glob("*.json")) if BATCHES.is_dir() else [])
    for path in batch_files:
        for article in read_collection(path):
            ident = validate_article(article, path.name)
            if ident in seen:
                raise ValueError(f"duplicate article across batches: {ident}")
            seen.add(ident)
            combined.append(article)
    result = {
        "schema_version": 1, "edition": "offline-knowledge-bulk-v1",
        "publication_state": "internal_draft",
        "articles": sorted(combined, key=lambda x: x["id"]),
    }
    OUT.parent.mkdir(parents=True, exist_ok=True)
    OUT.write_text(json.dumps(result, ensure_ascii=False, sort_keys=True,
                              separators=(",", ":")) + "\n", encoding="utf-8")
    print(f"Validated {len(combined)} unique draft articles from {len(batch_files)} editorial batches")
    return result

if __name__ == "__main__":
    compile_batches()
