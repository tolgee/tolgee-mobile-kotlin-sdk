package dev.datlag.tolgee.kormatter.utils.internal

import dev.datlag.tolgee.kormatter.utils.ConversionMap
import dev.datlag.tolgee.kormatter.utils.FlagSet

internal fun createFormatStringRegex(flags: FlagSet, conversions: ConversionMap): Regex {
    return Regex(buildString {
        append("""%(?:(\d+)\$)?([""")
        append(Regex.escape(flags.toCharArray().concatToString()))
        append("""]+)?(\d+)?(?:\.(\d+))?(""")

        val sbPrefixes = StringBuilder()
        for (ch in conversions.keys) {
            if (ch.prefix != null) {
                sbPrefixes.append(ch.prefix)
            }
        }

        if (sbPrefixes.isNotEmpty()) {
            append("[")
            append(Regex.escape(sbPrefixes.toString()))
            append("]")
        }

        append(""")?(.)""")
    })
}