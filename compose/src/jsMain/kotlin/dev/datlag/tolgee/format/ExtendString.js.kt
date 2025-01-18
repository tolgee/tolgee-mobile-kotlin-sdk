package dev.datlag.tolgee.format

import dev.datlag.tolgee.kormatter.Formatter
import kotlinx.collections.immutable.toImmutableList

internal actual fun String.sprintf(vararg args: Any): String = Sprintf(this, args.toImmutableList()).process().toString()
internal actual fun String.kormat(instance: Formatter, vararg args: Any): String = instance.format(this, *args)