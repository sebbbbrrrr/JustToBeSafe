package com.justtobesafe.app.ui.screens

import com.justtobesafe.app.data.KnowledgeArticle
import com.justtobesafe.app.data.KnowledgeSection
import com.justtobesafe.app.data.OfflineGuideArticle
import com.justtobesafe.app.data.OfflineGuideSection
import org.junit.Assert.*
import org.junit.Test

class OfflineSearchPresentationTest {
    private val body = KnowledgeArticle("fixture", listOf(KnowledgeSection("Local text",
        listOf("This locally installed paragraph discusses cupboard rotation and a practical inventory."))))

    @Test fun installedKnowledgeBodyProducesRealContext() {
        val state = OfflineSearchPresentation.indexState("cupboard") {
            Result.success(setOf("fixture"))
        }
        assertEquals(setOf("fixture"),
            (state as OfflineSearchPresentation.IndexState.FullText).ids)
        assertFalse(OfflineSearchPresentation.metadataMatches("cupboard",
            listOf("Household notes", "Simple home organisation")))
        assertTrue(OfflineSearchPresentation.knowledgeSnippet("cupboard", body)!!
            .contains("cupboard rotation"))
    }

    @Test fun urbanInstalledBodyProducesContext() {
        val article = OfflineGuideArticle("urban", "Draft", listOf(
            OfflineGuideSection("one", "Section", listOf("Local shelters are discussed in this draft."))))
        assertTrue(OfflineSearchPresentation.urbanSnippet("shelters", article)!!
            .contains("shelters"))
    }

    @Test fun matchInMiddleAndEnd() {
        val middle = "a".repeat(100) + " cupboard " + "b".repeat(100)
        val end = "a".repeat(250) + " lantern"
        assertTrue(OfflineSearchPresentation.snippet("cupboard", listOf(middle))!!.contains("cupboard"))
        assertTrue(OfflineSearchPresentation.snippet("lantern", listOf(end))!!.contains("lantern"))
    }

    @Test fun finalTokenRemainsWholeNearEndOfLongParagraph() {
        val paragraph = "a".repeat(400) + " lantern"
        val excerpt = OfflineSearchPresentation.snippet("lantern", listOf(paragraph))!!
        assertTrue(excerpt.contains("lantern"))
        assertTrue(excerpt.endsWith("lantern"))
        assertTrue(excerpt.length <= 180)
    }

    @Test fun trailingUnicodeMatchRemainsWhole() {
        val paragraph = "a".repeat(400) + " Café"
        val excerpt = OfflineSearchPresentation.snippet("CAFÉ", listOf(paragraph))!!
        assertTrue(excerpt.endsWith("Café"))
        assertTrue(excerpt.length <= 180)
    }

    @Test fun caseUnicodeAndPrefix() {
        assertEquals("Café supplies",
            OfflineSearchPresentation.snippet("CAFÉ", listOf("Café supplies")))
        assertEquals("Emergency batteries",
            OfflineSearchPresentation.snippet("batter", listOf("Emergency batteries")))
    }

    @Test fun absentTermAndMissingBodyDoNotInventQuotes() {
        assertNull(OfflineSearchPresentation.snippet("lantern", listOf("Cupboard inventory")))
        assertNull(OfflineSearchPresentation.knowledgeSnippet("lantern", null))
        assertNull(OfflineSearchPresentation.urbanSnippet("lantern", null))
    }

    @Test fun whitespaceAndBoundedLongParagraph() {
        val paragraph = "Before\n   the " + "word ".repeat(70) + "cupboard " + "after ".repeat(80)
        val excerpt = OfflineSearchPresentation.snippet("cupboard", listOf(paragraph))!!
        assertTrue(excerpt.length <= 180)
        assertFalse(excerpt.contains("\n"))
        assertTrue(excerpt.contains("cupboard"))
    }

    @Test fun surrogatePairAtExcerptBoundaryDoesNotSplitEmoji() {
        val paragraph = "a".repeat(47) + "😀" + " cupboard " + "b".repeat(250)
        val excerpt = OfflineSearchPresentation.snippet("cupboard", listOf(paragraph))!!
        assertTrue(excerpt.length <= 180)
        assertTrue(excerpt.contains("cupboard"))
        assertFalse(excerpt.any { Character.isSurrogate(it) } &&
            excerpt.indices.any { i ->
                val ch = excerpt[i]
                (Character.isHighSurrogate(ch) &&
                    (i + 1 == excerpt.length || !Character.isLowSurrogate(excerpt[i + 1]))) ||
                    (Character.isLowSurrogate(ch) &&
                        (i == 0 || !Character.isHighSurrogate(excerpt[i - 1])))
            })
    }

    @Test fun failedOrMissingIndexShowsMetadataOnly() {
        assertEquals(OfflineSearchPresentation.IndexState.MetadataOnly,
            OfflineSearchPresentation.indexState("power", null))
        assertEquals(OfflineSearchPresentation.IndexState.MetadataOnly,
            OfflineSearchPresentation.indexState("power") {
                Result.failure(IllegalStateException("Corrupt shard"))
            })
    }

    @Test fun thrownIndexReaderErrorFallsBackWithoutCrashing() {
        assertEquals(OfflineSearchPresentation.IndexState.MetadataOnly,
            OfflineSearchPresentation.indexState("power") {
                throw IllegalArgumentException("Malformed local index shard")
            })
    }

    @Test fun blankQuerySkipsIndexAndShowsCatalogue() {
        assertEquals(OfflineSearchPresentation.IndexState.Catalogue,
            OfflineSearchPresentation.indexState("  ") {
                error("Index should not be queried")
            })
    }

    @Test fun validEmptyIndexIsNoMatchNotCorruption() {
        assertEquals(emptySet<String>(),
            (OfflineSearchPresentation.indexState("unmatched") {
                Result.success(emptySet())
            } as OfflineSearchPresentation.IndexState.FullText).ids)
    }
}
