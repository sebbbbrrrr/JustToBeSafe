package com.justtobesafe.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForwardIos
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.PeopleOutline
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.justtobesafe.app.domain.SuppliesCatalog
import com.justtobesafe.app.domain.completedCount
import com.justtobesafe.app.domain.completionPercent
import com.justtobesafe.app.ui.components.ChecklistRing
import com.justtobesafe.app.ui.components.CountrysideHeader
import com.justtobesafe.app.ui.components.SafetyNotice
import com.justtobesafe.app.ui.components.SectionTitle
import com.justtobesafe.app.ui.components.SmallBadge
import com.justtobesafe.app.ui.components.SoftCard

@Composable
private fun ShortcutTile(title: String, detail: String, icon: @Composable () -> Unit,
    modifier: Modifier = Modifier, onClick: () -> Unit) {
    SoftCard(modifier) {
        Column(
            Modifier.fillMaxWidth().clickable(onClickLabel = "Open $title", onClick = onClick)
                .padding(15.dp), verticalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            Box(Modifier.size(40.dp).background(MaterialTheme.colorScheme.primaryContainer,
                RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) { icon() }
            Text(title, style = MaterialTheme.typography.titleSmall)
            Text(detail, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun HomeScreen(
    checked: Set<String>, contacts: Int, hasMeetingPoint: Boolean,
    onGuides: () -> Unit, onSupplies: () -> Unit, onPlan: () -> Unit,
    onOfficialWarnings: () -> Unit
) {
    val complete = completedCount(checked)
    val total = SuppliesCatalog.items.size
    val percent = completionPercent(complete, total)
    val colors = MaterialTheme.colorScheme

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item(key = "welcome") {
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text("Good to have a plan.", style = MaterialTheme.typography.headlineLarge)
                Text("Make everyday preparation a little easier.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant)
            }
        }
        item(key = "landscape") {
            CountrysideHeader(title = "Looking after home starts with the little things")
        }
        item(key = "progress") {
            SoftCard {
                Column(Modifier.padding(17.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Your supplies checklist", style = MaterialTheme.typography.titleMedium)
                        Icon(Icons.Outlined.Inventory2, contentDescription = null, tint = colors.primary)
                    }
                    BoxWithConstraints(Modifier.fillMaxWidth()) {
                        val compact = maxWidth < 285.dp
                        if (compact) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                ChecklistRing(percent, diameter = 98.dp)
                                ProgressExplanation(complete, total, onSupplies)
                            }
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(15.dp)) {
                                ChecklistRing(percent, diameter = 105.dp)
                                Box(Modifier.weight(1f)) {
                                    ProgressExplanation(complete, total, onSupplies)
                                }
                            }
                        }
                    }
                    Text("This tracks items marked available, not your safety or readiness level.",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.onSurfaceVariant)
                }
            }
        }
        item(key = "local") {
            Surface(color = colors.secondaryContainer,
                shape = RoundedCornerShape(17.dp)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(11.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(Modifier.size(42.dp).background(colors.surface.copy(alpha = 0.65f),
                            RoundedCornerShape(13.dp)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Outlined.CloudOff, contentDescription = null,
                                tint = colors.onSecondaryContainer)
                        }
                        Column(Modifier.weight(1f)) {
                            Text("Local information", style = MaterialTheme.typography.titleMedium,
                                color = colors.onSecondaryContainer)
                            Text("Live hazards not connected", style = MaterialTheme.typography.bodySmall,
                                color = colors.onSecondaryContainer)
                        }
                        SmallBadge("NOT LIVE", containerColor = colors.surface,
                            textColor = colors.onSurface)
                    }
                    Text("This prototype doesn't collect your location or predict local risks. Visit the official UK warning service for current conditions.",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.onSecondaryContainer)
                    OutlinedButton(onClick = onOfficialWarnings, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Outlined.Public, contentDescription = null,
                            modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Check official warnings")
                    }
                }
            }
        }
        item(key = "quickTitle") {
            SectionTitle("Your essentials", "Plan ahead, organise supplies and find information")
        }
        item(key = "shortcuts") {
            BoxWithConstraints(Modifier.fillMaxWidth()) {
                val compact = maxWidth < 350.dp
                val tileGuides: @Composable (Modifier) -> Unit = { modifier ->
                    ShortcutTile("Emergency topics", "Explore the offline topic index", icon = {
                        Icon(Icons.Outlined.MenuBook, contentDescription = null, tint = colors.primary)
                    }, modifier = modifier, onClick = onGuides)
                }
                val tilePlan: @Composable (Modifier) -> Unit = { modifier ->
                    ShortcutTile("Household plan", "$contacts contact${if (contacts == 1) "" else "s"} · Meeting point ${if (hasMeetingPoint) "set" else "to add"}",
                        icon = { Icon(Icons.Outlined.PeopleOutline, contentDescription = null,
                            tint = colors.primary) }, modifier = modifier, onClick = onPlan)
                }
                if (compact) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        tileGuides(Modifier.fillMaxWidth())
                        tilePlan(Modifier.fillMaxWidth())
                    }
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        tileGuides(Modifier.weight(1f))
                        tilePlan(Modifier.weight(1f))
                    }
                }
            }
        }
        item(key = "offline") {
            SoftCard {
                Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(11.dp)) {
                    Icon(Icons.Outlined.CheckCircleOutline, contentDescription = null,
                        tint = colors.primary)
                    Column(Modifier.weight(1f)) {
                        Text("Available without internet", style = MaterialTheme.typography.titleSmall)
                        Text("Your supply checklist and topic index are stored on this device.",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.onSurfaceVariant)
                    }
                }
            }
        }
        item(key = "preview") {
            SafetyNotice("Development preview: emergency procedures and live warnings are not provided in this app. Expert-reviewed content and licensing checks are required before release.")
        }
        item { Spacer(Modifier.height(12.dp)) }
    }
}

@Composable
private fun ProgressExplanation(done: Int, total: Int, onSupplies: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
        Text("$done of $total items checked", style = MaterialTheme.typography.titleMedium)
        Text("Track what you have and what still needs attention.",
            style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant)
        Button(onClick = onSupplies) {
            Text("Review supplies")
            Spacer(Modifier.width(7.dp))
            Icon(Icons.Outlined.ArrowForwardIos, contentDescription = null, modifier = Modifier.size(12.dp))
        }
    }
}
