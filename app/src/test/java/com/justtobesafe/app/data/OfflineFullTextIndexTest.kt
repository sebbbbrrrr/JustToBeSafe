package com.justtobesafe.app.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OfflineFullTextIndexTest {
    @Test fun naturalLanguageStopsDoNotHideSearchTerms() {
        assertEquals(listOf("treat", "burn"),
            OfflineFullTextIndex.tokenize("How to treat a burn?"))
        assertEquals(listOf("fix", "cable"),
            OfflineFullTextIndex.tokenize("how to fix a cable"))
    }

    @Test fun stopsOnlyReturnsNoContentTerms() {
        assertTrue(OfflineFullTextIndex.tokenize("How to do a").isEmpty())
    }

    @Test fun unicodeWordsStaySearchable() {
        assertEquals(listOf("café", "water"),
            OfflineFullTextIndex.tokenize("café & water"))
    }

    @Test fun hashShardSelectionIsStable() {
        val first = OfflineFullTextIndex.shardFor("burn")
        assertEquals(first, OfflineFullTextIndex.shardFor("burn"))
        assertTrue(first in "0123456789abcdef")
    }
}
