package com.justtobesafe.app.data

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Household details are encrypted at rest using a non-exportable Android Keystore key.
 * Files stay inside app-private storage; backups/transfer are disabled in the manifest.
 * No insecure plaintext fallback: read/write errors must be shown to the user.
 */
data class PlanContact(val name: String, val phone: String)
data class HouseholdPlan(val meetingPoint: String = "", val contacts: List<PlanContact> = emptyList())

class SecurePlanStore(context: Context) {
    private val appContext = context.applicationContext
    private val filename = "household_plan_v1.dat"
    private val alias = "justtobesafe_household_aes_v1"

    private fun key(): SecretKey {
        val store = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        (store.getKey(alias, null) as? SecretKey)?.let { return it }
        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        generator.init(
            KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .setRandomizedEncryptionRequired(true)
                .build()
        )
        return generator.generateKey()
    }

    suspend fun read(): HouseholdPlan = withContext(Dispatchers.IO) {
        val file = appContext.getFileStreamPath(filename)
        if (!file.exists()) return@withContext HouseholdPlan()
        val packed = Base64.decode(android.util.AtomicFile(file).openRead().use { it.readBytes() }, Base64.NO_WRAP)
        require(packed.size > 12) { "Invalid encrypted plan" }
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, key(), GCMParameterSpec(128, packed.copyOfRange(0, 12)))
        val plain = cipher.doFinal(packed.copyOfRange(12, packed.size))
        val json = JSONObject(String(plain, Charsets.UTF_8))
        val array = json.optJSONArray("contacts") ?: JSONArray()
        val contacts = (0 until array.length()).map { index ->
            val entry = array.getJSONObject(index)
            PlanContact(entry.getString("name"), entry.getString("phone"))
        }
        HouseholdPlan(json.optString("meetingPoint"), contacts)
    }

    suspend fun write(plan: HouseholdPlan): Unit = withContext(Dispatchers.IO) {
        val json = JSONObject().apply {
            put("meetingPoint", plan.meetingPoint)
            put("contacts", JSONArray().apply {
                plan.contacts.forEach { contact ->
                    put(JSONObject().put("name", contact.name).put("phone", contact.phone))
                }
            })
        }
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key())
        val packed = cipher.iv + cipher.doFinal(json.toString().toByteArray(Charsets.UTF_8))
        // AtomicFile prevents corruption during interruptions; Android internal storage only.
        val file = android.util.AtomicFile(appContext.getFileStreamPath(filename))
        val stream = file.startWrite()
        try {
            stream.write(Base64.encode(packed, Base64.NO_WRAP))
            stream.flush()
            file.finishWrite(stream)
        } catch (error: Exception) {
            file.failWrite(stream)
            throw error
        }
    }
}
