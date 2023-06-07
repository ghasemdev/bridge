@JvmInline
internal value class DataSize(private val size: Long) {
  val inWholeBits: Long
    get() = size

  val inWholeBytes: Long
    get() = size / 8

  val inWholeKilobytes: Long
    get() = size / 8 / KILO

  val inWholeKibibytes: Long
    get() = size / 8 / KIBI

  val inWholeMegabytes: Long
    get() = size / 8 / KILO / KILO

  val inWholeMebibytes: Long
    get() = size / 8 / KIBI / KIBI

  val inWholeGigabytes: Long
    get() = size / 8 / KILO / KILO / KILO

  val inWholeGigibytes: Long
    get() = size / 8 / KIBI / KIBI / KIBI

  val inWholeTerabytes: Long
    get() = size / 8 / KILO / KILO / KILO / KILO

  val inWholeTebibytes: Long
    get() = size / 8 / KIBI / KIBI / KIBI / KIBI
}

internal inline val Number.bits: DataSize
  get() = DataSize(1)

internal inline val Number.bytes: DataSize
  get() = 1.bits * 8

internal inline val Number.kilobytes: DataSize
  get() = 1.bytes * KILO

internal inline val Number.kibibytes: DataSize
  get() = 1.bytes * KIBI

internal inline val Number.megabytes: DataSize
  get() = 1.kilobytes * KILO

internal inline val Number.mebibytes: DataSize
  get() = 1.kibibytes * KIBI

internal inline val Number.gigabytes: DataSize
  get() = 1.megabytes * KILO

internal inline val Number.gigibytes: DataSize
  get() = 1.mebibytes * KIBI

internal inline val Number.terabytes: DataSize
  get() = 1.gigabytes * KILO

internal inline val Number.tebibytes: DataSize
  get() = 1.gigibytes * KIBI

internal operator fun DataSize.times(i: Int): DataSize = DataSize(inWholeBits * i)

internal operator fun DataSize.times(i: Long): DataSize = DataSize(inWholeBits * i)

private const val KILO = 1_000
private const val KIBI = 1_024
