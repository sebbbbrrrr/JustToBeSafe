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
    check(validFoodBarcode("0123456789012"))
    check(!validFoodBarcode("not-a-code"))
    check(pantryValidationError(PantryItem(name="Beans", quantity=3)) == null)
    check(pantryValidationError(PantryItem(name="Beans", quantity=0)) != null)
    check(pantryValidationError(PantryItem(name="Beans", expiryDate="31/12/2028")) != null)
    check(pantryPackageCount(listOf(PantryItem(name="Rice",quantity=2),
        PantryItem(name="Soup",quantity=4,emergencyReserve=true))) == 6)
    check(reservePackageCount(listOf(PantryItem(name="Soup",quantity=4,emergencyReserve=true))) == 4)
    check(expiryState("2026-11-20", java.time.LocalDate.of(2026,11,21)) == ExpiryState.EXPIRED)
    check(expiryState("2026-12-21", java.time.LocalDate.of(2026,11,21)) == ExpiryState.SOON)
    println("Checklist and pantry domain tests passed")
}
KOTLIN
kotlinc app/src/main/java/com/justtobesafe/app/domain/Checklist.kt app/src/main/java/com/justtobesafe/app/domain/PantryItem.kt "$work/Check.kt" -include-runtime -d "$work/tests.jar"
java -jar "$work/tests.jar"
