package dev.datlag.tolgee.common

import dev.datlag.tolgee.format.Sprintf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.Instant
import kotlin.js.Date

/**
 * Converts an object to an instance of Instant.
 *
 * @return The converted Instant instance.
 * @throws IllegalArgumentException if the object cannot be converted to Instant.
 */
internal actual fun Any.convertToInstant(): Instant = when (this) {
    is Date -> Instant.fromEpochMilliseconds(this.getTime().toLong())
    else -> throw IllegalArgumentException("Can not convert to LocalDateTime: $this")
}

/**
 * Formats the string using the specified arguments, similar to the functionality of the C `sprintf` function.
 *
 * This method processes the format string by replacing placeholders with the corresponding arguments provided
 * in the `args` parameter. The formatting rules follow conventions for specifying placeholders and their
 * replacements.
 *
 * @param args The arguments to format and substitute into the format string.
 * @return A string with the format placeholders replaced by the formatted arguments.
 */
internal actual fun String.sprintf(vararg args: Any): String = Sprintf(this, args.toImmutableList()).process().toString()