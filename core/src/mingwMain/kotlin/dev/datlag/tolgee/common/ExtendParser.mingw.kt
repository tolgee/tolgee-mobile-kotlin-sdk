package dev.datlag.tolgee.common

import dev.datlag.tolgee.format.Sprintf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.Instant

/**
 * Converts the invoking object to an instance of Instant.
 *
 * @return The corresponding Instant value derived from the invoking object.
 * @throws IllegalArgumentException if the conversion to Instant is not possible.
 */
internal actual fun Any.convertToInstant(): Instant {
    throw IllegalArgumentException("Can not convert to LocalDateTime: $this")
}

/**
 * Formats the string using the specified arguments, allowing for substitution and formatting
 * as defined by the placeholders in the string.
 *
 * @param args The arguments to be formatted and substituted into the string. Each placeholder
 * in the string corresponds to one or more of these arguments.
 * @return A newly formatted string with placeholders replaced by the provided arguments.
 */
internal actual fun String.sprintf(vararg args: Any): String = Sprintf(this, args.toImmutableList()).process().toString()