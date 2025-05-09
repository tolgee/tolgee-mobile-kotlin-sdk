package io.tolgee.common

import io.tolgee.model.TolgeeKey
import kotlinx.datetime.Instant
import kotlinx.serialization.json.*

/**
 * Converts a [JsonElement] to its string representation.
 * - If the element is a [JsonPrimitive], its content is returned or an empty string if null.
 * - If the element is a [JsonArray], the string representation of its first element is returned or an empty string if the array is empty.
 * - If the element is a [JsonObject], the string representation of the value of its first key is returned or an empty string if the object is empty.
 *
 * @return The string representation of the [JsonElement], or an empty string if it cannot be resolved.
 */
internal fun JsonElement.stringValue(): String = when (this) {
    is JsonPrimitive -> contentOrNull ?: ""
    is JsonArray -> firstOrNull()?.stringValue() ?: ""
    is JsonObject -> this.values.firstOrNull()?.stringValue() ?: ""
}

internal fun JsonElement.keyData(): TolgeeKey.Data = when (this) {
    is JsonArray -> TolgeeKey.Data.Array(map { it.stringValue() })
    else -> TolgeeKey.Data.Text(stringValue())
}

/**
 * Converts the given object to an instance of Instant.
 *
 * @return The converted Instant value from the given object.
 */
internal expect fun Any.convertToInstant(): Instant

/**
 * Formats the string using the specified arguments, similar to the C `sprintf` function.
 *
 * This function replaces placeholders in the string with corresponding arguments from the provided varargs,
 * applying necessary formatting as specified within the string.
 *
 * @param args The arguments to be formatted and substituted into the string.
 * @return A formatted string with the placeholders replaced by the provided arguments.
 */
internal expect fun String.sprintf(vararg args: Any): String