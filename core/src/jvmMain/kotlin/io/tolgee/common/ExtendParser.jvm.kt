package io.tolgee.common

import io.tolgee.format.Sprintf
import dev.datlag.tooling.async.scopeCatching
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

/**
 * Converts the given object to an instance of Instant.
 *
 * @return The converted Instant value from the given object.
 * @throws IllegalArgumentException if the object cannot be converted to an Instant.
 */
internal actual fun Any.convertToInstant(): Instant = when (this) {
    is java.time.LocalDateTime -> Instant.fromEpochSeconds(
        this.atZone(ZoneId.systemDefault()).toEpochSecond()
    )
    is ZonedDateTime -> Instant.fromEpochSeconds(this.toEpochSecond())
    is java.time.Instant -> Instant.fromEpochMilliseconds(this.toEpochMilli())
    is Date -> Instant.fromEpochMilliseconds(this.time)
    else -> throw IllegalArgumentException("Can not convert to LocalDateTime: $this")
}

/**
 * Formats the string using the specified arguments, similar to the C `sprintf` function.
 *
 * This method replaces placeholders in the string with the corresponding arguments provided,
 * applying formatting rules defined within the string.
 *
 * @param args The arguments to be formatted and substituted into the string.
 * @return A formatted string with placeholders replaced by the provided arguments,
 *         or the original string if formatting fails.
 */
internal actual fun String.sprintf(vararg args: Any): String = scopeCatching {
    Sprintf(this, args.toImmutableList()).process().toString()
}.getOrNull() ?: this.format(*args)