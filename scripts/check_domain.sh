#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
if ! command -v kotlinc >/dev/null; then
  echo "kotlinc unavailable; cannot run standalone domain smoke tests" >&2
  exit 1
fi
work="$(mktemp -d)"
trap 'rm -rf "$work"' EXIT
cat > "$work/Check.kt" <<'KOTLIN'
import com.justtobesafe.app.domain.*
fun main() {
    check(SuppliesCatalog.items.size == 8)
    check(SuppliesCatalog.validIds.size == SuppliesCatalog.items.size)
    check(completionPercent(0, 8) == 0)
    check(completionPercent(6, 8) == 75)
    check(completionPercent(80, 8) == 100)
    check(completionPercent(-4, 8) == 0)
    check(completedCount(setOf("water", "invalid")) == 1)
    println("Checklist domain tests passed")
}
KOTLIN
kotlinc app/src/main/java/com/justtobesafe/app/domain/Checklist.kt "$work/Check.kt" -include-runtime -d "$work/tests.jar"
java -jar "$work/tests.jar"
