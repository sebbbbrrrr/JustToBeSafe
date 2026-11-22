#!/usr/bin/env python3
"""Small isolated corruption tests for the index verifier; no generated assets modified."""
import ast
import gzip
import hashlib
import json
from pathlib import Path
import tempfile
import unittest

SOURCE = Path(__file__).with_name("check_fulltext_index.py")

class ShardIntegrityTests(unittest.TestCase):
    def setUp(self):
        self.tmp = tempfile.TemporaryDirectory()
        self.addCleanup(self.tmp.cleanup)
        root = Path(self.tmp.name)
        self.index = root / "search"
        self.index.mkdir()
        self.document_id = "fixture-article"
        self.term = "prepared"
        self.nibble = hashlib.sha256(self.term.encode()).hexdigest()[0]
        for digit in "0123456789abcdef":
            terms = {self.term: [self.document_id]} if digit == self.nibble else {}
            self.write(digit, {"version": 1, "terms": terms})

    def write(self, digit, data):
        (self.index / f"terms_{digit}.json.gz").write_bytes(
            gzip.compress(json.dumps(data).encode("utf-8"))
        )

    def validate(self):
        # Extract the production function without importing top-level checks,
        # which intentionally require a complete generated application index.
        tree = ast.parse(SOURCE.read_text(encoding="utf-8"))
        function = next(node for node in tree.body
                        if isinstance(node, ast.FunctionDef) and node.name == "validate_shards")
        module = ast.fix_missing_locations(ast.Module(body=[function], type_ignores=[]))
        namespace = {"INDEX": self.index, "gzip": gzip, "json": json,
                     "hashlib": hashlib, "expected_ids": {self.document_id}, "shards": {}}
        exec(compile(module, str(SOURCE), "exec"), namespace)
        namespace["validate_shards"]()

    def test_valid_fixture(self):
        self.validate()

    def test_missing_shard(self):
        (self.index / "terms_f.json.gz").unlink()
        with self.assertRaisesRegex(AssertionError, "missing search shard"):
            self.validate()

    def test_corrupt_gzip(self):
        (self.index / "terms_0.json.gz").write_bytes(b"not gzip")
        with self.assertRaisesRegex(AssertionError, "Invalid or missing search shard"):
            self.validate()

    def test_invalid_json(self):
        (self.index / "terms_1.json.gz").write_bytes(gzip.compress(b"{broken"))
        with self.assertRaisesRegex(AssertionError, "Invalid or missing search shard"):
            self.validate()

    def test_malformed_postings(self):
        self.write(self.nibble, {"version": 1, "terms": {self.term: "not-list"}})
        with self.assertRaisesRegex(AssertionError, "Invalid postings"):
            self.validate()

    def test_unknown_document(self):
        self.write(self.nibble, {"version": 1, "terms": {self.term: ["unknown"]}})
        with self.assertRaisesRegex(AssertionError, "Unknown document"):
            self.validate()

    def test_misplaced_term(self):
        wrong = "0" if self.nibble != "0" else "1"
        self.write(wrong, {"version": 1, "terms": {self.term: [self.document_id]}})
        with self.assertRaisesRegex(AssertionError, "Misplaced term"):
            self.validate()

if __name__ == "__main__":
    unittest.main()
