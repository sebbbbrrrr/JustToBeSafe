package com.justtobesafe.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.justtobesafe.app.ui.components.DestinationRow
import com.justtobesafe.app.ui.components.SafetyNotice
import com.justtobesafe.app.ui.components.SectionTitle
import com.justtobesafe.app.ui.components.SmallBadge
import com.justtobesafe.app.ui.components.SoftCard

/** The app intentionally ships no unapproved emergency procedure instructions. */
enum class GuideTopic(val id: String, val title: String, val subtitle: String) {
    POWER("power", "Power cuts", "Plan for interruptions to electricity"),
    FLOOD("flood", "Flooding", "Find official flood information and sources"),
    WEATHER("weather", "Severe weather", "Find the official UK weather warning service"),
    FIRST_AID("aid", "First aid basics", "Procedures require specialist clinical review")
}

@Composable
fun GuidesScreen(modifier: Modifier = Modifier, selectedGuide: GuideTopic? = null,
    onSelect: (String) -> Unit, openOfficial: (String) -> Unit, onBack: () -> Unit = {}) {
    LazyColumn(modifier, contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)) {
        if (selectedGuide == null) {
            item { SectionTitle("Emergency guides", "A simple index of topics to explore") }
            item { SmallBadge("INDEX AVAILABLE OFFLINE") }
            item { SafetyNotice("The guide index works without internet. Detailed emergency instructions are not included in this prototype; they await qualified expert and licensing review.") }
            items(GuideTopic.entries, key = { it.id }) { topic ->
                DestinationRow(topic.title, topic.subtitle, icon = {
                    val image = when (topic) {
                        GuideTopic.POWER -> Icons.Outlined.Bolt
                        GuideTopic.FLOOD -> Icons.Outlined.WaterDrop
                        GuideTopic.WEATHER -> Icons.Outlined.Cloud
                        GuideTopic.FIRST_AID -> Icons.Outlined.MedicalServices
                    }
                    Icon(image, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }, onClick = { onSelect(topic.id) })
            }
            item {
                OutlinedButton(onClick = { openOfficial("https://beready.campaign.gov.uk/") }) {
                    Icon(Icons.Outlined.Public, contentDescription = null)
                    Text("  View official UK preparedness resources")
                }
            }
        } else {
            item {
                OutlinedButton(onClick = onBack) {
                    Icon(Icons.Outlined.ArrowBack, contentDescription = null)
                    Text("  All guides")
                }
            }
            item { SectionTitle(selectedGuide.title, selectedGuide.subtitle) }
            item { SmallBadge("NOT YET REVIEWED · PLACEHOLDER") }
            item {
                SoftCard {
                    Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Guidance in preparation", style = MaterialTheme.typography.titleLarge)
                        Text("This page is an interface prototype. Detailed safety advice will appear only after expert review, source verification and licensing checks.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Currently available offline: this topic title and status only.",
                            style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            item {
                OutlinedButton(onClick = {
                    val link = if (selectedGuide == GuideTopic.WEATHER)
                        "https://weather.metoffice.gov.uk/warnings-and-advice/uk-warnings"
                    else "https://beready.campaign.gov.uk/"
                    openOfficial(link)
                }) {
                    Icon(Icons.Outlined.Public, contentDescription = null)
                    Text("  Read official external guidance")
                }
            }
            item { SafetyNotice("External official sources may change and require internet. This app does not reproduce or independently certify their advice.") }
        }
        item { Spacer(Modifier.height(20.dp)) }
    }
}
