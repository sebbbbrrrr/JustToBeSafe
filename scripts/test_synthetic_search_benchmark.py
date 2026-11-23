#!/usr/bin/env python3
"""Determinism and safety tests for synthetic-only search benchmarking."""
import importlib.util
from pathlib import Path
import subprocess
import sys
import tempfile
import unittest
import json

SCRIPT = Path(__file__).with_name("benchmark_synthetic_search.py")
spec = importlib.util.spec_from_file_location("synthetic_search_bench", SCRIPT)
bench = importlib.util.module_from_spec(spec)
spec.loader.exec_module(bench)

class SyntheticSearchTests(unittest.TestCase):
    def test_deterministic_fixture_content(self):
        self.assertEqual(list(bench.documents(10, 2026)), list(bench.documents(10, 2026)))
        self.assertNotEqual(list(bench.documents(10, 2026)), list(bench.documents(10, 7)))

    def test_deterministic_shards_and_retrieval(self):
        x = bench.benchmark(30, 2026)
        y = bench.benchmark(30, 2026)
        self.assertEqual(x["shard_sha256"], y["shard_sha256"])
        self.assertEqual(x["compressed_bytes"], y["compressed_bytes"])
        self.assertEqual(x["unique_terms"], y["unique_terms"])
        self.assertEqual(x["documents"], 30)
        self.assertGreater(x["compressed_bytes"], 0)
        counts = {item["term"]: item["hits"] for item in x["queries"]}
        self.assertEqual(counts["synthetic"], 30)
        self.assertEqual(counts["unfindablefixture"], 0)

    def test_cli_json_and_no_tracked_asset_changes(self):
        with tempfile.TemporaryDirectory() as directory:
            out = Path(directory) / "report.json"
            p = subprocess.run([sys.executable, str(SCRIPT), "--sizes", "5", "--output", str(out)],
                               capture_output=True, text=True, timeout=30)
            self.assertEqual(p.returncode, 0, p.stderr)
            report = json.loads(out.read_text(encoding="utf-8"))
            self.assertEqual(report["kind"], "synthetic-only")
            self.assertEqual(report["results"][0]["documents"], 5)
            self.assertIn("NOT Android", report["environment"])
            self.assertEqual(len(list(Path(directory).rglob("*"))), 1)

    def test_rejects_oversized_fixtures(self):
        p = subprocess.run([sys.executable, str(SCRIPT), "--sizes", "10001"],
                           capture_output=True, text=True, timeout=15)
        self.assertNotEqual(p.returncode, 0)
        self.assertIn("1 to 10000", p.stderr)

if __name__ == "__main__":
    unittest.main()
