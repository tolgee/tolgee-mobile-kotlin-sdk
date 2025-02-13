package dev.datlag.tolgee.common

import dev.datlag.tolgee.format.Sprintf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.Instant
import kotlinx.datetime.toKotlinInstant
import platform.Foundation.NSDate

internal actual fun Any.convertToInstant(): Instant = when (this) {
    is NSDate -> toKotlinInstant()
    else -> throw IllegalArgumentException("Can not convert to LocalDateTime: $this")
}

internal actual fun String.sprintf(vararg args: Any): String = Sprintf(this, args.toImmutableList()).process().toString()