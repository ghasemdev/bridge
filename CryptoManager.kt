import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import java.io.InputStream
import java.io.OutputStream
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

@RequiresApi(Build.VERSION_CODES.M)
class CryptoManager {

    private val cipher by lazy {
        Cipher.getInstance(TRANSFORMATION)
    }

    private val keyStore by lazy {
        KeyStore.getInstance(KEY_STORE).apply {
            load(null)
        }
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
        cipher.init(Cipher.ENCRYPT_MODE, getKey())
        val encryptedBytes = cipher.doFinal(bytes)

        outputStream.buffered().use {
            it.write(cipher.iv.size)
            it.write(cipher.iv)
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

            cipher.init(Cipher.DECRYPT_MODE, getKey(), IvParameterSpec(iv))
            cipher.doFinal(encryptedBytes)
        }
    }

    companion object {
        private const val KEY_STORE = "AndroidKeyStore"
        private const val KEY_STORE_ALIAS = "LatOPetHtheaCiar"
        private const val KEY_PURPOSES = KeyProperties.PURPOSE_ENCRYPT or
                KeyProperties.PURPOSE_DECRYPT
        private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES // AES
        private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC // CBC
        private const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7 // PKCS7Padding
        private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
    }
}
