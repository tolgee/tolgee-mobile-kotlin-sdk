package io.tolgee.common

import io.tolgee.format.Sprintf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.Instant

/**
 * Converts the given object to an instance of Instant.
 *
 * @return The converted Instant value from the given object.
 * @throws IllegalArgumentException if the conversion cannot be performed.
 */
internal actual fun Any.convertToInstant(): Instant {
    throw IllegalArgumentException("Can not convert to LocalDateTime: $this")
}

/**
 * Formats the string using the specified arguments and returns the formatted result.
 *
 * This function works similarly to the C `sprintf` function by replacing format specifiers
 * within the string with corresponding values from the provided arguments.
 *
 * @param args The values to substitute into the format specifiers within the string.
 * @return A new string where the format specifiers are replaced with corresponding argument values.
 */
internal actual fun String.sprintf(vararg args: Any): String = Sprintf(this, args.toImmutableList()).process().toString()