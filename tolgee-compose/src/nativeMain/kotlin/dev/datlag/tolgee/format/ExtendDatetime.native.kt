package dev.datlag.tolgee.format

import kotlinx.datetime.Instant

internal actual fun Any.convertToInstant(): Instant {
    throw IllegalArgumentException("Can not convert to LocalDateTime: $this")
}