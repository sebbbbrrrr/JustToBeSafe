package com.justtobesafe.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.justtobesafe.app.domain.SuppliesCatalog
import com.justtobesafe.app.domain.completedCount
import com.justtobesafe.app.domain.completionPercent
import com.justtobesafe.app.ui.components.ChecklistRing
import com.justtobesafe.app.ui.components.CountrysideHeader
import com.justtobesafe.app.ui.components.DestinationRow
import com.justtobesafe.app.ui.components.SafetyNotice
import com.justtobesafe.app.ui.components.SectionTitle
import com.justtobesafe.app.ui.components.SmallBadge
import com.justtobesafe.app.ui.components.SoftCard

@Composable
fun HomeScreen(checked: Set<String>, contacts: Int, hasMeetingPoint: Boolean,
    onGuides: () -> Unit, onSupplies: () -> Unit, onPlan: () -> Unit,
    onOfficialWarnings: () -> Unit) {
    val complete = completedCount(checked)
    val total = SuppliesCatalog.items.size
    val percent = completionPercent(complete, total)
    LazyColumn(Modifier.fillMaxWidth(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 18.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Good to have a plan.", style = MaterialTheme.typography.headlineLarge)
                Text("A calmer way to prepare your household.",
                    style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        item { SmallBadge("DEVELOPMENT PREVIEW · NOT AN ALERT SERVICE") }
        item { CountrysideHeader() }
        item {
            SoftCard {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Your supplies progress", style = MaterialTheme.typography.titleMedium)
                        Icon(Icons.Outlined.Inventory2, contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                        ChecklistRing(percent, diameter = 110.dp)
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("$complete of $total checked", style = MaterialTheme.typography.titleMedium)
                            Text("Tracks the items you marked — not a rating of how safe you are.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                            OutlinedButton(onClick = onSupplies) { Text("View supplies") }
                        }
                    }
                }
            }
        }
        item { SectionTitle("Your essentials", "Everything you need to organise, in one place") }
        item {
            DestinationRow("Emergency guides", "Topics available offline; instructions pending expert review",
                icon = { Icon(Icons.Outlined.MenuBook, contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary) }, onClick = onGuides)
        }
        item {
            DestinationRow("Household plan",
                "$contacts saved contact${if (contacts == 1) "" else "s"} · Meeting point ${if (hasMeetingPoint) "set" else "not set"}",
                icon = { Icon(Icons.Outlined.People, contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary) }, onClick = onPlan)
        }
        item { SectionTitle("Local information") }
        item {
            SoftCard {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Icon(Icons.Outlined.CloudOff, contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary)
                        Text("Live hazards not connected", style = MaterialTheme.typography.titleMedium)
                    }
                    Text("No location data is collected. This preview cannot assess local risk or show live warnings.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    OutlinedButton(onClick = onOfficialWarnings) {
                        Icon(Icons.Outlined.Public, contentDescription = null)
                        Spacer(Modifier.width(7.dp))
                        Text("Open official UK warnings")
                    }
                }
            }
        }
        item { SafetyNotice("Offline means this prototype's screens and saved checklist remain usable without internet. Full emergency procedures will be published only after expert and licensing review.") }
        item { Spacer(Modifier.height(18.dp)) }
    }
}
