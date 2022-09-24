import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import com.parsuomash.tick.core.ui.android.ktx.util.decodeBase64
import com.parsuomash.tick.core.ui.android.ktx.util.encodeBase64
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

//@RequiresApi(Build.VERSION_CODES.M)
object CryptoManager {

  private val keyStore by lazy {
    KeyStore.getInstance(KEY_STORE).apply {
      load(null)
    }
  }

  private val encryptionCipher by lazy {
    Cipher.getInstance(TRANSFORMATION).apply {
      init(Cipher.ENCRYPT_MODE, getKey())
    }
  }

  private fun getDecryptionCipher(iv: ByteArray) = Cipher.getInstance(TRANSFORMATION).apply {
    init(Cipher.DECRYPT_MODE, getKey(), IvParameterSpec(iv))
  }

  private fun getKey(): SecretKey {
    val existingKey = keyStore.getEntry(KEY_STORE_ALIAS, null) as? KeyStore.SecretKeyEntry
    return existingKey?.secretKey ?: createKey()
  }

  private fun createKey(): SecretKey {
    val keyGenerator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      KeyGenerator.getInstance(ALGORITHM).apply {
        init(
          KeyGenParameterSpec.Builder(KEY_STORE_ALIAS, KEY_PURPOSES)
            .setBlockModes(BLOCK_MODE)
            .setEncryptionPaddings(PADDING)
            .setUserAuthenticationRequired(false) // Biometric authentication
            .setRandomizedEncryptionRequired(true)
            .build()
        )
      }
    } else {
      KeyGenerator.getInstance(KEY_STORE)
    }
    return keyGenerator.generateKey()
  }

  @JvmStatic
  fun encrypt(content: String): SecureData {
    val encryptedBytes = encryptionCipher.doFinal(content.encodeToByteArray())
    return SecureData(
      iv = encryptionCipher.iv.encodeBase64(),
      content = encryptedBytes.encodeBase64()
    )
  }

  @JvmStatic
  fun decrypt(encryptedContent: SecureData): String {
    val decryptionCipher = getDecryptionCipher(encryptedContent.iv.decodeBase64())
    val decryptedBytes = decryptionCipher.doFinal(encryptedContent.content.decodeBase64())
    return decryptedBytes.decodeToString()
  }

  @JvmStatic
  @JvmOverloads
  suspend fun encrypt(
    bytes: ByteArray,
    outputStream: OutputStream,
    context: CoroutineContext = Dispatchers.IO
  ): ByteArray = withContext(context) {
    // Encrypt content
    val encryptedBytes = encryptionCipher.doFinal(bytes)

    // Write IV and encrypted bytes to OutputStream
    outputStream.buffered().use { bos ->
      bos.write(encryptionCipher.iv.size)
      bos.write(encryptionCipher.iv)
      bos.write(encryptedBytes)
    }
    encryptedBytes
  }

  @JvmStatic
  @JvmOverloads
  suspend fun decrypt(
    inputStream: InputStream,
    context: CoroutineContext = Dispatchers.IO
  ): ByteArray = withContext(context) {
    // Create a buffer for performance optimization
    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)

    inputStream.buffered().use { bis ->
      // Read IV to initialize decryption cipher
      val ivSize = bis.read()
      val iv = ByteArray(ivSize)
      bis.read(iv)

      val decryptionCipher = getDecryptionCipher(iv)
      var length = bis.read(buffer)

      ByteArrayOutputStream().use { decryptedBytes ->
        while (length >= 0) {
          val trimBuffer = if (length != DEFAULT_BUFFER_SIZE) buffer.trim() else buffer
          val bytes = decryptionCipher.update(trimBuffer, 0, length)
          if (bytes != null) {
            decryptedBytes.write(bytes)
          }
          length = bis.read(buffer)
        }
        decryptedBytes.write(decryptionCipher.doFinal())

        decryptedBytes.toByteArray()
      }
    }
  }

  private fun ByteArray.trim(): ByteArray {
    var i = size - 1
    while (i >= 0 && this[i] == 0.toByte()) {
      --i
    }
    return copyOf(i + 1)
  }
}

private const val KEY_STORE = "AndroidKeyStore"
private const val KEY_STORE_ALIAS = "LatOPetHtheaCiar"
private const val KEY_PURPOSES = KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT

private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES // AES
private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC // CBC
private const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7 // PKCS7Padding
private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
