package com.justtobesafe.app.data

import android.content.Context
import org.json.JSONObject
import java.util.zip.GZIPInputStream

/** Small metadata-only index; article bodies are gzip-sharded and loaded on demand. */
data class KnowledgeEntry(
    val id: String,
    val title: String,
    val category: String,
    val summary: String,
    val tags: List<String>,
    val risk: String,
    val review: String,
    val illustration: String?,
    val pack: String
)

data class KnowledgeSection(val title: String, val paragraphs: List<String>)
data class KnowledgeVisual(
    val kind: String,
    val alt: String,
    val caption: String,
    val labels: List<String>
)
data class KnowledgeArticle(
    val id: String,
    val sections: List<KnowledgeSection>,
    val visual: KnowledgeVisual? = null
)

class OfflineKnowledgeLibrary private constructor(
    private val context: Context,
    val entries: List<KnowledgeEntry>
) {
    val categories: List<String> = entries.map { it.category }.distinct().sorted()

    // Keep a maximum of two decoded category packs in memory. Future categories
    // can scale without eager inflation of the entire content library.
    private val cache = object : LinkedHashMap<String, Map<String, KnowledgeArticle>>(4, 0.75f, true) {
        override fun removeEldestEntry(
            eldest: MutableMap.MutableEntry<String, Map<String, KnowledgeArticle>>?
        ): Boolean = size > 2
    }

    fun find(id: String?): KnowledgeEntry? = entries.firstOrNull { it.id == id }

    fun search(query: String, category: String? = null): List<KnowledgeEntry> {
        val term = query.trim()
        return entries.filter { entry ->
            (category == null || entry.category == category) &&
                (term.isEmpty() || (listOf(entry.title, entry.summary, entry.category) + entry.tags)
                    .any { it.contains(term, ignoreCase = true) })
        }
    }

    fun readArticle(id: String?): Result<KnowledgeArticle> = runCatching {
        val entry = requireNotNull(find(id)) { "Unknown knowledge article" }
        val pack = synchronized(cache) {
            cache[entry.pack] ?: readPack(entry.pack).also { cache[entry.pack] = it }
        }
        requireNotNull(pack[entry.id]) { "Article missing from its category pack" }
    }

    private fun readPack(fileName: String): Map<String, KnowledgeArticle> {
        require(Regex("pack_[0-9]{2}\\.json\\.gz").matches(fileName)) {
            "Invalid pack identifier"
        }
        val bytes = context.assets.open("knowledge/$fileName").use { input ->
            GZIPInputStream(input).bufferedReader(Charsets.UTF_8).use { it.readText() }
        }
        val root = JSONObject(bytes)
        require(root.getInt("schema_version") == 1)
        val list = root.getJSONArray("articles")
        val articles = mutableMapOf<String, KnowledgeArticle>()
        for (i in 0 until list.length()) {
            val raw = list.getJSONObject(i)
            require(raw.getString("review") == "pending") {
                "Only clearly labelled drafts are supported in this preview"
            }
            val id = raw.getString("id")
            require(!articles.containsKey(id)) { "Duplicate packed article ID" }
            val blocks = raw.getJSONArray("sections")
            val sections = buildList {
                for (j in 0 until blocks.length()) {
                    val section = blocks.getJSONObject(j)
                    val texts = section.getJSONArray("paragraphs")
                    add(KnowledgeSection(section.getString("title"),
                        List(texts.length()) { k -> texts.getString(k) }))
                }
            }
            require(sections.isNotEmpty())
            val visual = raw.optJSONObject("visual")?.let { spec ->
                val kind = spec.getString("kind")
                require(kind in setOf("sequence", "checklist", "shelves", "route")) {
                    "Unsupported illustration kind"
                }
                require(raw.getString("risk") !in
                    setOf("medical", "electrical", "chemical", "public-health")) {
                    "Unreviewed technical/clinical procedure diagrams are not allowed"
                }
                val labelsJson = spec.getJSONArray("labels")
                val labels = (0 until labelsJson.length()).map { labelsJson.getString(it) }
                require(labels.size in 2..4 && labels.all { it.isNotBlank() && it.length <= 36 })
                val alt = spec.getString("alt")
                val caption = spec.getString("caption")
                require(alt.length in 30..240 && caption.length in 15..240)
                KnowledgeVisual(kind, alt, caption, labels)
            }
            articles[id] = KnowledgeArticle(id, sections, visual)
        }
        return articles
    }

    companion object {
        fun load(context: Context): Result<OfflineKnowledgeLibrary> = runCatching {
            val json = context.assets.open("knowledge/manifest.json")
                .bufferedReader(Charsets.UTF_8).use { it.readText() }
            val root = JSONObject(json)
            require(root.getInt("schema_version") == 1)
            require(root.getString("publication_state") == "internal_draft")
            val items = root.getJSONArray("entries")
            val ids = mutableSetOf<String>()
            val entries = buildList {
                for (i in 0 until items.length()) {
                    val obj = items.getJSONObject(i)
                    val id = obj.getString("id")
                    require(ids.add(id) && id.isNotBlank())
                    require(obj.getString("review") == "pending")
                    val tags = obj.getJSONArray("tags")
                    add(KnowledgeEntry(id, obj.getString("title"),
                        obj.getString("category"), obj.getString("summary"),
                        List(tags.length()) { j -> tags.getString(j) },
                        obj.getString("risk"), obj.getString("review"),
                        obj.optString("illustration").takeUnless { it.isBlank() || it == "null" },
                        obj.getString("pack")))
                }
            }
            require(entries.isNotEmpty())
            OfflineKnowledgeLibrary(context.applicationContext, entries)
        }
    }
}
