package dev.datlag.tolgee.format

import dev.datlag.tolgee.kormatter.Formatter

internal expect fun String.sprintf(vararg args: Any): String
internal expect fun String.kormat(instance: Formatter, vararg args: Any): String