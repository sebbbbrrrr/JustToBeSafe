package com.justtobesafe.app.ui

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.justtobesafe.app.data.HouseholdPlan
import com.justtobesafe.app.ui.screens.GuidesScreen
import com.justtobesafe.app.ui.screens.HomeScreen
import com.justtobesafe.app.ui.screens.PlanScreen
import com.justtobesafe.app.ui.screens.SuppliesScreen
import com.justtobesafe.app.ui.theme.CalmUtilityTheme

/** Static, non-personal preview data. These are UI fixtures, not claims about hazards. */
@Preview(name = "Home / compact Android", widthDp = 360, heightDp = 760, showBackground = true)
@Composable
private fun HomePreview() = CalmUtilityTheme {
    HomeScreen(checked = setOf("water", "torch", "radio"), contacts = 0,
        hasMeetingPoint = false, onGuides = {}, onSupplies = {},
        onPlan = {}, onOfficialWarnings = {})
}

@Preview(name = "Home / larger text", widthDp = 320, heightDp = 760,
    fontScale = 1.3f, showBackground = true)
@Composable
private fun HomeLargeTextPreview() = CalmUtilityTheme {
    HomeScreen(checked = emptySet(), contacts = 0, hasMeetingPoint = false,
        onGuides = {}, onSupplies = {}, onPlan = {}, onOfficialWarnings = {})
}

@Preview(name = "Guides / Android", widthDp = 360, heightDp = 760, showBackground = true)
@Composable
private fun GuidesPreview() = CalmUtilityTheme {
    GuidesScreen(onSelect = {}, openOfficial = {})
}

@Preview(name = "Supplies / Android", widthDp = 360, heightDp = 760, showBackground = true)
@Composable
private fun SuppliesPreview() = CalmUtilityTheme {
    SuppliesScreen(checked = setOf("water", "food", "firstaid"), onToggle = {})
}

@Preview(name = "Plan / Android", widthDp = 360, heightDp = 760, showBackground = true)
@Composable
private fun PlanPreview() = CalmUtilityTheme {
    PlanScreen(plan = HouseholdPlan(), loaded = true, error = null, onSave = {})
}

@Preview(name = "Supplies / dark Android", widthDp = 360, heightDp = 760,
    uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun DarkSuppliesPreview() = CalmUtilityTheme {
    SuppliesScreen(checked = setOf("water"), onToggle = {})
}
