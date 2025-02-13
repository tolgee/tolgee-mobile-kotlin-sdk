package dev.datlag.tolgee.common

import android.os.Build
import dev.datlag.tolgee.format.Sprintf
import dev.datlag.tooling.async.scopeCatching
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

internal actual fun Any.convertToInstant(): Instant = when (this) {
    is Date -> Instant.fromEpochMilliseconds(this.time)
    else -> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            when (this) {
                is java.time.LocalDateTime -> Instant.fromEpochSeconds(
                    this.atZone(ZoneId.systemDefault()).toEpochSecond()
                )
                is ZonedDateTime -> Instant.fromEpochSeconds(this.toEpochSecond())
                is java.time.Instant -> Instant.fromEpochMilliseconds(this.toEpochMilli())
                else -> throw IllegalArgumentException("Can not convert to LocalDateTime: $this")
            }
        } else {
            throw IllegalArgumentException("Can not convert to LocalDateTime: $this")
        }
    }
}

internal actual fun String.sprintf(vararg args: Any): String = scopeCatching {
    Sprintf(this, args.toImmutableList()).process().toString()
}.getOrNull() ?: this.format(*args)