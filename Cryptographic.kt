package com.parsuomash.tick.core.ui.security

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class Cryptographic(private val hashSalt: String, private val secretKey: String) {

  /** Hash Content with given salt*/
  fun hashContent(content: String, salt: String = hashSalt): ByteArray {
    val pbeKeySpec = PBEKeySpec(
      content.toCharArray(),
      salt.toByteArray(),
      PASSWORD_HASH_ITERATIONS,
      PASSWORD_HASH_KEY_SIZE
    )
    val secretKeyFactory =
      SecretKeyFactory.getInstance(PASSWORD_HASH_ALGORITHM_PBKDF2_HMAC_SHA512)
    return secretKeyFactory.generateSecret(pbeKeySpec).encoded
  }

  /** Encrypt text with a key */
  fun encrypt(text: String, key: String = secretKey): String {
    val encrypted = cipher(Cipher.ENCRYPT_MODE, key).doFinal(text.toByteArray(Charsets.UTF_8))
    return Base64.encodeToString(encrypted, 0)
  }

  /** Decrypt text with a key */
  fun decrypt(text: String, key: String = secretKey): String {
    val byteStr = Base64.decode(text.toByteArray(Charsets.UTF_8), 0)
    return String(cipher(Cipher.DECRYPT_MODE, key).doFinal(byteStr))
  }

  @Throws(IllegalArgumentException::class)
  private fun cipher(mode: Int, secretKey: String): Cipher {
    require(secretKey.length == SECRET_KEY_LENGTH) { "SecretKey length is not 32 chars" }

    val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
    val secretKeySpec = SecretKeySpec(secretKey.toByteArray(Charsets.UTF_8), "AES")
    val iv = IvParameterSpec(
      secretKey.substring(0, SECRET_KEY_END_INDEX).toByteArray(Charsets.UTF_8)
    )
    return cipher.apply { init(mode, secretKeySpec, iv) }
  }

  companion object {
    private const val PASSWORD_HASH_ALGORITHM_PBKDF2_HMAC_SHA512 = "PBKDF2WithHmacSHA512"
    private const val PASSWORD_HASH_ITERATIONS = 65_536
    private const val PASSWORD_HASH_KEY_SIZE = 128
    private const val SECRET_KEY_LENGTH = 32
    private const val SECRET_KEY_END_INDEX = 16
  }
}
