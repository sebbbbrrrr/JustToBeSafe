package com.justtobesafe.app.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.justtobesafe.app.domain.PantryItem
import com.justtobesafe.app.domain.pantryValidationError

/** App-private offline inventory. Distinct from the original supplies checklist. */
class PantryStore(context: Context) : SQLiteOpenHelper(context.applicationContext, "pantry_v1.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE pantry_items (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                barcode TEXT NOT NULL DEFAULT '',
                name TEXT NOT NULL,
                brand TEXT NOT NULL DEFAULT '',
                pack_size TEXT NOT NULL DEFAULT '',
                quantity INTEGER NOT NULL CHECK (quantity BETWEEN 1 AND 9999),
                expiry_date TEXT NOT NULL DEFAULT '',
                location TEXT NOT NULL DEFAULT 'Pantry',
                emergency_reserve INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Future migrations must preserve inventory; never drop household data here.
        throw IllegalStateException("No pantry schema migration from version $oldVersion")
    }

    fun readAll(): List<PantryItem> = readableDatabase.query(
        "pantry_items", null, null, null, null, null, "name COLLATE NOCASE ASC, id ASC"
    ).use { cursor ->
        val rows = mutableListOf<PantryItem>()
        val id = cursor.getColumnIndexOrThrow("id")
        val barcode = cursor.getColumnIndexOrThrow("barcode")
        val name = cursor.getColumnIndexOrThrow("name")
        val brand = cursor.getColumnIndexOrThrow("brands")
        val size = cursor.getColumnIndexOrThrow("pack_size")
        val quantity = cursor.getColumnIndexOrThrow("quantity")
        val expiry = cursor.getColumnIndexOrThrow("expiry_date")
        val location = cursor.getColumnIndexOrThrow("location")
        val reserve = cursor.getColumnIndexOrThrow("emergency_reserve")
        while (cursor.moveToNext()) rows += PantryItem(
            id = cursor.getLong(id), barcode = cursor.getString(barcode),
            name = cursor.getString(name), brand = cursor.getString(brand),
            packSize = cursor.getString(size), quantity = cursor.getInt(quantity),
            expiryDate = cursor.getString(expiry), location = cursor.getString(location),
            emergencyReserve = cursor.getInt(reserve) != 0
        )
        rows
    }

    /** Returns the persisted row ID; failures leave the UI inventory unchanged. */
    fun save(item: PantryItem): Long {
        require(pantryValidationError(item) == null) { "Invalid pantry item" }
        val values = ContentValues().apply {
            put("barcode", item.barcode.trim())
            put("name", item.name.trim())
            put("brand", item.brand.trim())
            put("pack_size", item.packSize.trim())
            put("quantity", item.quantity)
            put("expiry_date", item.expiryDate.trim())
            put("location", item.location.trim().ifBlank { "Pantry" })
            put("emergency_reserve", if (item.emergencyReserve) 1 else 0)
        }
        return if (item.id == 0L) {
            writableDatabase.insertOrThrow("pantry_items", null, values)
        } else {
            check(writableDatabase.update("pantry_items", values, "id = ?", arrayOf(item.id.toString())) == 1) {
                "Pantry item no longer exists"
            }
            item.id
        }
    }

    fun delete(id: Long): Boolean = writableDatabase.delete(
        "pantry_items", "id = ?", arrayOf(id.toString())
    ) == 1
}
