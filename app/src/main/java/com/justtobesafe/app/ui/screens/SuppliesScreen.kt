package com.justtobesafe.app.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BatteryChargingFull
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.LocalDining
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.Radio
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import com.justtobesafe.app.domain.SuppliesCatalog
import com.justtobesafe.app.domain.SupplyItem
import com.justtobesafe.app.domain.completedCount
import com.justtobesafe.app.domain.completionPercent
import com.justtobesafe.app.ui.components.SafetyNotice
import com.justtobesafe.app.ui.components.SectionTitle
import com.justtobesafe.app.ui.components.SmallBadge
import com.justtobesafe.app.ui.components.SoftCard
import com.justtobesafe.app.ui.components.calmMotionDuration

private fun itemIcon(item: SupplyItem): ImageVector = when (item.id) {
    "water" -> Icons.Outlined.WaterDrop
    "food" -> Icons.Outlined.LocalDining
    "torch" -> Icons.Outlined.Lightbulb
    "firstaid" -> Icons.Outlined.MedicalServices
    "power" -> Icons.Outlined.BatteryChargingFull
    "radio" -> Icons.Outlined.Radio
    "medicines" -> Icons.Outlined.FavoriteBorder
    "documents" -> Icons.Outlined.Description
    else -> Icons.Outlined.Inventory2
}

@Composable
private fun SupplyRow(item: SupplyItem, selected: Boolean, onToggle: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val tint by animateColorAsState(
        targetValue = if (selected) colors.primaryContainer.copy(alpha = 0.28f) else colors.surface,
        animationSpec = tween(calmMotionDuration(170)), label = "Supply selection background"
    )
    Row(
        modifier = Modifier.fillMaxWidth().background(tint)
            .toggleable(value = selected, role = Role.Checkbox,
                onValueChange = { onToggle() })
            .heightIn(min = 76.dp)
            .padding(start = 12.dp, end = 13.dp, top = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        Checkbox(
            checked = selected, onCheckedChange = null,
            modifier = Modifier.clearAndSetSemantics { }
        )
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(item.title, style = MaterialTheme.typography.titleSmall)
            Text(item.subtitle, style = MaterialTheme.typography.bodySmall,
                color = colors.onSurfaceVariant)
            Text(if (selected) "Marked available" else "Not yet marked",
                style = MaterialTheme.typography.labelSmall,
                color = if (selected) colors.primary else colors.onSurfaceVariant)
        }
        Icon(itemIcon(item), contentDescription = null,
            modifier = Modifier.size(23.dp),
            tint = if (selected) colors.primary else colors.secondary)
    }
}

@Composable
fun SuppliesScreen(checked: Set<String>, onToggle: (String) -> Unit) {
    var selected by rememberSaveable { mutableStateOf("checklist") }
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FilterChip(selected = selected == "checklist",
                onClick = { selected = "checklist" }, label = { Text("Checklist") })
            FilterChip(selected = selected == "pantry",
                onClick = { selected = "pantry" }, label = { Text("Food pantry") })
        }
        Box(Modifier.weight(1f)) {
            if (selected == "pantry") PantryScreen()
            else SuppliesChecklist(checked = checked, onToggle = onToggle)
        }
    }
}

@Composable
private fun SuppliesChecklist(checked: Set<String>, onToggle: (String) -> Unit) {
    val done = completedCount(checked)
    val total = SuppliesCatalog.items.size
    val percentage = completionPercent(done, total)
    val colors = MaterialTheme.colorScheme
    val animated by animateFloatAsState(
        targetValue = percentage / 100f,
        animationSpec = tween(calmMotionDuration(250)), label = "Supplies progress"
    )

    LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item(key = "supplies-title") {
            SectionTitle("Household supplies", "Keep an easy record of the essentials you have")
        }
        item(key = "supplies-status") {
            SmallBadge("SAVED LOCALLY · AVAILABLE OFFLINE")
        }
        item(key = "supplies-progress") {
            SoftCard {
                Column(Modifier.padding(17.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Text("Your supplies checklist", style = MaterialTheme.typography.titleMedium)
                            Text("$done of $total items marked available",
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.onSurfaceVariant)
                        }
                        Text("$percentage%", style = MaterialTheme.typography.headlineMedium,
                            color = colors.primary)
                    }
                    LinearProgressIndicator(
                        progress = { animated }, modifier = Modifier.fillMaxWidth().height(8.dp),
                        trackColor = colors.surfaceVariant, color = colors.primary
                    )
                    Text("This is a personal inventory tracker, not an assessment of emergency readiness.",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.onSurfaceVariant)
                }
            }
        }
        item(key = "supplies-checklist-title") {
            SectionTitle("Review your items", "Tap an item to update its availability")
        }
        item(key = "supplies-list") {
            SoftCard {
                Column {
                    SuppliesCatalog.items.forEachIndexed { index, item ->
                        SupplyRow(item, selected = item.id in checked, onToggle = { onToggle(item.id) })
                        if (index < SuppliesCatalog.items.lastIndex) {
                            HorizontalDivider(
                                Modifier.padding(start = 61.dp, end = 12.dp),
                                color = colors.outlineVariant
                            )
                        }
                    }
                }
            }
        }
        item(key = "offline-pack") {
            Surface(
                color = colors.primaryContainer,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(17.dp)
            ) {
                Row(Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Outlined.CheckCircleOutline, contentDescription = null,
                        modifier = Modifier.size(27.dp), tint = colors.onPrimaryContainer)
                    Column(Modifier.weight(1f)) {
                        Text("Available offline", style = MaterialTheme.typography.titleMedium,
                            color = colors.onPrimaryContainer)
                        Text("Your selections are saved on this device, with no account or network required.",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.onPrimaryContainer)
                    }
                }
            }
        }
        item(key = "supplies-notice") {
            SafetyNotice("These categories are organisational prompts. Household-specific quantities and emergency recommendations must be sourced and reviewed before any wider release.")
        }
        item { Spacer(Modifier.height(15.dp)) }
    }
}
