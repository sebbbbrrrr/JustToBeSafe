#!/usr/bin/env python3
"""Validate offline full-text index integrity and existing common-language queries."""
from __future__ import annotations

import argparse
import gzip
import hashlib
import json
from pathlib import Path
import re
import sys

ROOT = Path(__file__).resolve().parents[1]
STOPWORDS = {"how", "to", "a", "an", "the", "do", "i", "can", "what",
             "is", "for", "in", "of", "and", "my", "with", "on", "at"}
NIBBLES = "0123456789abcdef"


class IndexErrorDetail(ValueError):
    """Index validation error with a source-specific diagnostic."""


def read_json(path: Path, *, compressed: bool = False):
    try:
        if compressed:
            with gzip.open(path, "rt", encoding="utf-8") as stream:
                return json.load(stream)
        return json.loads(path.read_text(encoding="utf-8"))
    except (OSError, UnicodeError, ValueError, EOFError) as exc:
        raise IndexErrorDetail(f"{path}: cannot read valid {'gzip JSON' if compressed else 'JSON'}: {exc}") from exc


def validate(index: Path, merged: Path, urban_path: Path, *, fixture: bool = False):
    meta = read_json(index / "manifest.json")
    if not isinstance(meta, dict):
        raise IndexErrorDetail(f"{index / 'manifest.json'}: manifest must be an object")
    for key, expected in (("schema_version", 1), ("tokenizer", "unicode-prefix-v2"),
                          ("shards", 16)):
        if type(meta.get(key)) is not type(expected) or meta.get(key) != expected:
            raise IndexErrorDetail(f"manifest.json: {key} must be {expected!r}")
    docs = meta.get("documents")
    if not isinstance(docs, list) or not all(isinstance(x, str) and x for x in docs):
        raise IndexErrorDetail("manifest.json: documents must be nonempty string IDs")
    if len(docs) != len(set(docs)) or docs != sorted(docs):
        raise IndexErrorDetail("manifest.json: documents must be unique and sorted")
    data = read_json(merged)
    urban = read_json(urban_path)
    try:
        source_ids = [a["id"] for a in data["articles"]] + [
            a["topic_id"] for a in urban["articles"]]
    except (TypeError, KeyError) as exc:
        raise IndexErrorDetail(f"source fixtures {merged} / {urban_path}: malformed articles: {exc}") from exc
    if len(source_ids) != len(set(source_ids)):
        raise IndexErrorDetail("source articles: duplicate article IDs")
    expected_ids = set(source_ids)
    if set(docs) != expected_ids:
        missing = sorted(expected_ids - set(docs))
        extra = sorted(set(docs) - expected_ids)
        raise IndexErrorDetail(f"manifest.json: document ID mismatch; missing={missing[:5]}, unknown={extra[:5]}")
    if not fixture and len(docs) < 23:
        raise IndexErrorDetail("manifest.json: unexpected loss of baseline offline guides (fewer than 23)")
    shards = {}
    for nibble in NIBBLES:
        path = index / f"terms_{nibble}.json.gz"
        payload = read_json(path, compressed=True)
        if not isinstance(payload, dict) or type(payload.get("version")) is not int or payload["version"] != 1 or not isinstance(payload.get("terms"), dict):
            raise IndexErrorDetail(f"{path}: expected version=1 and terms object")
        terms = payload["terms"]
        for token, postings in terms.items():
            if not isinstance(token, str) or not token or hashlib.sha256(token.encode("utf-8")).hexdigest()[0] != nibble:
                raise IndexErrorDetail(f"{path}: invalid nibble placement or token {token!r}")
            if not isinstance(postings, list) or not postings or not all(isinstance(x, str) and x for x in postings):
                raise IndexErrorDetail(f"{path}: malformed postings for {token!r}")
            if postings != sorted(set(postings)):
                raise IndexErrorDetail(f"{path}: postings for {token!r} must be sorted and unique")
            unknown = set(postings) - expected_ids
            if unknown:
                raise IndexErrorDetail(f"{path}: unknown posting article IDs for {token!r}: {sorted(unknown)}")
        shards[nibble] = terms
    return docs, shards


def query(text: str, docs: list[str], shards: dict):
    words = [w for w in re.findall(r"[^\W_]+", text.lower()) if w not in STOPWORDS]
    if not words:
        return set(docs)
    found = None
    for word in words:
        nibble = hashlib.sha256(word.encode("utf-8")).hexdigest()[0]
        postings = set(shards[nibble].get(word, []))
        found = postings if found is None else found.intersection(postings)
    return found or set()


def main(argv=None):
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--fixture-root", type=Path, help="isolated fixture root; skips shipped corpus query assertions")
    args = parser.parse_args(argv)
    root = args.fixture_root if args.fixture_root is not None else ROOT
    index = root / "app/src/main/assets/search"
    merged = root / "content/.generated/knowledge_articles.json"
    urban = root / "app/src/main/assets/guides/uk_urban_guide_articles.json"
    try:
        docs, shards = validate(index, merged, urban, fixture=args.fixture_root is not None)
        if args.fixture_root is None:
            expectations = (
                ("how to treat a burn", "first-aid-burns"),
                ("how to fix a cable", "home-damaged-cable"),
                ("insurance documents", "prepare-important-documents"),
                ("refrigeration", "power-cuts"),
                ("equipment inspection", "equipment-check-calendar"),
            )
            for phrase, article in expectations:
                if article not in query(phrase, docs, shards):
                    raise IndexErrorDetail(f"query {phrase!r}: missing expected article {article}")
            optional = (("test-w1-first-aid-01", "first aid kit stock"),
                        ("test-w4-urban-01", "lift failure"),
                        ("test-w5-outdoors-01", "walking route card"))
            for article, phrase in optional:
                if article in docs and article not in query(phrase, docs, shards):
                    raise IndexErrorDetail(f"query {phrase!r}: missing expected article {article}")
            if query("unfindablezxy9876", docs, shards):
                raise IndexErrorDetail("unknown query unexpectedly returned results")
        print(f"PASS: {len(docs)} offline indexed articles, 16 validated shards" +
              ("" if args.fixture_root is not None else ", body and common-language queries"))
        return 0
    except IndexErrorDetail as exc:
        print(f"FAIL: {exc}", file=sys.stderr)
        return 1


if __name__ == "__main__":
    sys.exit(main())
