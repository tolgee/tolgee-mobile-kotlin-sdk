package dev.datlag.tolgee.kormatter.utils.internal

import dev.datlag.tolgee.kormatter.utils.ConversionKey
import dev.datlag.tolgee.kormatter.utils.FormatString

internal fun parseFormatString(format: String, regex: Regex): Iterator<FormatString> {
    return object : Iterator<FormatString> {
        private var next: MatchResult? = regex.find(format)

        override fun hasNext(): Boolean = next != null

        override fun next(): FormatString {
            val nxt = next ?: throw NoSuchElementException()

            val result = FormatString(
                argumentIndex = nxt.groupValues[1].toIntOrNull(),
                flags = nxt.groupValues[2],
                width = nxt.groupValues[3].toIntOrNull(),
                precision = nxt.groupValues[4].toIntOrNull(),
                conversion = ConversionKey(nxt.groupValues[5].singleOrNull(), nxt.groupValues[6].single()),
                start = nxt.range.first,
                endInclusive = nxt.range.last
            )
            next = nxt.next()
            return result
        }
    }
}