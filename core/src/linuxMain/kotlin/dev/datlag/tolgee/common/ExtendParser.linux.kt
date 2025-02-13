package dev.datlag.tolgee.common

import dev.datlag.tolgee.format.Sprintf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.Instant

/**
 * Converts the given object to an instance of Instant.
 *
 * @return The converted Instant value from the given object.
 * @throws IllegalArgumentException if the object cannot be converted to an Instant.
 */
internal actual fun Any.convertToInstant(): Instant {
    throw IllegalArgumentException("Can not convert to LocalDateTime: $this")
}

/**
 * Formats the string using the specified arguments, similar to the C `sprintf` function.
 *
 * This function replaces placeholders in the string with the provided arguments, applying formatting rules
 * defined within the string.
 *
 * @param args The arguments to be substituted into the format string.
 * @return The formatted string with placeholders replaced by the provided arguments.
 */
internal actual fun String.sprintf(vararg args: Any): String = Sprintf(this, args.toImmutableList()).process().toString()