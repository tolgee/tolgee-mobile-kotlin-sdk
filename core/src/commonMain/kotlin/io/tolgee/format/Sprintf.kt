package io.tolgee.format

import io.tolgee.common.convertToInstant
import kotlinx.collections.immutable.ImmutableList
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.toLocalDateTime

/**
 * Mostly copied from https://github.com/sergeych/mp_stools
 */
internal data class Sprintf(
    val format: String,
    val args: ImmutableList<Any>
): CharSequence {
    private var pos = 0
    private var specStart = -1
    private val result = StringBuilder()
    private var currentIndex = 0

    fun process(): Sprintf {
        while (pos < format.length) {
            val ch = format[pos++]

            if (ch == '%') {
                when {
                    specStart == pos - 1 -> {
                        result.append(ch)
                        specStart = -1
                    }
                    specStart < 0 -> specStart = pos
                    else -> invalidFormat("unexpected %")
                }
            } else {
                if (specStart >= 0) {
                    pos--
                    Specification(this, currentIndex++).scan()
                } else {
                    result.append(ch)
                }
            }
        }
        return this
    }

    internal fun invalidFormat(reason: String): Nothing = throw IllegalArgumentException("Bad format: $reason at offset ${pos - 1} of \"$format\"")

    internal fun nextChar(): Char {
        if (pos >= format.length) invalidFormat("unexpected end of string inside format specification")
        return format[pos++]
    }

    override fun toString(): String = result.toString()

    override val length: Int
        get() = result.length

    override fun get(index: Int): Char = result[index]

    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
        return result.subSequence(startIndex, endIndex)
    }

    internal fun getNumber(index: Int): Number {
        return notNullArg(index)
    }

    internal fun getText(index: Int): String {
        return notNull(index).toString()
    }

    internal fun getCharacter(index: Int): Char {
        return notNullArg(index)
    }

    internal fun getLocalDateTime(index: Int): LocalDateTime {
        return when (val t = notNullArg<Any>(index)) {
            is Instant -> t.toLocalDateTime(TimeZone.currentSystemDefault())
            is LocalDateTime -> t
            is LocalDate -> t.atTime(0, 0)
            else -> t.convertToInstant().toLocalDateTime(TimeZone.currentSystemDefault())
        }
    }

    internal fun specificationDone(text: String) {
        result.append(text)
        specStart = -1
    }

    @Suppress("UNCHECKED_CAST")
    internal fun <T> notNullArg(index: Int) = notNull(index) as T

    internal fun notNull(index: Int) = args[index]

    internal fun pushbackArgumentIndex() {
        currentIndex--
    }

}