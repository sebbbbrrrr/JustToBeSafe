#!/usr/bin/env python3
"""Synthetic-only offline search scale benchmark. Never touches APK/content assets.

Measure Python/Linux reference index construction, compressed on-disk size, and
cold (uncached) query cost for deterministic fictional articles. NOT Android timings.
"""
from __future__ import annotations
import argparse
import gzip
import hashlib
import json
from pathlib import Path
import random
import re
import tempfile
import time

NIBBLES = "0123456789abcdef"
STOPWORDS = {"how", "to", "a", "an", "the", "do", "i", "can", "what",
             "is", "for", "in", "of", "and", "my", "with", "on", "at"}
VOCAB = ("pantry storage document communication weather flashlight inspection "
         "equipment calendar bicycle household battery jacket checklist radio "
         "window blanket family shelf notebook backup maintenance water rain").split()

def compact(value):
    return json.dumps(value, sort_keys=True, ensure_ascii=False,
                      separators=(",", ":")).encode("utf-8")

def tokens(text):
    words = set(re.findall(r"[^\W_]+", text.lower(), flags=re.UNICODE)) - STOPWORDS
    output = set(words)
    for word in words:
        for length in range(4, min(len(word), 9)):
            output.add(word[:length])
    return output

def documents(count, seed):
    rng = random.Random(seed)
    for i in range(count):
        words = rng.choices(VOCAB, k=45)
        words.extend((f"record{i:06d}", "synthetic", "fixture"))
        yield f"synthetic-{i:06d}", " ".join(words)

def index_documents(docs, output):
    shards = {n: {} for n in NIBBLES}
    for doc_id, body in docs:
        for term in tokens(body):
            n = hashlib.sha256(term.encode("utf-8")).hexdigest()[0]
            shards[n].setdefault(term, []).append(doc_id)
    for n in NIBBLES:
        terms = {term: sorted(values) for term, values in sorted(shards[n].items())}
        (output / f"terms_{n}.json.gz").write_bytes(
            gzip.compress(compact({"version": 1, "terms": terms}), compresslevel=9, mtime=0)
        )
    return sum(len(x) for x in shards.values())

def cold_query(output, term):
    nibble = hashlib.sha256(term.encode("utf-8")).hexdigest()[0]
    start = time.perf_counter()
    with gzip.open(output / f"terms_{nibble}.json.gz", "rt", encoding="utf-8") as fh:
        postings = json.load(fh)["terms"].get(term, [])
    return len(postings), (time.perf_counter() - start) * 1000.0

def benchmark(count, seed):
    with tempfile.TemporaryDirectory(prefix="jts-synthetic-search-") as directory:
        output = Path(directory)
        start = time.perf_counter()
        term_count = index_documents(documents(count, seed), output)
        build_seconds = time.perf_counter() - start
        sizes = [(output / f"terms_{n}.json.gz").stat().st_size for n in NIBBLES]
        samples = []
        for term in ("pantry", "radio", "synthetic", "unfindablefixture"):
            hits, ms = cold_query(output, term)
            samples.append({"term": term, "hits": hits, "cold_query_ms": round(ms, 3)})
        hashes = {n: hashlib.sha256((output / f"terms_{n}.json.gz").read_bytes()).hexdigest()
                  for n in NIBBLES}
        return {"documents": count, "unique_terms": term_count, "compressed_bytes": sum(sizes),
                "build_seconds": round(build_seconds, 3), "queries": samples,
                "shard_sha256": hashes}

def main():
    p = argparse.ArgumentParser(description=__doc__)
    p.add_argument("--sizes", nargs="+", type=int, default=[100, 1000, 10000])
    p.add_argument("--seed", type=int, default=2026)
    p.add_argument("--output", type=Path, default=None)
    args = p.parse_args()
    if not args.sizes or any(n < 1 or n > 10000 for n in args.sizes):
        p.error("sizes must be integers from 1 to 10000")
    report = {"schema_version": 1, "kind": "synthetic-only",
              "environment": "Python host, NOT Android or Huawei P30",
              "seed": args.seed, "results": [benchmark(n, args.seed) for n in args.sizes]}
    payload = json.dumps(report, indent=2) + "\n"
    if args.output is not None:
        args.output.parent.mkdir(parents=True, exist_ok=True)
        args.output.write_text(payload, encoding="utf-8")
    else:
        print(payload, end="")

if __name__ == "__main__":
    main()
