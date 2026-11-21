package com.justtobesafe.app.domain

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PantryItemTest {
    @Test fun validatesItemsAndBarcodes() {
        assertNull(pantryValidationError(PantryItem(name = "Beans", barcode = "5000159484695")))
        assertTrue(pantryValidationError(PantryItem(name = "")) != null)
        assertTrue(pantryValidationError(PantryItem(name = "Rice", quantity = 0)) != null)
        assertTrue(pantryValidationError(PantryItem(name = "Rice", barcode = "abc")) != null)
        assertTrue(pantryValidationError(PantryItem(name = "Rice", expiryDate = "31/12/2028")) != null)
        assertTrue(validFoodBarcode("0123456789012")) // Preserve UPC leading zero.
    }

    @Test fun expiryBoundaryIsInclusive() {
        val today = LocalDate.of(2026, 11, 21)
        assertEquals(ExpiryState.UNKNOWN, expiryState("", today))
        assertEquals(ExpiryState.EXPIRED, expiryState("2026-11-20", today))
        assertEquals(ExpiryState.SOON, expiryState("2026-11-21", today))
        assertEquals(ExpiryState.SOON, expiryState("2026-12-21", today))
        assertEquals(ExpiryState.GOOD, expiryState("2026-12-22", today))
    }

    @Test fun stockCountsPackagesNotCalories() {
        val items = listOf(PantryItem(name = "Pasta", quantity = 4, emergencyReserve = true),
            PantryItem(name = "Oats", quantity = 2))
        assertEquals(6, pantryPackageCount(items))
        assertEquals(4, reservePackageCount(items))
    }
}
