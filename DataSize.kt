package ir.partsoftware.digitalsignsdk.core.kotlin.utils

@JvmInline
internal value class DataSize(private val size: Long) {
  inline val inWholeBits: Long
    get() = size

  inline val inWholeBytes: Long
    get() = size / 1.bytes

  inline val inWholeKilobytes: Long
    get() = size / 1.kilobytes

  inline val inWholeMegabytes: Long
    get() = size / 1.megabytes

  inline val inWholeGigabytes: Long
    get() = size / 1.gigabytes

  inline val inWholeTerabytes: Long
    get() = size / 1.terabytes
}

internal inline val Int.bits: DataSize
  get() = DataSize(1)

internal inline val Int.bytes: DataSize
  get() = 1.bits * BIT

internal inline val Int.kilobytes: DataSize
  get() = 1.bytes * KILO

internal inline val Int.megabytes: DataSize
  get() = 1.kilobytes * KILO

internal inline val Int.gigabytes: DataSize
  get() = 1.megabytes * KILO

internal inline val Int.terabytes: DataSize
  get() = 1.gigabytes * KILO

internal inline val Long.bits: DataSize
  get() = DataSize(1)

internal inline val Long.bytes: DataSize
  get() = 1.bits * BIT

internal inline val Long.kilobytes: DataSize
  get() = 1.bytes * KILO

internal inline val Long.megabytes: DataSize
  get() = 1.kilobytes * KILO

internal inline val Long.gigabytes: DataSize
  get() = 1.megabytes * KILO

internal inline val Long.terabytes: DataSize
  get() = 1.gigabytes * KILO

internal operator fun DataSize.times(i: Int): DataSize = DataSize(inWholeBits * i)

internal operator fun DataSize.times(i: Long): DataSize = DataSize(inWholeBits * i)

internal operator fun Long.div(size: DataSize): Long = this / size.inWholeBits

private const val BIT = 8
private const val KILO = 1_024
