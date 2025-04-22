package io.tolgee.common

import io.tolgee.format.Sprintf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.Instant
import kotlinx.datetime.toKotlinInstant
import platform.Foundation.NSDate

/**
 * Converts the given object to an instance of Instant.
 *
 * @return The converted Instant value if the object is of a supported type, otherwise throws an IllegalArgumentException.
 */
internal actual fun Any.convertToInstant(): Instant = when (this) {
    is NSDate -> toKotlinInstant()
    else -> throw IllegalArgumentException("Can not convert to LocalDateTime: $this")
}

/**
 * Formats the string using the specified arguments, similar to the C `sprintf` function.
 *
 * This function replaces placeholders in the string with corresponding arguments from the provided varargs,
 * applying necessary formatting as specified within the string.
 *
 * @param args The arguments to be formatted and substituted into the string.
 * @return A formatted string with the placeholders replaced by the provided arguments.
 */
internal actual fun String.sprintf(vararg args: Any): String = Sprintf(this, args.toImmutableList()).process().toString()