package dev.datlag.tolgee.common

import kotlinx.datetime.Instant
import kotlinx.serialization.json.*

internal fun JsonElement.stringValue(): String = when (this) {
    is JsonPrimitive -> contentOrNull ?: ""
    is JsonArray -> firstOrNull()?.stringValue() ?: ""
    is JsonObject -> this.values.firstOrNull()?.stringValue() ?: ""
}

internal expect fun Any.convertToInstant(): Instant

internal expect fun String.sprintf(vararg args: Any): String