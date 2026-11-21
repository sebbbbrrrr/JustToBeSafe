package com.justtobesafe.app.domain

import java.time.LocalDate
import java.time.format.DateTimeParseException

/** A package/stock line, not an assertion about dietary or emergency sufficiency. */
data class PantryItem(
    val id: Long = 0L,
    val barcode: String = "",
    val name: String = "",
    val brand: String = "",
    val packSize: String = "",
    val quantity: Int = 1,
    val expiryDate: String = "",
    val location: String = "Pantry",
    val emergencyReserve: Boolean = false
)

enum class ExpiryState { UNKNOWN, GOOD, SOON, EXPIRED }

fun validFoodBarcode(value: String): Boolean = value.isEmpty() ||
    (value.length in 8..14 && value.all { it in '0'..'9' })

fun pantryValidationError(item: PantryItem): String? = when {
    item.name.isBlank() -> "Enter a product name"
    item.name.length > 120 -> "Keep the product name under 120 characters"
    item.brand.length > 120 || item.packSize.length > 80 || item.location.length > 80 ->
        "Shorten the brand, pack size or storage location"
    item.quantity !in 1..9999 -> "Quantity must be between 1 and 9,999"
    !validFoodBarcode(item.barcode) -> "Barcode must be 8–14 digits"
    item.expiryDate.isNotBlank() && try { LocalDate.parse(item.expiryDate); false }
        catch (_: DateTimeParseException) { true } -> "Use expiry date YYYY-MM-DD"
    else -> null
}

fun expiryState(date: String, today: LocalDate = LocalDate.now()): ExpiryState {
    if (date.isBlank()) return ExpiryState.UNKNOWN
    val expiry = try { LocalDate.parse(date) } catch (_: DateTimeParseException) {
        return ExpiryState.UNKNOWN
    }
    return when {
        expiry.isBefore(today) -> ExpiryState.EXPIRED
        !expiry.isAfter(today.plusDays(30)) -> ExpiryState.SOON
        else -> ExpiryState.GOOD
    }
}

fun pantryPackageCount(items: List<PantryItem>): Int = items.sumOf { it.quantity }
fun reservePackageCount(items: List<PantryItem>): Int =
    items.filter { it.emergencyReserve }.sumOf { it.quantity }
