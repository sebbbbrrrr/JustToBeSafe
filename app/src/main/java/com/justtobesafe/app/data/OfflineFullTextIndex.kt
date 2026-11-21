package com.justtobesafe.app.data

import android.content.Context
import org.json.JSONObject
import java.security.MessageDigest
import java.util.zip.GZIPInputStream

/**
 * APK-local body search, produced deterministically at build time.
 * The manifest is tiny, and query terms inflate only their assigned index shards.
 * Returns matching IDs; normal readers open article bodies from separate packs.
 */
class OfflineFullTextIndex private constructor(
    private val context: Context,
    private val ids: Set<String>
) {
    private val cache = object : LinkedHashMap<Char, Map<String, Set<String>>>(5, 0.75f, true) {
        override fun removeEldestEntry(
            eldest: MutableMap.MutableEntry<Char, Map<String, Set<String>>>?
        ) = size > 4
    }

    fun search(query: String): Result<Set<String>> = runCatching {
        val words = tokenize(query).distinct()
        if (words.isEmpty()) return@runCatching ids
        var matches: Set<String>? = null
        for (word in words) {
            val digit = shardFor(word)
            val shard = synchronized(cache) {
                cache[digit] ?: loadShard(digit).also { cache[digit] = it }
            }
            val postings = shard[word] ?: emptySet()
            matches = if (matches == null) postings else matches.intersect(postings)
            if (matches.isEmpty()) break
        }
        matches ?: emptySet()
    }

    private fun loadShard(digit: Char): Map<String, Set<String>> {
        val text = context.assets.open("search/terms_${digit}.json.gz")
            .use { stream -> GZIPInputStream(stream).bufferedReader(Charsets.UTF_8).use { it.readText() } }
        val root = JSONObject(text)
        require(root.getInt("version") == 1)
        val terms = root.getJSONObject("terms")
        val result = mutableMapOf<String, Set<String>>()
        val keys = terms.keys()
        while (keys.hasNext()) {
            val word = keys.next()
            val array = terms.getJSONArray(word)
            val postings = (0 until array.length()).map { array.getString(it) }.toSet()
            require(ids.containsAll(postings)) { "Search index contains unknown document IDs" }
            result[word] = postings
        }
        return result
    }

    companion object {
        /** Unicode letter/number terms, same lowercasing rule as the Python compiler. */
        internal fun tokenize(query: String): List<String> =
            Regex("[\\p{L}\\p{N}]+").findAll(query.lowercase(java.util.Locale.ROOT))
                .map { it.value }.toList()

        internal fun shardFor(word: String): Char {
            val bytes = MessageDigest.getInstance("SHA-256")
                .digest(word.toByteArray(Charsets.UTF_8))
            return "0123456789abcdef"[(bytes[0].toInt() and 0xFF) ushr 4]
        }

        fun load(context: Context): Result<OfflineFullTextIndex> = runCatching {
            val raw = context.assets.open("search/manifest.json")
                .bufferedReader(Charsets.UTF_8).use { it.readText() }
            val root = JSONObject(raw)
            require(root.getInt("schema_version") == 1)
            require(root.getInt("shards") == 16)
            require(root.getString("tokenizer") == "unicode-alphanumeric-lowercase-v1")
            require(root.getString("publication_state") == "internal_draft")
            val arr = root.getJSONArray("documents")
            val ids = (0 until arr.length()).map { arr.getString(it) }.toSet()
            require(ids.isNotEmpty() && ids.size == arr.length())
            OfflineFullTextIndex(context.applicationContext, ids)
        }
    }
}
