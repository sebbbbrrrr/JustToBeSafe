package com.justtobesafe.app.data

import android.content.Context
import com.justtobesafe.app.domain.SuppliesCatalog

/** Non-sensitive checklist status only; app-private, offline and never uploaded. */
class InventoryStore(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences("inventory_v1", Context.MODE_PRIVATE)
    fun read(): Set<String> = (prefs.getStringSet("checked", emptySet()) ?: emptySet())
        .filterTo(mutableSetOf()) { it in SuppliesCatalog.validIds }
    fun write(ids: Set<String>): Boolean =
        prefs.edit().putStringSet("checked", ids.intersect(SuppliesCatalog.validIds).toSet()).commit()
}
