package dev.datlag.tolgee.common

import dev.datlag.tolgee.format.Sprintf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.Instant
import kotlin.js.Date

internal actual fun Any.convertToInstant(): Instant = when (this) {
    is Date -> Instant.fromEpochMilliseconds(this.getTime().toLong())
    else -> throw IllegalArgumentException("Can not convert to LocalDateTime: $this")
}

internal actual fun String.sprintf(vararg args: Any): String = Sprintf(this, args.toImmutableList()).process().toString()