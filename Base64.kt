package com.parsuomash.tick.core.ui.android.ktx.util

import android.util.Base64OutputStream
import java.io.InputStream
import java.io.OutputStream

/**
 * Utilities for encoding and decoding the Base64 representation of
 * binary data. See RFCs [2045](http://www.ietf.org/rfc/rfc2045.txt)
 * and [3548](http://www.ietf.org/rfc/rfc3548.txt).
 */
object Base64 {

  /**
   * Encode a [text] into base64 format with default [flags].
   *
   * Example:
   * ```Kotlin
   * Base64.encode("Hello World!") // SGVsbG8gV29ybGQh
   * ```
   * @since 2.0.0
   * @param text content text.
   * @param flags controls certain features of the encoded output.
   * Passing [Flags.Default] results in output that adheres to RFC 2045.
   */
  @JvmStatic
  @JvmOverloads
  fun encode(text: String, flags: Flags = Flags.Default): String =
    android.util.Base64.encodeToString(text.encodeToByteArray(), flags.code)

  /**
   * write a [text] to [outputStream] with default [flags] and base64 format.
   *
   * Example:
   * ```Kotlin
   * Base64.encode("Hello World!", file.outputStream())
   * ```
   * @since 2.0.0
   * @param text content text.
   * @param outputStream stream to save encoded content.
   * @param flags controls certain features of the encoded output.
   * Passing [Flags.Default] results in output that adheres to RFC 2045.
   */
  @JvmStatic
  @JvmOverloads
  fun encode(text: String, outputStream: OutputStream, flags: Flags = Flags.Default) {
    outputStream.buffered().use {
      it.write(android.util.Base64.encode(text.encodeToByteArray(), flags.code))
    }
  }

  /**
   * Decode a [text] from base64 format with default [flags].
   *
   * Example:
   * ```Kotlin
   * Base64.decode("SGVsbG8gV29ybGQh") // Hello World!
   * ```
   * @since 2.0.0
   * @param text content text.
   * @param flags controls certain features of the encoded output.
   * Passing [Flags.Default] results in output that adheres to RFC 2045.
   */
  @JvmStatic
  @JvmOverloads
  fun decode(text: String, flags: Flags = Flags.Default): String =
    android.util.Base64.decode(text, flags.code).decodeToString()

  /**
   * Read a text from [inputStream] with default [flags] and base64 format.
   *
   * Example:
   * ```Kotlin
   * Base64.decode(file.inputStream()) // Hello World!
   * ```
   * @since 2.0.0
   * @param inputStream stream that should read to decode content.
   * @param flags controls certain features of the encoded output.
   * Passing [Flags.Default] results in output that adheres to RFC 2045.
   */
  @JvmStatic
  @JvmOverloads
  fun decode(inputStream: InputStream, flags: Flags = Flags.Default): String {
    return inputStream.buffered().use {
      android.util.Base64.decode(it.readBytes(), flags.code).decodeToString()
    }
  }

  /** [Base64] values for encoder/decoder flags. */
  enum class Flags(val code: Int) {
    /** Default values for encoder/decoder flags. */
    Default(android.util.Base64.DEFAULT),

    /**
     * Encoder/decoder flag bit to indicate using the "URL and
     * filename safe" variant of Base64 (see RFC 3548 section 4) where
     * `-` and `_` are used in place of `+` and `/`.
     */
    UrlSafe(android.util.Base64.URL_SAFE),

    /**
     * Encoder flag bit to indicate lines should be terminated with a
     * CRLF pair instead of just an LF.  Has no effect if `NO_WRAP` is specified as well.
     */
    Crlf(android.util.Base64.CRLF),

    /**
     * Flag to pass to [Base64OutputStream] to indicate that it
     * should not close the output stream it is wrapping when it
     * itself is closed.
     */
    NoClose(android.util.Base64.NO_CLOSE),

    /**
     * Encoder flag bit to omit the padding '=' characters at the end
     * of the output (if any).
     */
    NoPadding(android.util.Base64.NO_PADDING),

    /**
     * Encoder flag bit to omit all line terminators (i.e., the output
     * will be on one long line).
     */
    NoWrap(android.util.Base64.NO_WRAP),
  }
}

/**
 * Encode a string into [Base64] format.
 *
 * Example:
 * ```Kotlin
 * "Hello World!".base64Encode // SGVsbG8gV29ybGQh
 * ```
 * @since 2.0.0
 * @see encodeToBase64
 */
@get:JvmSynthetic
inline val String.base64Encode: String
  get() = encodeToBase64()

/**
 * Decode a string from [Base64] format.
 *
 * Example:
 * ```Kotlin
 * "SGVsbG8gV29ybGQh".base64Decode // Hello World!
 * ```
 * @since 2.0.0
 * @see decodeToBase64
 */
@get:JvmSynthetic
inline val String.base64Decode: String
  get() = decodeToBase64()

/**
 * Encode a string into [Base64] format.
 *
 * Example:
 * ```Kotlin
 * "Hello World!".encodeToBase64() // SGVsbG8gV29ybGQh
 * ```
 * @since 2.0.0
 * @see base64Encode
 */
@JvmSynthetic
fun String.encodeToBase64(flags: Base64.Flags = Base64.Flags.Default): String =
  Base64.encode(this, flags)

/**
 * Decode a string from [Base64] format.
 *
 * Example:
 * ```Kotlin
 * "SGVsbG8gV29ybGQh".decodeToBase64() // Hello World!
 * ```
 * @since 2.0.0
 * @see base64Decode
 */
@JvmSynthetic
fun String.decodeToBase64(flags: Base64.Flags = Base64.Flags.Default): String =
  Base64.decode(this, flags)

/**
 * Encode a [ByteArray] into [Base64] format.
 * @since 2.0.0
 */
@JvmSynthetic
fun ByteArray.encodeBase64(flags: Base64.Flags = Base64.Flags.Default): String =
  android.util.Base64.encodeToString(this, flags.code)

/**
 * Decode a [String] from [Base64] format.
 * @since 2.0.0
 */
@JvmSynthetic
fun String.decodeBase64(flags: Base64.Flags = Base64.Flags.Default): ByteArray =
  android.util.Base64.decode(this, flags.code)
