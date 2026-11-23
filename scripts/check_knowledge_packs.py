#!/usr/bin/env python3
"""Verify compressed offline knowledge packs and their source/manifest references."""
import argparse
import gzip
import json
from pathlib import Path
import sys
import zlib


def check(root: Path) -> str:
    source_path = root / "content/.generated/knowledge_articles.json"
    assets = root / "app/src/main/assets/knowledge"
    manifest_path = assets / "manifest.json"

    def read_json(path: Path):
        try:
            return json.loads(path.read_text(encoding="utf-8"))
        except (OSError, UnicodeError, json.JSONDecodeError) as exc:
            raise ValueError(f"{path}: cannot read valid JSON: {exc}") from exc

    source = read_json(source_path)
    manifest = read_json(manifest_path)
    if manifest.get("schema_version") != 1 or manifest.get("publication_state") != "internal_draft":
        raise ValueError(f"{manifest_path}: unsupported schema or publication state")
    articles = source.get("articles")
    entries = manifest.get("entries")
    if not isinstance(articles, list) or not isinstance(entries, list):
        raise ValueError(f"{manifest_path}: articles/entries must be lists")
    source_ids = [a.get("id") for a in articles]
    entry_ids = [e.get("id") for e in entries]
    if len(source_ids) != len(set(source_ids)) or len(entry_ids) != len(set(entry_ids)):
        raise ValueError(f"{manifest_path}: duplicate article IDs")
    missing = sorted(set(source_ids) - set(entry_ids))
    extra = sorted(set(entry_ids) - set(source_ids))
    if missing or extra:
        raise ValueError(f"{manifest_path}: source/manifest ID mismatch; missing={missing}, extra={extra}")

    packs = {}
    for entry in entries:
        name = entry.get("pack")
        if not isinstance(name, str) or not name.startswith("pack_") or not name.endswith(".json.gz") or Path(name).name != name:
            raise ValueError(f"{manifest_path}: invalid pack name {name!r}")
        packs.setdefault(name, []).append(entry)

    for name, references in sorted(packs.items()):
        path = assets / name
        try:
            with gzip.open(path, "rt", encoding="utf-8") as handle:
                contents = json.load(handle)
        except (OSError, EOFError, UnicodeError, json.JSONDecodeError, zlib.error) as exc:
            raise ValueError(f"{path}: missing, corrupt gzip, or malformed pack JSON: {exc}") from exc
        if not isinstance(contents, dict) or contents.get("schema_version") != 1 or not isinstance(contents.get("articles"), list):
            raise ValueError(f"{path}: invalid pack schema")
        records = {a.get("id"): a for a in contents["articles"]}
        for entry in references:
            ident = entry["id"]
            if ident not in records:
                raise ValueError(f"{path}: referenced article ID {ident!r} missing from pack")
            record = records[ident]
            if record.get("title") != entry.get("title"):
                raise ValueError(f"{path}: title mismatch for article {ident!r}")
            if record.get("review") != "pending" or not record.get("sections"):
                raise ValueError(f"{path}: article {ident!r} has invalid review or empty sections")
    return f"PASS: {len(source_ids)} searchable offline knowledge articles in {len(packs)} compressed packs"


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--root", type=Path, default=Path(__file__).resolve().parents[1],
                        help="repository or temporary fixture root")
    args = parser.parse_args()
    try:
        print(check(args.root))
    except (ValueError, TypeError, KeyError) as exc:
        print(f"FAIL: {exc}", file=sys.stderr)
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main())
