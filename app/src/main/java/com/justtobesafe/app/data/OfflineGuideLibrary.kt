package com.justtobesafe.app.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/** Original in-app editorial text, stored in APK assets and readable without a network. */
data class OfflineGuideSection(
    val id: String,
    val title: String,
    val paragraphs: List<String>
)

data class OfflineGuideArticle(
    val topicId: String,
    val notice: String,
    val sections: List<OfflineGuideSection>
)

data class OfflineGuideLibrary(
    val edition: String,
    val articles: Map<String, OfflineGuideArticle>
) {
    fun articleFor(topicId: String?): OfflineGuideArticle? = articles[topicId]

    companion object {
        fun load(context: Context): Result<OfflineGuideLibrary> = runCatching {
            val json = context.assets.open("guides/uk_urban_guide_articles.json")
                .bufferedReader(Charsets.UTF_8).use { it.readText() }
            parse(json)
        }

        /** Fail closed: this reader supports only clearly labelled, unreviewed drafts. */
        fun parse(json: String): OfflineGuideLibrary {
            val root = JSONObject(json)
            require(root.getInt("schema_version") == 1)
            require(root.getString("publication_state") == "internal_draft")
            val all = root.getJSONArray("articles")
            val articles = mutableMapOf<String, OfflineGuideArticle>()
            for (i in 0 until all.length()) {
                val article = all.getJSONObject(i)
                require(article.getString("status") == "internal_draft" &&
                    article.getString("safety_review") == "pending") {
                    "Unexpected article publication state"
                }
                val topicId = article.getString("topic_id")
                require(topicId.isNotBlank() && !articles.containsKey(topicId)) {
                    "Missing or duplicate article ID"
                }
                val blocks = article.getJSONArray("sections")
                val sectionIds = mutableSetOf<String>()
                val sections = buildList {
                    for (j in 0 until blocks.length()) {
                        val section = blocks.getJSONObject(j)
                        val id = section.getString("id")
                        require(sectionIds.add(id) && id.isNotBlank())
                        val paragraphs = section.getJSONArray("paragraphs").paragraphStrings()
                        require(paragraphs.isNotEmpty() && paragraphs.all { it.isNotBlank() })
                        add(OfflineGuideSection(id, section.getString("title"), paragraphs))
                    }
                }
                require(sections.isNotEmpty())
                articles[topicId] = OfflineGuideArticle(
                    topicId,
                    article.getString("display_notice"),
                    sections
                )
            }
            require(articles.isNotEmpty())
            return OfflineGuideLibrary(root.getString("edition"), articles)
        }
    }
}

private fun JSONArray.paragraphStrings(): List<String> =
    List(length()) { getString(it) }
