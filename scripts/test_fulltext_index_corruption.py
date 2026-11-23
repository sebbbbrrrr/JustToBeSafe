#!/usr/bin/env python3
"""Deterministic, self-contained offline index corruption regression fixtures."""
import gzip
import hashlib
import json
from pathlib import Path
import subprocess
import sys
import tempfile
import unittest

CHECKER = Path(__file__).with_name("check_fulltext_index.py")
DIGITS = "0123456789abcdef"


class IndexCorruptionTests(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.addCleanup(self.temp.cleanup)
        self.root = Path(self.temp.name)
        self.index = self.root / "app/src/main/assets/search"
        self.index.mkdir(parents=True)
        self.merged = self.root / "content/.generated/knowledge_articles.json"
        self.merged.parent.mkdir(parents=True)
        self.urban = self.root / "app/src/main/assets/guides/uk_urban_guide_articles.json"
        self.urban.parent.mkdir(parents=True)
        self.docs = ["fixture-home", "fixture-urban"]
        self.write_json(self.merged, {"articles": [{"id": "fixture-home"}]})
        self.write_json(self.urban, {"articles": [{"topic_id": "fixture-urban"}]})
        self.write_json(self.index / "manifest.json", {
            "schema_version": 1, "tokenizer": "unicode-prefix-v2",
            "shards": 16, "publication_state": "internal_draft",
            "documents": self.docs,
        })
        self.shards = {n: {"version": 1, "terms": {}} for n in DIGITS}
        self.shards[self.nibble("water")]["terms"]["water"] = self.docs[:]
        self.save_shards()

    @staticmethod
    def nibble(token):
        return hashlib.sha256(token.encode()).hexdigest()[0]

    @staticmethod
    def write_json(path, value):
        path.write_text(json.dumps(value, sort_keys=True), encoding="utf-8")

    def save_shards(self):
        for n, value in self.shards.items():
            (self.index / f"terms_{n}.json.gz").write_bytes(
                gzip.compress(json.dumps(value, sort_keys=True).encode(), mtime=0))

    def run_checker(self, expected=0, diagnostic=None):
        result = subprocess.run(
            [sys.executable, str(CHECKER), "--fixture-root", str(self.root)],
            capture_output=True, text=True, timeout=15)
        self.assertEqual(result.returncode, expected, result.stdout + result.stderr)
        if diagnostic:
            self.assertIn(diagnostic, result.stderr)
        return result

    def test_valid_sixteen_shards_and_stable_ids(self):
        self.run_checker()
        self.assertEqual(self.docs, json.loads((self.index / "manifest.json").read_text())["documents"])

    def test_missing_shard(self):
        (self.index / "terms_0.json.gz").unlink()
        self.run_checker(1, "terms_0.json.gz")

    def test_truncated_gzip(self):
        path = self.index / "terms_0.json.gz"
        path.write_bytes(path.read_bytes()[:8])
        self.run_checker(1, "terms_0.json.gz")

    def test_invalid_gzip(self):
        (self.index / "terms_0.json.gz").write_bytes(b"not gzip")
        self.run_checker(1, "terms_0.json.gz")

    def test_malformed_shard_json(self):
        (self.index / "terms_0.json.gz").write_bytes(gzip.compress(b"{", mtime=0))
        self.run_checker(1, "terms_0.json.gz")

    def test_malformed_manifest(self):
        (self.index / "manifest.json").write_text("{", encoding="utf-8")
        self.run_checker(1, "manifest.json")

    def test_mismatched_manifest_ids(self):
        manifest = json.loads((self.index / "manifest.json").read_text())
        manifest["documents"] = ["fixture-home", "unknown"]
        self.write_json(self.index / "manifest.json", manifest)
        self.run_checker(1, "document ID mismatch")

    def test_unknown_posting_id(self):
        self.shards[self.nibble("water")]["terms"]["water"] = ["fixture-home", "unknown"]
        self.save_shards()
        self.run_checker(1, "unknown posting article IDs")

    def test_wrong_nibble(self):
        wrong = next(n for n in DIGITS if n != self.nibble("water"))
        self.shards[wrong]["terms"]["water"] = ["fixture-home"]
        self.save_shards()
        self.run_checker(1, f"terms_{wrong}.json.gz")

    def test_malformed_postings(self):
        self.shards[self.nibble("water")]["terms"]["water"] = {"fixture-home": True}
        self.save_shards()
        self.run_checker(1, "malformed postings")

    def test_duplicate_postings(self):
        self.shards[self.nibble("water")]["terms"]["water"] = ["fixture-home", "fixture-home"]
        self.save_shards()
        self.run_checker(1, "sorted and unique")

    def test_malformed_source_id_type(self):
        self.write_json(self.merged, {"articles": [{"id": ["not", "hashable"]}]})
        self.run_checker(1, "article IDs must be nonempty strings")

    def test_empty_source_id(self):
        self.write_json(self.urban, {"articles": [{"topic_id": ""}]})
        self.run_checker(1, "article IDs must be nonempty strings")

    def test_duplicate_manifest_ids(self):
        manifest = json.loads((self.index / "manifest.json").read_text())
        manifest["documents"].append("fixture-home")
        self.write_json(self.index / "manifest.json", manifest)
        self.run_checker(1, "unique and sorted")


if __name__ == "__main__":
    unittest.main()
