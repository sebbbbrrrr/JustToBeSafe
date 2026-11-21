package com.justtobesafe.app.data

import com.justtobesafe.app.domain.validFoodBarcode
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

data class FoodProduct(val name: String, val brand: String, val packSize: String)

/** Sends only the scanned barcode to Open Food Facts; never sends inventory or household details. */
class FoodProductLookup {
    suspend fun find(barcode: String): FoodProduct? = withContext(Dispatchers.IO) {
        require(barcode.isNotBlank() && validFoodBarcode(barcode))
        val endpoint = URL("https://world.openfoodfacts.org/api/v2/product/$barcode.json" +
            "?fields=product_name,product_name_en,brands,quantity")
        val connection = (endpoint.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 6000
            readTimeout = 6000
            setRequestProperty("Accept", "application/json")
            setRequestProperty("User-Agent", "JustToBeSafe/0.1 (https://github.com/sebbbbrrrr/JustToBeSafe)")
        }
        try {
            if (connection.responseCode != HttpURLConnection.HTTP_OK) return@withContext null
            val text = connection.inputStream.bufferedReader().use { it.readText() }
            val root = JSONObject(text)
            if (root.optInt("status") != 1) return@withContext null
            val product = root.optJSONObject("product") ?: return@withContext null
            val name = product.optString("product_name_en").ifBlank {
                product.optString("product_name")
            }.trim()
            if (name.isBlank()) return@withContext null
            FoodProduct(name.take(120), product.optString("brands").take(120),
                product.optString("quantity").take(80))
        } finally {
            connection.disconnect()
        }
    }
}
