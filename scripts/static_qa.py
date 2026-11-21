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
for permission in ("INTERNET", "ACCESS_FINE_LOCATION", "ACCESS_COARSE_LOCATION",
                   "READ_CONTACTS", "CALL_PHONE", "POST_NOTIFICATIONS"):
    assert f'android.permission.{permission}' not in manifest_text, permission
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

assert not list(root.rglob("*.apk")), "Never commit generated APKs"
print(f"Static checks passed: {len(files)} Kotlin files, {len(xmls)} valid XML files, no dangerous permissions")
