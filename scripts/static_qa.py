"""No network/dependencies: metadata and safety smoke tests, not a compilation substitute."""
from pathlib import Path
import re
import xml.etree.ElementTree as ET

root = Path(__file__).resolve().parents[1]
manifest = root / "app/src/main/AndroidManifest.xml"
xmls = list((root / "app/src/main/res").rglob("*.xml")) + [manifest]
for path in xmls:
    ET.parse(path)

manifest_text = manifest.read_text()
for permission in ("ACCESS_FINE_LOCATION", "ACCESS_COARSE_LOCATION",
                   "READ_CONTACTS", "CALL_PHONE", "POST_NOTIFICATIONS"):
    assert f'android.permission.{permission}' not in manifest_text, permission
# Pantry adds an explicitly scoped camera permission and Open Food Facts product lookup.
assert 'android.permission.CAMERA' in manifest_text
assert 'android.permission.INTERNET' in manifest_text
assert 'android:allowBackup="false"' in manifest_text
assert 'android:dataExtractionRules=' in manifest_text

kotlin_root = root / "app/src/main/java"
files = list(kotlin_root.rglob("*.kt"))
assert len(files) >= 7
combined = "\n".join(path.read_text() for path in files)
for phrase in ("HomeScreen(", "GuidesScreen(", "SuppliesScreen(", "PlanScreen(",
               "SecurePlanStore", "AES/GCM/NoPadding", "BackHandler(",
               "NavigationBar(", "NavigationRail("):
    assert phrase in combined, phrase
assert "Live hazards not connected" in combined
assert "NOT YET REVIEWED" in combined
for path in files:
    text = path.read_text()
    assert "package com.justtobesafe.app" in text, str(path)
    assert "TODO(" not in text, str(path)

# Calm Utility visual-quality regression invariants.
activity = (kotlin_root / "com/justtobesafe/app/MainActivity.kt").read_text()
home = (kotlin_root / "com/justtobesafe/app/ui/screens/HomeScreen.kt").read_text()
guides = (kotlin_root / "com/justtobesafe/app/ui/screens/GuidesScreen.kt").read_text()
supplies = (kotlin_root / "com/justtobesafe/app/ui/screens/SuppliesScreen.kt").read_text()
plan = (kotlin_root / "com/justtobesafe/app/ui/screens/PlanScreen.kt").read_text()
components = (kotlin_root / "com/justtobesafe/app/ui/components/Components.kt").read_text()
previews = (kotlin_root / "com/justtobesafe/app/ui/CalmUtilityPreviews.kt").read_text()
for required in ("AnimatedContent(", "NavigationBarItemDefaults.colors(", "NavigationRail(",
                 "BackHandler(", "Saved securely on this device"):
    assert required in activity, required
assert "Live hazards not connected" in home
assert "not your safety or readiness level" in home
assert "Search topics" in guides and "No matching topics" in guides
assert "NOT YET REVIEWED" in guides
assert ".toggleable(" in supplies and "onCheckedChange = null" in supplies
assert "not an assessment of emergency readiness" in supplies
assert "Android Keystore" in plan
assert "ANIMATOR_DURATION_SCALE" in components
assert previews.count("@Preview(") >= 6
assert "widthDp = 320" in previews and "fontScale = 1.3f" in previews
artwork = root / "app/src/main/res/drawable-nodpi/calm_countryside.jpg"
assert artwork.is_file() and artwork.read_bytes().startswith(b"\xff\xd8\xff")
assert artwork.stat().st_size < 200_000, "Decorative image should stay lightweight"
# Treat the concept percentage, speculative local hazard forecast and dates as illustration only.
assert "78%" not in home + supplies
assert "Most likely in the next 30 days" not in home

assert not list(root.rglob("*.apk")), "Never commit generated APKs"
print(f"Static checks passed: {len(files)} Kotlin files, {len(xmls)} valid XML files, no dangerous permissions")

# Pantry regression checks: real camera analysis, local database and editable stock.
assert "ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST" in combined
assert "BarcodeScanning.getClient(" in combined
assert "PantryStore(" in combined
assert "world.openfoodfacts.org/api/v2/product/" in combined
assert "CREATE TABLE pantry_items" in combined
assert "pantryValidationError" in combined
