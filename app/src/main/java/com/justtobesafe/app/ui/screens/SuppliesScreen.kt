package com.justtobesafe.app.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.justtobesafe.app.domain.SuppliesCatalog
import com.justtobesafe.app.domain.completedCount
import com.justtobesafe.app.domain.completionPercent
import com.justtobesafe.app.ui.components.SafetyNotice
import com.justtobesafe.app.ui.components.SectionTitle
import com.justtobesafe.app.ui.components.SmallBadge
import com.justtobesafe.app.ui.components.SoftCard

@Composable
fun SuppliesScreen(checked: Set<String>, onToggle: (String) -> Unit) {
    val done = completedCount(checked)
    val total = SuppliesCatalog.items.size
    val progress = completionPercent(done, total)
    val animatedProgress by animateFloatAsState(
        progress / 100f, animationSpec = tween(durationMillis = 260), label = "Supply list progress")

    LazyColumn(contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item { SectionTitle("Household supplies", "A practical inventory you can keep up to date") }
        item { SmallBadge("SAVED ON THIS DEVICE · WORKS OFFLINE") }
        item {
            SoftCard {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text("Your checklist", style = MaterialTheme.typography.titleLarge)
                            Text("$done of $total items marked available",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text("$progress%", style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary)
                    }
                    LinearProgressIndicator(progress = { animatedProgress },
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        color = MaterialTheme.colorScheme.primary)
                    Text("Your selections are reminders, not confirmation of complete emergency readiness.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        item { SectionTitle("Items to review") }
        item {
            SoftCard {
                Column {
                    SuppliesCatalog.items.forEachIndexed { index, supply ->
                        val selected = supply.id in checked
                        Row(
                            Modifier.fillMaxWidth()
                                .semantics { stateDescription = if (selected) "Available" else "Not marked available" }
                                .clickable(role = Role.Checkbox, onClickLabel = "Toggle ${supply.title}") {
                                    onToggle(supply.id)
                                }
                                .padding(horizontal = 11.dp, vertical = 9.dp)
                                .animateContentSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Checkbox(checked = selected, onCheckedChange = { onToggle(supply.id) })
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(supply.title, style = MaterialTheme.typography.titleSmall)
                                Text(supply.subtitle, style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        if (index != SuppliesCatalog.items.lastIndex)
                            HorizontalDivider(Modifier.padding(start = 60.dp, end = 12.dp),
                                color = MaterialTheme.colorScheme.outlineVariant)
                    }
                }
            }
        }
        item {
            SoftCard {
                Row(Modifier.padding(17.dp), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Outlined.Lock, contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary)
                    Column(Modifier.weight(1f)) {
                        Text("Available offline", style = MaterialTheme.typography.titleSmall)
                        Text("This checklist is saved locally without an account or internet connection.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
        item { SafetyNotice("Suggested items are organisational prompts only. Amounts and household-specific guidance require verified source material and expert review before publication.") }
        item { Spacer(Modifier.height(18.dp)) }
    }
}
