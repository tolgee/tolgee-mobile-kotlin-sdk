package dev.datlag.tolgee.format

import dev.datlag.tooling.async.scopeCatching
import kotlinx.collections.immutable.toImmutableList

internal actual fun String.sprintf(vararg args: Any): String = scopeCatching {
    Sprintf(this, args.toImmutableList()).process().toString()
}.getOrNull() ?: this.format(*args)