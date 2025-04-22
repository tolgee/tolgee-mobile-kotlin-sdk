package io.tolgee.common

import dev.datlag.tooling.existsSafely
import dev.datlag.tooling.async.scopeCatching
import java.io.File

internal fun File.fullPathSafely(checkExists: Boolean = true): String? {
    if (checkExists && !this.existsSafely()) {
        return null
    }

    return scopeCatching {
        this.canonicalPath
    }.getOrNull()?.ifBlank { null } ?: scopeCatching {
        this.absolutePath
    }.getOrNull()?.ifBlank { null }
}