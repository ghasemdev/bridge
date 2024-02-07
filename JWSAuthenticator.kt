@file:Suppress("SwallowedException", "InstanceOfCheckForException")

package ir.partsoftware.digitalsignsdk.data.utils.jws

import com.nimbusds.jose.JOSEException
import com.nimbusds.jose.JOSEObjectType
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSObject
import com.nimbusds.jose.Payload
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.jwk.RSAKey
import ir.partsoftware.digitalsignsdk.constants.LoggerConstants.Message.JWS_CANT_SIGN
import ir.partsoftware.digitalsignsdk.constants.LoggerConstants.Message.JWS_INVALID_ALGORITHM
import ir.partsoftware.digitalsignsdk.constants.LoggerConstants.Tag.JWS_ALGORITHM
import ir.partsoftware.digitalsignsdk.constants.LoggerConstants.Tag.JWS_GENERIC
import ir.partsoftware.digitalsignsdk.constants.LoggerConstants.Tag.JWS_KEYSTORE
import ir.partsoftware.digitalsignsdk.constants.LoggerConstants.Tag.JWS_SIGNER
import ir.partsoftware.digitalsignsdk.core.android.logger.logError
import ir.partsoftware.digitalsignsdk.data.utils.JWSHeader
import ir.partsoftware.digitalsignsdk.exceptions.JWSException
import ir.partsoftware.digitalsignsdk.exceptions.UnusableKeyException
import ir.partsoftware.digitalsignsdk.exceptions.UserNotAuthenticatedException
import ir.partsoftware.digitalsignsdk.presentation.digitalsign.utils.DigitalSignConstants.KEY_JWS
import ir.partsoftware.digitalsignsdk.presentation.digitalsign.utils.DigitalSignConstants.KEY_PAIR_ALIAS
import ir.partsoftware.digitalsignsdk.presentation.digitalsign.utils.KeyPairHelper
import java.security.InvalidAlgorithmParameterException
import java.security.KeyPair
import java.security.KeyStoreException
import java.security.PrivateKey
import java.security.interfaces.RSAPublicKey
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class JWSAuthenticator @Inject constructor(
  private val keyPairHelper: KeyPairHelper
) {
  /**
   *  Get **JWS** string signed by **RSA** key.
   *  @param payload The payload contains sorted query parameters or body of the **HTTP** request.
   *  @param header The JWS header contains extra data like timestamp, action , ... .
   *  @param attachPublicKey When this field enabled rsa public key is attached to jws header.
   */
  fun sign(
    payload: String,
    header: JWSHeader,
    attachPublicKey: Boolean = false
  ): String = sign(alias = KEY_JWS) { publicKey ->
    createJWSObject(
      jwk = publicKey,
      header = mapOf(
        JWS_HEADER_ACTION to header.action,
        JWS_HEADER_METHOD to header.method,
        JWS_HEADER_TIMESTAMP to header.timestamp
      ),
      payload = payload,
      attachPublicKey = attachPublicKey
    )
  }

  /**
   *  Get **JWS** string signed by certificate.
   *  @param payload The payload contains sorted query parameters or body of the **HTTP** request.
   *  @param header The JWS header contains extra data like timestamp, action , ... .
   */
  fun signByCertificate(
    payload: String,
    header: JWSHeader
  ): String = sign(alias = KEY_PAIR_ALIAS) { publicKey ->
    createJWSObject(
      jwk = publicKey,
      header = mapOf(
        JWS_ACTION to header.action,
        JWS_METHOD to header.method,
        JWS_TIMESTAMP to header.timestamp
      ),
      payload = payload,
      attachPublicKey = true
    )
  }

  private inline fun sign(
    alias: String,
    jwsObjectProvider: (publicKey: RSAKey) -> JWSObject
  ): String {
    var jwsObject: JWSObject? = null
    return try {
      val (privateKey, publicKey) = getOrGenerateJWSKeyPair(alias)

      // Create an RSA signer with a reference to a private RSA 2048-bit key in the
      // Android key store
      val signer = RSASSASigner(privateKey)

      // Create the JWS object
      jwsObject = jwsObjectProvider(publicKey)

      // Initiate the signing and watch for a prompt exception
      jwsObject.sign(signer)

      // Output the JWS
      jwsObject.serialize(false)
    } catch (exception: JOSEException) {
      logError(
        throwable = exception,
        message = JWS_CANT_SIGN + " (jws header: ${jwsObject?.header}, " +
          "jws payload: ${jwsObject?.payload}, " + "jws signature: ${jwsObject?.signature})",
        tag = JWS_SIGNER
      )

      if (exception.message == USER_NOT_AUTHENTICATED) {
        throw UserNotAuthenticatedException(
          "کاربر گرامی حذف گواهی با مشکل مواجه شده است، لطفا مجددا تلاش کنید."
        )
      }
      throw JWSException(exception.message.orEmpty())
    } catch (exception: Exception) {
      exceptionLogger(exception)

      if (exception is UnusableKeyException) {
        throw exception
      }
      throw JWSException(exception.message.orEmpty())
    }
  }

  private fun getOrGenerateJWSKeyPair(keyAlias: String): Pair<PrivateKey, RSAKey> {
    val keyPair: KeyPair = keyPairHelper.getOrGenerateKeyPair(
      keyAlias = keyAlias,
      userAuthenticationRequired = false
    )

    val rsaJWK = RSAKey.Builder(keyPair.public as RSAPublicKey)
      .keyIDFromThumbprint()
      .build()

    return Pair(keyPair.private, rsaJWK.toPublicJWK())
  }

  private fun createJWSObject(
    jwk: RSAKey,
    header: Map<String, String>,
    payload: String,
    attachPublicKey: Boolean
  ): JWSObject {
    val jwsHeader = JWSHeader(JWSAlgorithm.RS256) {
      if (attachPublicKey) jwk(jwk)
      keyID(jwk.keyID)
      header.forEach { (key, value) ->
        customParam(key, value)
      }
      base64URLEncodePayload(true)
      type(JOSEObjectType.JWT)
    }
    val detachedPayload = Payload(payload)
    return JWSObject(jwsHeader, detachedPayload)
  }

  private fun exceptionLogger(exception: Throwable) = when (exception) {
    is KeyStoreException -> logError(throwable = exception, tag = JWS_KEYSTORE)
    is InvalidAlgorithmParameterException -> logError(
      throwable = exception,
      message = JWS_INVALID_ALGORITHM,
      tag = JWS_ALGORITHM
    )

    else -> logError(throwable = exception, tag = JWS_GENERIC)
  }

  companion object {
    private const val JWS_TIMESTAMP = "timestamp"
    private const val JWS_ACTION = "action"
    private const val JWS_METHOD = "method"

    private const val JWS_HEADER_TIMESTAMP = "ra-timestamp"
    private const val JWS_HEADER_ACTION = "ra-action"
    private const val JWS_HEADER_METHOD = "ra-method"

    private const val USER_NOT_AUTHENTICATED = "Invalid private RSA key: User not authenticated"
  }
}
