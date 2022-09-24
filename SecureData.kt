package com.parsuomash.tick.core.ui.security

class SecureData(
  val content: String,
  val iv: String
) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as SecureData

    if (content != other.content) return false
    if (iv != other.iv) return false

    return true
  }

  override fun hashCode(): Int {
    var result = content.hashCode()
    result = 31 * result + iv.hashCode()
    return result
  }

  override fun toString(): String = "$iv $content"

  companion object {
    fun decode(value: String): SecureData {
      val (iv, content) = value.split(" ")
      return SecureData(iv = iv, content = content)
    }
  }
}
