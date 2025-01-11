package dev.datlag.tolgee.format

import kotlinx.datetime.Instant
import kotlin.js.Date

internal actual fun Any.convertToInstant(): Instant = when (this) {
    is Date -> Instant.fromEpochMilliseconds(this.getTime().toLong())
    else -> throw IllegalArgumentException("Can not convert to LocalDateTime: $this")
}