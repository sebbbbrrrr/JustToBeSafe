package com.justtobesafe.app.ui.screens

import com.justtobesafe.app.data.KnowledgeArticle
import com.justtobesafe.app.data.OfflineGuideArticle
import java.util.Locale

/** Pure presentation logic: no I/O, no search-index format assumptions. */
internal object OfflineSearchPresentation {
    sealed class IndexState {
        data object Catalogue : IndexState()
        data object MetadataOnly : IndexState()
        data class FullText(val ids: Set<String>) : IndexState()
    }

    fun indexState(query: String, search: (() -> Result<Set<String>>)?): IndexState =
        when {
            query.isBlank() -> IndexState.Catalogue
            search == null -> IndexState.MetadataOnly
            else -> search().fold(
                onSuccess = { IndexState.FullText(it) },
                onFailure = { IndexState.MetadataOnly }
            )
        }

    fun metadataMatches(query: String, fields: List<String>): Boolean {
        val term = query.trim()
        return term.isNotEmpty() && fields.any { it.contains(term, ignoreCase = true) }
    }

    fun knowledgeSnippet(query: String, article: KnowledgeArticle?): String? =
        snippet(query, article?.sections?.flatMap { it.paragraphs }.orEmpty())

    fun urbanSnippet(query: String, article: OfflineGuideArticle?): String? =
        snippet(query, article?.sections?.flatMap { it.paragraphs }.orEmpty())

    /** Context is taken exclusively from an installed paragraph, never generated prose. */
    fun snippet(query: String, paragraphs: List<String>, limit: Int = 180): String? {
        require(limit >= 16)
        val terms = Regex("[\\p{L}\\p{N}]+").findAll(query.lowercase(Locale.ROOT))
            .map { it.value }.filter { it.isNotBlank() }.toList()
        if (terms.isEmpty()) return null
        for (paragraph in paragraphs) {
            val clean = paragraph.replace(Regex("\\s+"), " ").trim()
            val lower = clean.lowercase(Locale.ROOT)
            val hit = Regex("[\\p{L}\\p{N}]+").findAll(lower)
                .firstOrNull { word -> terms.any { word.value.startsWith(it) } } ?: continue
            if (clean.length <= limit) return clean
            val start = (hit.range.first - 48).coerceAtLeast(0)
                .coerceAtMost(clean.length - limit)
            val end = (start + limit - 2).coerceAtMost(clean.length)
            val safeStart = if (start > 0 && Character.isLowSurrogate(clean[start])) start + 1 else start
            val safeEnd = if (end < clean.length && Character.isHighSurrogate(clean[end - 1])) end - 1 else end
            val excerpt = clean.substring(safeStart, safeEnd).trim()
            return (if (safeStart > 0) "…" else "") + excerpt +
                (if (safeEnd < clean.length) "…" else "")
        }
        return null
    }
}
