package com.justtobesafe.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowForwardIos
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.justtobesafe.app.ui.components.SafetyNotice
import com.justtobesafe.app.ui.components.SectionTitle
import com.justtobesafe.app.ui.components.SmallBadge
import com.justtobesafe.app.ui.components.SoftCard

/** Topics alone are available offline. These entries contain no unreviewed safety procedures. */
enum class GuideTopic(val id: String, val title: String, val subtitle: String) {
    POWER("power", "Power cuts", "Information to explore before an electricity interruption"),
    FLOOD("flood", "Flooding", "Find official flood information and services"),
    WEATHER("weather", "Severe weather", "Find the official UK weather warning service"),
    FIRST_AID("aid", "First aid basics", "Procedures require specialist clinical review")
}

private fun topicIcon(topic: GuideTopic): ImageVector = when (topic) {
    GuideTopic.POWER -> Icons.Outlined.Bolt
    GuideTopic.FLOOD -> Icons.Outlined.WaterDrop
    GuideTopic.WEATHER -> Icons.Outlined.Cloud
    GuideTopic.FIRST_AID -> Icons.Outlined.MedicalServices
}

@Composable
private fun TopicCard(topic: GuideTopic, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val background = when (topic) {
        GuideTopic.POWER -> colors.tertiaryContainer
        GuideTopic.FLOOD -> colors.secondaryContainer
        GuideTopic.WEATHER -> colors.surfaceVariant
        GuideTopic.FIRST_AID -> colors.primaryContainer
    }
    val foreground = when (topic) {
        GuideTopic.POWER -> colors.onTertiaryContainer
        GuideTopic.FLOOD -> colors.onSecondaryContainer
        GuideTopic.WEATHER -> colors.onSurfaceVariant
        GuideTopic.FIRST_AID -> colors.onPrimaryContainer
    }
    SoftCard {
        Row(
            Modifier.fillMaxWidth().clickable(onClickLabel = "Open ${topic.title}", onClick = onClick)
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(13.dp)
        ) {
            Box(
                Modifier.size(57.dp).background(background, RoundedCornerShape(17.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(topicIcon(topic), contentDescription = null,
                    modifier = Modifier.size(30.dp), tint = foreground)
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(topic.title, style = MaterialTheme.typography.titleMedium)
                Text(topic.subtitle, style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurfaceVariant)
                Text("Topic preview", style = MaterialTheme.typography.labelSmall, color = colors.primary)
            }
            Icon(Icons.Outlined.ArrowForwardIos, contentDescription = null,
                tint = colors.onSurfaceVariant, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun GuidesScreen(
    modifier: Modifier = Modifier,
    selectedGuide: GuideTopic? = null,
    onSelect: (String) -> Unit,
    openOfficial: (String) -> Unit,
    onBack: () -> Unit = {}
) {
    var query by rememberSaveable { mutableStateOf("") }
    val colors = MaterialTheme.colorScheme

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (selectedGuide == null) {
            item(key = "guides-title") {
                SectionTitle("Emergency topics", "Find your way to official UK information")
            }
            item(key = "search") {
                OutlinedTextField(
                    value = query, onValueChange = { query = it.take(100) },
                    modifier = Modifier.fillMaxWidth(), singleLine = true,
                    label = { Text("Search topics") },
                    leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                    trailingIcon = {
                        if (query.isNotBlank()) IconButton(onClick = { query = "" }) {
                            Icon(Icons.Outlined.Close, contentDescription = "Clear search")
                        }
                    }, shape = RoundedCornerShape(15.dp)
                )
            }
            item(key = "offline-status") {
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SmallBadge("TOPIC INDEX SAVED OFFLINE")
                }
            }
            val visible = GuideTopic.entries.filter {
                query.isBlank() || it.title.contains(query.trim(), ignoreCase = true) ||
                    it.subtitle.contains(query.trim(), ignoreCase = true)
            }
            if (visible.isEmpty()) {
                item(key = "empty") {
                    SoftCard {
                        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("No matching topics", style = MaterialTheme.typography.titleMedium)
                            Text("Try a different search, or clear it to see every topic.",
                                style = MaterialTheme.typography.bodyMedium, color = colors.onSurfaceVariant)
                        }
                    }
                }
            } else {
                items(visible, key = { it.id }) { topic ->
                    TopicCard(topic, onClick = { onSelect(topic.id) })
                }
            }
            item(key = "source-link") {
                OutlinedButton(
                    onClick = { openOfficial("https://beready.campaign.gov.uk/") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Outlined.Public, contentDescription = null,
                        modifier = Modifier.size(19.dp))
                    Spacer(Modifier.width(9.dp))
                    Text("Open official UK preparedness website")
                }
            }
            item(key = "guides-disclaimer") {
                SafetyNotice("Only topic titles and descriptions are available offline at present. Detailed emergency advice has not been published: qualified specialist and licensing review is still required.")
            }
        } else {
            item(key = "return") {
                OutlinedButton(onClick = onBack) {
                    Icon(Icons.Outlined.ArrowBack, contentDescription = null,
                        modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("All topics")
                }
            }
            item(key = "detail-hero") {
                val background = when (selectedGuide) {
                    GuideTopic.POWER -> colors.tertiaryContainer
                    GuideTopic.FLOOD -> colors.secondaryContainer
                    GuideTopic.WEATHER -> colors.surfaceVariant
                    GuideTopic.FIRST_AID -> colors.primaryContainer
                }
                val foreground = when (selectedGuide) {
                    GuideTopic.POWER -> colors.onTertiaryContainer
                    GuideTopic.FLOOD -> colors.onSecondaryContainer
                    GuideTopic.WEATHER -> colors.onSurfaceVariant
                    GuideTopic.FIRST_AID -> colors.onPrimaryContainer
                }
                Box(Modifier.fillMaxWidth().background(background, RoundedCornerShape(18.dp))) {
                    Column(Modifier.padding(21.dp), verticalArrangement = Arrangement.spacedBy(11.dp)) {
                        Icon(topicIcon(selectedGuide), contentDescription = null,
                            tint = foreground, modifier = Modifier.size(35.dp))
                        Text(selectedGuide.title, style = MaterialTheme.typography.headlineMedium,
                            color = foreground)
                        Text(selectedGuide.subtitle, style = MaterialTheme.typography.bodyMedium,
                            color = foreground)
                    }
                }
            }
            item(key = "review-status") {
                SmallBadge("NOT YET REVIEWED · PLACEHOLDER",
                    containerColor = colors.tertiaryContainer,
                    textColor = colors.onTertiaryContainer)
            }
            item(key = "guidance-pending") {
                SoftCard {
                    Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Guidance is in preparation", style = MaterialTheme.typography.titleLarge)
                        Text("This page demonstrates the app's reading experience. Safety procedures will appear here only after expert review, source verification and rights checks.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.onSurfaceVariant)
                        Text("Offline today: this topic's title and status only.",
                            style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            item(key = "official") {
                Button(
                    onClick = {
                        openOfficial(if (selectedGuide == GuideTopic.WEATHER)
                            "https://weather.metoffice.gov.uk/warnings-and-advice/uk-warnings"
                        else "https://beready.campaign.gov.uk/")
                    }, modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Outlined.Public, contentDescription = null,
                        modifier = Modifier.size(19.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("View official external guidance")
                }
            }
            item(key = "network-note") {
                SafetyNotice("Opening official sources requires a connection. This prototype does not reproduce, verify or monitor third-party guidance.")
            }
        }
        item(key = "bottom") { Spacer(Modifier.height(16.dp)) }
    }
}
