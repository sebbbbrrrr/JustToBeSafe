package com.justtobesafe.app.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class UrbanGuideCatalogueTest {
    private val power = UrbanGuideTopic(
        id = "power-cuts", title = "Local power cuts", priority = 1,
        category = "electrical", geographicScope = "all-uk",
        tags = listOf("electricity", "equipment"), sourceUrls = listOf("https://example.org/power"),
        sections = listOf(UrbanGuideSection("before", "Prepare beforehand", "draft")),
        safetyReview = "pending", offlineBodyAvailable = false
    )
    private val flood = UrbanGuideTopic(
        id = "urban-flooding", title = "Urban flooding", priority = 2,
        category = "flood", geographicScope = "region-specific",
        tags = listOf("drainage"), sourceUrls = listOf("https://example.org/flood"),
        sections = emptyList(), safetyReview = "pending", offlineBodyAvailable = false
    )
    private val catalogue = UrbanGuideCatalogue(listOf(power, flood), "test")

    @Test fun emptySearchReturnsAllTopics() =
        assertEquals(listOf(power, flood), catalogue.search("  "))

    @Test fun searchMatchesCaseInsensitiveTitleAndTags() {
        assertEquals(listOf(power), catalogue.search("POWER"))
        assertEquals(listOf(flood), catalogue.search("drainage"))
    }

    @Test fun searchDoesNotInventResults() =
        assertEquals(emptyList<UrbanGuideTopic>(), catalogue.search("earthquake"))

    @Test fun lookupUsesStableIds() {
        assertEquals(power, catalogue.find("power-cuts"))
        assertNull(catalogue.find("does-not-exist"))
    }

    @Test fun draftSectionsHaveNoSafetyBody() {
        assertEquals("draft", power.sections.single().status)
        assertEquals(false, power.offlineBodyAvailable)
    }
}
