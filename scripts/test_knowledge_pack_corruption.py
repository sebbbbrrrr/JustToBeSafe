#!/usr/bin/env python3
"""Isolated, deterministic corruption regression tests for the pack checker."""
import gzip
import json
from pathlib import Path
import subprocess
import sys
import tempfile
import unittest

CHECKER = Path(__file__).with_name("check_knowledge_packs.py")


class PackCheckerFixtures(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.addCleanup(self.temp.cleanup)
        self.root = Path(self.temp.name)
        self.assets = self.root / "app/src/main/assets/knowledge"
        self.assets.mkdir(parents=True)
        self.source = self.root / "content/.generated/knowledge_articles.json"
        self.source.parent.mkdir(parents=True)
        self.article = {"id": "fixture-one", "title": "Synthetic fixture",
                        "review": "pending", "sections": [{"title": "Sample", "paragraphs": ["Only a fixture."]}]}
        self.entry = {"id": "fixture-one", "title": "Synthetic fixture", "pack": "pack_01.json.gz"}
        self.write_source([self.article])
        self.write_manifest([self.entry])
        self.write_pack([self.article])

    def write_source(self, articles):
        self.source.write_text(json.dumps({"articles": articles}), encoding="utf-8")

    def write_manifest(self, entries):
        (self.assets / "manifest.json").write_text(json.dumps({
            "schema_version": 1, "publication_state": "internal_draft", "entries": entries
        }), encoding="utf-8")

    def write_pack(self, articles):
        (self.assets / "pack_01.json.gz").write_bytes(gzip.compress(json.dumps({
            "schema_version": 1, "articles": articles
        }).encode("utf-8"), mtime=0))

    def run_checker(self):
        return subprocess.run([sys.executable, str(CHECKER), "--root", str(self.root)],
                              capture_output=True, text=True, timeout=10)

    def assert_failure(self, *terms):
        result = self.run_checker()
        self.assertNotEqual(result.returncode, 0, result.stdout)
        for term in terms:
            self.assertIn(term, result.stderr)

    def test_valid_minimal_pack(self):
        result = self.run_checker()
        self.assertEqual(result.returncode, 0, result.stderr)
        self.assertIn("PASS: 1", result.stdout)

    def test_missing_pack(self):
        (self.assets / "pack_01.json.gz").unlink()
        self.assert_failure("pack_01.json.gz", "missing")

    def test_truncated_gzip(self):
        path = self.assets / "pack_01.json.gz"
        path.write_bytes(path.read_bytes()[:12])
        self.assert_failure("pack_01.json.gz", "corrupt")

    def test_invalid_gzip(self):
        (self.assets / "pack_01.json.gz").write_bytes(b"not gzip")
        self.assert_failure("pack_01.json.gz", "corrupt")

    def test_malformed_json_in_gzip(self):
        (self.assets / "pack_01.json.gz").write_bytes(gzip.compress(b"{bad json", mtime=0))
        self.assert_failure("pack_01.json.gz", "malformed")

    def test_missing_article_in_pack(self):
        self.write_pack([dict(self.article, id="another-id")])
        self.assert_failure("pack_01.json.gz", "fixture-one", "missing")

    def test_malformed_article_record_in_pack(self):
        self.write_pack([None])
        self.assert_failure("pack_01.json.gz", "invalid article record")

    def test_malformed_manifest_entry(self):
        self.write_manifest([None])
        self.assert_failure("manifest.json", "invalid manifest entry")

    def test_manifest_source_id_mismatch(self):
        self.write_manifest([dict(self.entry, id="manifest-only")])
        self.assert_failure("manifest.json", "source/manifest ID mismatch", "manifest-only")


if __name__ == "__main__":
    unittest.main()
