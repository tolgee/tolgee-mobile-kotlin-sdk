package dev.datlag.tolgee.format

import kotlinx.collections.immutable.toImmutableList

internal actual fun String.sprintf(vararg args: Any): String = Sprintf(this, args.toImmutableList()).process().toString()