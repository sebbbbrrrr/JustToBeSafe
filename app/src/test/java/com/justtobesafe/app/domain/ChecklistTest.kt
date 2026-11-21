package com.justtobesafe.app.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class ChecklistTest {
    @Test fun zeroItemsIsZero() = assertEquals(0, completionPercent(0, 0))
    @Test fun exactPercentage() = assertEquals(75, completionPercent(6, 8))
    @Test fun completedIsClamped() = assertEquals(100, completionPercent(80, 8))
    @Test fun onlyKnownIdsCount() = assertEquals(1, completedCount(setOf("water", "invalid")))
    @Test fun idsStayUnique() = assertEquals(SuppliesCatalog.items.size, SuppliesCatalog.validIds.size)
}
