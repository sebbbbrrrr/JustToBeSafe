package com.justtobesafe.app.data

import android.content.Context

/**
 * Local household identity for the prototype. This is NOT a multi-user account,
 * an invitation system, or a shared/synchronised household.
 * Store only a non-sensitive display name until secure accounts are implemented.
 */
class LocalHouseholdStore(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences(
        "local_household_v1", Context.MODE_PRIVATE
    )

    fun readName(): String = preferences.getString(KEY_NAME, DEFAULT_NAME)
        ?.takeIf { it.isNotBlank() } ?: DEFAULT_NAME

    fun saveName(name: String): Boolean {
        val cleaned = name.trim()
        if (cleaned.isEmpty() || cleaned.length > 60 || cleaned.any { Character.isISOControl(it) }) {
            return false
        }
        return preferences.edit().putString(KEY_NAME, cleaned).commit()
    }

    companion object {
        const val DEFAULT_NAME = "My household"
        private const val KEY_NAME = "household_name"
    }
}
