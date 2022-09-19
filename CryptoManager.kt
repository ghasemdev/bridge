package com.parsuomash.tick.core.ui.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.io.InputStream
import java.io.OutputStream
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class CryptoManager {

  private val keyStore = KeyStore.getInstance(KEY_STORE).apply {
    load(null)
  }

  private val encryptCipher = Cipher.getInstance(TRANSFORMATION).apply {
    init(Cipher.ENCRYPT_MODE, getKey())
  }

  private fun getDecryptCipher(iv: ByteArray) = Cipher.getInstance(TRANSFORMATION).apply {
    init(Cipher.DECRYPT_MODE, getKey(), IvParameterSpec(iv))
  }

  private fun getKey(): SecretKey {
    val existingKey = keyStore.getEntry(KEY_STORE_ALIAS, null) as? KeyStore.SecretKeyEntry
    return existingKey?.secretKey ?: createKey()
  }

  private fun createKey(): SecretKey = KeyGenerator.getInstance(ALGORITHM).apply {
    init(
      KeyGenParameterSpec.Builder(KEY_STORE_ALIAS, KEY_PURPOSES)
        .setBlockModes(BLOCK_MODE)
        .setEncryptionPaddings(PADDING)
        .setUserAuthenticationRequired(false) // Biometric authentication
        .setRandomizedEncryptionRequired(true)
        .build()
    )
  }.generateKey()

  fun encrypt(bytes: ByteArray, outputStream: OutputStream): ByteArray {
    val encryptedBytes = encryptCipher.doFinal(bytes)
    outputStream.buffered().use {
      it.write(encryptCipher.iv.size)
      it.write(encryptCipher.iv)
      it.write(encryptedBytes.size)
      it.write(encryptedBytes)
    }
    return encryptedBytes
  }

  fun decrypt(inputStream: InputStream): ByteArray {
    return inputStream.buffered().use {
      val ivSize = it.read()
      val iv = ByteArray(ivSize)
      it.read(iv)

      val encryptedByteSize = it.read()
      val encryptedBytes = ByteArray(encryptedByteSize)
      it.read(encryptedBytes)

      getDecryptCipher(iv).doFinal(encryptedBytes)
    }
  }

  companion object {
    private const val KEY_STORE = "AndroidKeyStore"
    private const val KEY_STORE_ALIAS = "LatOPetHtheaCiar"
    private const val KEY_PURPOSES = KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT

    private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES // AES
    private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC // CBC
    private const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7 // PKCS7Padding
    private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
  }
}
