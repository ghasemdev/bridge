package com.parsuomash.tick.core.ui.security

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val jsonCrypto by lazy { Json { encodeDefaults = true } }

/**
 * serializes data type into string
 * performs encryption
 * stores encrypted data in DataStore
 *
 * Example:
 * ```kotlin
 * dataStore.secureEdit(value) { prefs, encryptedValue ->
 *   prefs[USER_INFO] = encryptedValue
 * }
 * ```
 * @since 2.0.0
 */
suspend inline fun <reified T> DataStore<Preferences>.secureEdit(
  value: T,
  crossinline editStore: (MutablePreferences, String) -> Unit
) {
  edit {
    val encryptedValue = CryptoManager.encrypt(Json.encodeToString(value))
    editStore.invoke(it, encryptedValue.toString())
  }
}

/**
 * fetches encrypted data from DataStore
 * performs decryption
 * deserializes data into respective data type
 *
 * Example:
 * ```kotlin
 * dataStore.data
 *   .secureMap<LoginResModel> { prefs ->
 *     prefs[USER_INFO].orEmpty()
 *   }
 * ```
 * @since 2.0.0
 */
inline fun <reified T> Flow<Preferences>.secureMap(crossinline fetchValue: (value: Preferences) -> String): Flow<T> {
  return map {
    val decryptedValue = CryptoManager.decrypt(SecureData.decode(fetchValue(it)))
    jsonCrypto.decodeFromString(decryptedValue)
  }
}
