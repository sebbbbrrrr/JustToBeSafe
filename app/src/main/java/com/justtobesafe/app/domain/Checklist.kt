package com.justtobesafe.app.domain

/** Descriptive inventory reminders, not medical or safety procedures. */
data class SupplyItem(val id: String, val title: String, val subtitle: String)

object SuppliesCatalog {
    val items: List<SupplyItem> = listOf(
        SupplyItem("water", "Drinking water", "Check the amount suitable for your household"),
        SupplyItem("food", "Long-life food", "Review dietary requirements and expiry dates"),
        SupplyItem("torch", "Torch and batteries", "Check it works and keep it accessible"),
        SupplyItem("firstaid", "First aid supplies", "Keep your usual household supplies available"),
        SupplyItem("power", "Power bank", "Keep essential devices charged"),
        SupplyItem("radio", "Battery or wind-up radio", "An alternative way to receive updates"),
        SupplyItem("medicines", "Personal essentials", "Consider items regularly used in your home"),
        SupplyItem("documents", "Important information", "Know where to find your essential records")
    )
    val validIds: Set<String> = items.map { it.id }.toSet()
}

fun completedCount(selected: Set<String>, allowed: Set<String> = SuppliesCatalog.validIds): Int =
    selected.count { it in allowed }

fun completionPercent(completed: Int, total: Int): Int =
    if (total <= 0) 0 else ((completed.coerceIn(0, total) * 100.0) / total).toInt()
