package com.justtobesafe.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.material3.FilterChip
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
import com.justtobesafe.app.data.OfflineGuideLibrary
import com.justtobesafe.app.data.OfflineKnowledgeLibrary
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
                Text("OFFLINE ARTICLE · Unreviewed draft",
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

/** Full offline editorial reader, with non-publishable draft safety status. */
@Composable
fun GuidesScreen(
    catalogue: UrbanGuideCatalogue? = null,
    offlineLibrary: OfflineGuideLibrary? = null,
    knowledge: OfflineKnowledgeLibrary? = null,
    selectedGuideId: String? = null,
    onSelect: (String) -> Unit,
    openOfficial: (String) -> Unit,
    onBack: () -> Unit = {}
) {
    var query by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("All") }
    val colors = MaterialTheme.colorScheme
    val selected = catalogue?.find(selectedGuideId)
    val knowledgeSelected = knowledge?.find(selectedGuideId)
    val knowledgeArticle = androidx.compose.runtime.remember(knowledge, selectedGuideId) {
        if (knowledgeSelected != null) knowledge?.readArticle(selectedGuideId)?.getOrNull() else null
    }
    val article = offlineLibrary?.articleFor(selectedGuideId)
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (selectedGuideId != null && knowledgeSelected != null) {
            item(key = "knowledge-back") {
                OutlinedButton(onClick = onBack) {
                    Icon(Icons.Outlined.ArrowBack, contentDescription = null,
                        modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("All topics")
                }
            }
            item(key = "knowledge-heading") {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SectionTitle(knowledgeSelected.title,
                        knowledgeSelected.category + " · Offline reference article")
                    SmallBadge("OFFLINE · UNREVIEWED DRAFT")
                    SafetyNotice(
                        if (knowledgeSelected.risk == "medical" || knowledgeSelected.risk == "electrical")
                            "This topic involves health or electrical hazards. It is draft reference text, not professionally reviewed instructions. Seek suitable qualified help for risky tasks."
                        else "This is a development draft. Incident-specific official instructions and live warnings are not available offline."
                    )
                }
            }
            if (knowledgeArticle == null) {
                item(key = "knowledge-error") {
                    SafetyNotice("The locally stored article could not be read. No instructions are displayed.")
                }
            } else {
                items(knowledgeArticle.sections, key = { it.title }) { section ->
                    SoftCard {
                        Column(Modifier.fillMaxWidth().padding(17.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(section.title, style = MaterialTheme.typography.titleLarge)
                            section.paragraphs.forEach { paragraph ->
                                Text(paragraph, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
                item(key = "knowledge-footnote") {
                    Text("Original editorial draft, stored entirely in the APK. Not approved clinical, electrical or emergency guidance.",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.onSurfaceVariant)
                }
            }
        } else if (selectedGuideId != null && selected != null) {
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
                    SmallBadge("OFFLINE TEXT · NOT YET REVIEWED")
                }
            }
            item(key = "safety-status") {
                SafetyNotice("This is original, unreviewed editorial preview text stored on your device. " +
                    "It is not approved operational emergency guidance. Current warnings and " +
                    "incident-specific instructions are not available offline.")
            }
            if (article != null) {
                item(key = "article-status") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        SectionTitle("Read offline", "Complete local editorial draft · " +
                            article.sections.size + " sections")
                        Text("No connection, browser or account is needed to read the text below.",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.onSurfaceVariant)
                    }
                }
                items(article.sections, key = { it.id }) { section ->
                    SoftCard {
                        Column(Modifier.fillMaxWidth().padding(17.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(section.title, style = MaterialTheme.typography.titleLarge)
                            section.paragraphs.forEach { paragraph ->
                                Text(paragraph, style = MaterialTheme.typography.bodyMedium,
                                    color = colors.onSurface)
                            }
                        }
                    }
                }
            } else {
                item(key = "article-unavailable") {
                    SafetyNotice("Offline article text is unavailable on this installation. " +
                        "No safety instructions are available in this screen. " +
                        "Check app updates when a connection is available.")
                }
            }
            item(key = "source-status") {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SectionTitle("Sources and attribution", "Reference details stored offline")
                    Text("This article is original draft writing, not a reproduced official document. " +
                        "The following source candidates need attribution, rights and content review " +
                        "before release. The source URLs are recorded here for reference; you do " +
                        "not need to open them to read this article.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.onSurfaceVariant)
                    selected.sourceUrls.forEachIndexed { index, source ->
                        SoftCard {
                            Column(Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                Text("Reference " + (index + 1),
                                    style = MaterialTheme.typography.titleSmall)
                                Text(source, style = MaterialTheme.typography.bodySmall,
                                    color = colors.onSurfaceVariant)
                            }
                        }
                    }
                    SmallBadge("EDITORIAL DRAFT · NO REVIEW DATE")
                    Text("Official live alerts, current utility status and source updates " +
                        "cannot be guaranteed without connectivity.",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.onSurfaceVariant)
                }
            }
        } else {
            item(key = "guides-title") {
                SectionTitle("Offline knowledge library",
                    "Preparedness, first aid, home skills and urban disruptions")
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
            if (knowledge != null) {
                item(key = "knowledge-filters") {
                    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        (listOf("All") + knowledge.categories).forEach { value ->
                            FilterChip(selected = category == value,
                                onClick = { category = value },
                                label = { Text(value) })
                        }
                    }
                }
                val results = knowledge.search(query,
                    category.takeUnless { it == "All" })
                if (results.isNotEmpty()) {
                    item(key = "knowledge-count") {
                        SectionTitle("Practical reference", results.size.toString() +
                            " in-app articles · no connection required")
                    }
                    items(results, key = { "knowledge-" + it.id }) { entry ->
                        SoftCard {
                            Row(Modifier.fillMaxWidth()
                                .clickable(onClickLabel = "Read " + entry.title,
                                    onClick = { onSelect(entry.id) })
                                .padding(16.dp), verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Icon(Icons.Outlined.MenuBook, contentDescription = null,
                                    tint = colors.primary)
                                Column(Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(entry.title, style = MaterialTheme.typography.titleMedium)
                                    Text(entry.summary, style = MaterialTheme.typography.bodySmall,
                                        color = colors.onSurfaceVariant)
                                    Text(entry.category + " · Offline draft",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = colors.primary)
                                }
                                Icon(Icons.Outlined.ArrowForwardIos, contentDescription = null,
                                    tint = colors.onSurfaceVariant, modifier = Modifier.size(15.dp))
                            }
                        }
                    }
                }
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
                            " OFFLINE ARTICLES · UNREVIEWED DRAFTS")
                        Text("Order reflects editorial development priorities, not " +
                            "your local probability of an incident.",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.onSurfaceVariant)
                    }
                }
                val visible = if (category == "All" || category == "Urban emergencies")
                    catalogue.search(query) else emptyList()
                if (visible.isEmpty() && knowledge?.search(query,
                        category.takeUnless { it == "All" })?.isEmpty() != false) {
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
                    if (visible.isNotEmpty()) item(key = "urban-heading") {
                        SectionTitle("Urban emergencies", "12 situational articles")
                    }
                    items(visible, key = { it.id }) { topic ->
                        UrbanGuideRow(topic, onClick = { onSelect(topic.id) })
                    }
                }
                item(key = "draft-notice") {
                    SafetyNotice("These articles are readable offline, but remain unreviewed editorial " +
                        "drafts. Do not treat them as incident-specific emergency instructions.")
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
                    Text("Optional: visit UK preparedness website")
                }
            }
        }
        item(key = "bottom") { Spacer(Modifier.height(16.dp)) }
    }
}
