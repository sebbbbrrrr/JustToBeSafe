package com.justtobesafe.app.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/** Read-only bundled editorial index. No safety instructions are authorised for publication. */
data class UrbanGuideSection(
    val id: String,
    val title: String,
    val status: String
)

data class UrbanGuideTopic(
    val id: String,
    val title: String,
    val priority: Int,
    val category: String,
    val geographicScope: String,
    val tags: List<String>,
    val sourceUrls: List<String>,
    val sections: List<UrbanGuideSection>,
    val safetyReview: String,
    val offlineBodyAvailable: Boolean
)

data class UrbanGuideCatalogue(
    val topics: List<UrbanGuideTopic>,
    val edition: String
) {
    fun find(id: String?): UrbanGuideTopic? = topics.firstOrNull { it.id == id }
    fun search(query: String): List<UrbanGuideTopic> {
        val term = query.trim()
        if (term.isEmpty()) return topics
        return topics.filter { topic ->
            (listOf(topic.title, topic.category, topic.geographicScope) + topic.tags)
                .any { it.contains(term, ignoreCase = true) }
        }
    }

    companion object {
        fun load(context: Context): Result<UrbanGuideCatalogue> = runCatching {
            val raw = context.assets.open("guides/uk_urban_guide_catalogue.json")
                .bufferedReader(Charsets.UTF_8).use { it.readText() }
            parse(raw)
        }

        fun parse(raw: String): UrbanGuideCatalogue {
            val root = JSONObject(raw)
            require(root.getInt("schema_version") == 1) { "Unsupported guide catalogue version" }
            require(root.getString("publication_state") == "internal_draft") {
                "Unreviewed guide catalogue must remain an internal draft"
            }
            val items = root.getJSONArray("topics")
            val seenIds = mutableSetOf<String>()
            val topics = buildList {
                for (i in 0 until items.length()) {
                    val item = items.getJSONObject(i)
                    val id = item.getString("id")
                    require(id.isNotBlank() && seenIds.add(id)) { "Invalid or duplicate guide ID" }
                    require(item.getString("state") == "internal_draft" &&
                        item.getString("safety_review") == "pending" &&
                        item.getString("reuse_rights") == "not_assessed" &&
                        !item.getBoolean("offline_body_available")) {
                        "Draft guide has an unsafe publication state"
                    }
                    val sources = item.getJSONArray("source_urls").strings()
                    require(sources.isNotEmpty() && sources.all { it.startsWith("https://") }) {
                        "Guide must have at least one HTTPS source candidate"
                    }
                    val sectionsJson = item.getJSONArray("content_sections")
                    val sections = buildList {
                        for (j in 0 until sectionsJson.length()) {
                            val section = sectionsJson.getJSONObject(j)
                            val status = section.getString("status")
                            require(status == "draft" || status == "blocked_expert_review") {
                                "Unexpected guide section publication state"
                            }
                            add(UrbanGuideSection(section.getString("id"),
                                section.getString("title"), status))
                        }
                    }
                    add(UrbanGuideTopic(
                        id = id,
                        title = item.getString("title"),
                        priority = item.getInt("priority"),
                        category = item.getString("category"),
                        geographicScope = item.getString("geographic_scope"),
                        tags = item.getJSONArray("tags").strings(),
                        sourceUrls = sources,
                        sections = sections,
                        safetyReview = item.getString("safety_review"),
                        offlineBodyAvailable = false
                    ))
                }
            }
            require(topics.isNotEmpty()) { "Guide catalogue is empty" }
            require(topics.map { it.priority }.toSet() == (1..topics.size).toSet()) {
                "Guide editorial priorities must be sequential"
            }
            return UrbanGuideCatalogue(topics.sortedBy { it.priority }, root.getString("edition"))
        }
    }
}

private fun JSONArray.strings(): List<String> = List(length()) { index -> getString(index) }
