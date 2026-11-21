package com.justtobesafe.app.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowForwardIos
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Search
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.justtobesafe.app.data.UrbanGuideCatalogue
import com.justtobesafe.app.data.UrbanGuideTopic
import com.justtobesafe.app.ui.components.SafetyNotice
import com.justtobesafe.app.ui.components.SectionTitle
import com.justtobesafe.app.ui.components.SmallBadge
import com.justtobesafe.app.ui.components.SoftCard

@Composable
private fun UrbanGuideRow(topic: UrbanGuideTopic, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    SoftCard {
        Row(
            modifier = Modifier.fillMaxWidth()
                .clickable(onClickLabel = "Open " + topic.title, onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 17.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                if (topic.id == "power-cuts") Icons.Outlined.Bolt else Icons.Outlined.MenuBook,
                contentDescription = null, tint = colors.primary, modifier = Modifier.size(28.dp)
            )
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(topic.title, style = MaterialTheme.typography.titleMedium,
                    maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(
                    "Development order " + topic.priority + " · " + scopeLabel(topic.geographicScope),
                    style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant
                )
                Text("NOT YET REVIEWED · Draft outline",
                    style = MaterialTheme.typography.labelSmall, color = colors.primary)
            }
            Icon(Icons.Outlined.ArrowForwardIos, contentDescription = null,
                tint = colors.onSurfaceVariant, modifier = Modifier.size(15.dp))
        }
    }
}

private fun scopeLabel(scope: String): String = when (scope) {
    "all-uk" -> "UK-wide topic"
    "region-specific" -> "Regional details required"
    "building-specific" -> "Building-specific"
    "local-specific" -> "Local details required"
    else -> "Scope to be verified"
}

/** Metadata-only urban guide library; editorial prompts and safety procedures are not shown. */
@Composable
fun GuidesScreen(
    catalogue: UrbanGuideCatalogue? = null,
    selectedGuideId: String? = null,
    onSelect: (String) -> Unit,
    openOfficial: (String) -> Unit,
    onBack: () -> Unit = {}
) {
    var query by rememberSaveable { mutableStateOf("") }
    val colors = MaterialTheme.colorScheme
    val selected = catalogue?.find(selectedGuideId)
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (selectedGuideId != null && selected != null) {
            item(key = "back") {
                OutlinedButton(onClick = onBack) {
                    Icon(Icons.Outlined.ArrowBack, contentDescription = null,
                        modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("All topics")
                }
            }
            item(key = "heading") {
                Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                    SectionTitle(selected.title,
                        "Urban preparedness · " + scopeLabel(selected.geographicScope))
                    SmallBadge("NOT YET REVIEWED · OUTLINE ONLY")
                }
            }
            item(key = "safety-status") {
                SafetyNotice("This is an unpublished planning outline, not emergency safety guidance. " +
                    "Procedures require qualified review and licensing clearance before publication. " +
                    "No live warnings or current electricity status are connected.")
            }
            if (selected.sections.isNotEmpty()) {
                item(key = "structure-title") {
                    SectionTitle("Planned guide sections",
                        "Available offline as a section index, not reviewed instructions")
                }
                items(selected.sections, key = { it.id }) { section ->
                    SoftCard {
                        Row(Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.Top) {
                            Icon(Icons.Outlined.MenuBook, contentDescription = null,
                                tint = colors.primary, modifier = Modifier.size(22.dp))
                            Column(Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(section.title, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    if (section.status == "blocked_expert_review")
                                        "Awaiting specialist safety review"
                                    else "Editorial section planned · content pending review",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            } else {
                item(key = "not-written") {
                    SoftCard {
                        Column(Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Guide being planned",
                                style = MaterialTheme.typography.titleMedium)
                            Text("The topic is included in our development roadmap. " +
                                "No reviewed guide content is available yet.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.onSurfaceVariant)
                        }
                    }
                }
            }
            item(key = "source-status") {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SectionTitle("Official reference links",
                        "External pages need internet and may be updated by their publishers")
                    selected.sourceUrls.forEachIndexed { index, source ->
                        OutlinedButton(onClick = { openOfficial(source) },
                            modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Outlined.Public, contentDescription = null,
                                modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Open source candidate " + (index + 1))
                        }
                    }
                    Text("Links are reference candidates only. Their content, licences, " +
                        "geographic applicability and review dates have not been cleared.",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.onSurfaceVariant)
                }
            }
        } else {
            item(key = "guides-title") {
                SectionTitle("Urban preparedness",
                    "Explore practical topics, ordered for development relevance")
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
                    }
                )
            }
            if (catalogue == null) {
                item(key = "unavailable") {
                    SafetyNotice("The bundled guide index could not be loaded. " +
                        "No emergency instructions are available in this preview. " +
                        "You can still consult official sources when connected.")
                }
            } else {
                item(key = "offline-status") {
                    Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                        SmallBadge(catalogue.topics.size.toString() +
                            " DRAFT TOPICS · INDEX AVAILABLE OFFLINE")
                        Text("Order reflects editorial development priorities, not " +
                            "your local probability of an incident.",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.onSurfaceVariant)
                    }
                }
                val visible = catalogue.search(query)
                if (visible.isEmpty()) {
                    item(key = "empty") {
                        SoftCard {
                            Column(Modifier.padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("No matching topics",
                                    style = MaterialTheme.typography.titleMedium)
                                Text("Try a different search, or clear it to see every topic.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = colors.onSurfaceVariant)
                            }
                        }
                    }
                } else {
                    items(visible, key = { it.id }) { topic ->
                        UrbanGuideRow(topic, onClick = { onSelect(topic.id) })
                    }
                }
                item(key = "draft-notice") {
                    SafetyNotice("NOT YET REVIEWED: These are topic and section outlines only. " +
                        "Detailed emergency procedures are not published in this preview.")
                }
            }
            item(key = "official-uk") {
                OutlinedButton(
                    onClick = { openOfficial("https://beready.campaign.gov.uk/") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Outlined.Public, contentDescription = null,
                        modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Official UK preparedness website")
                }
            }
        }
        item(key = "bottom") { Spacer(Modifier.height(16.dp)) }
    }
}
