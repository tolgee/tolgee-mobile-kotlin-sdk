package io.tolgee.common

import android.os.Build
import io.tolgee.format.Sprintf
import dev.datlag.tooling.async.scopeCatching
import kotlinx.collections.immutable.toImmutableList
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Converts the current object to an instance of [Instant]. This function handles various
 * object types, such as [Date], [java.time.LocalDateTime], [ZonedDateTime], and [java.time.Instant],
 * and converts them to [Instant] where applicable.
 *
 * @return The corresponding [Instant] for the current object.
 * @throws IllegalArgumentException If the object type is not supported or cannot be converted.
 */
@OptIn(ExperimentalTime::class)
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

/**
 * Formats the current string using the specified arguments, following a pattern similar to the C `sprintf` function.
 * Allows for the substitution and formatting of placeholders within the string.
 *
 * @param args The arguments used to replace placeholders in the string format.
 * @return A formatted string with placeholders replaced by the respective values from the provided arguments.
 */
internal actual fun String.sprintf(vararg args: Any): String = scopeCatching {
    Sprintf(this, args.toImmutableList()).process().toString()
}.getOrNull() ?: this.format(*args)