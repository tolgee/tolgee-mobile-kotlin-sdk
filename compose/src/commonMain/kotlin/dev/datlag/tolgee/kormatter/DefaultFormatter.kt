package dev.datlag.tolgee.kormatter

import dev.datlag.tolgee.kormatter.conversions.conversion
import dev.datlag.tolgee.kormatter.conversions.conversionNotNull
import dev.datlag.tolgee.kormatter.utils.PartAction
import dev.datlag.tolgee.kormatter.utils.internal.lineSeparator

internal val DefaultFormatter = buildFormatter {
    conversions {
        '%'(
            conversion(
                    "%",
                    precisionAction = PartAction.FORBIDDEN
            ),
            false
        )
        'n'(
            conversion(
                lineSeparator,
                PartAction.FORBIDDEN,
                PartAction.FORBIDDEN
            ),
            false
        )
        'b'(
            conversion { _, arg ->
                when (arg) {
                    null -> "false"
                    is Boolean -> arg.toString()
                    else -> "true"
                }
            }
        )
        's'(
            conversion(
                supportedFlags = charArrayOf('#')
            ) { to, str, arg ->
                if (arg is Formatable) {
                    arg.formatTo(to, str)
                } else {
                    to.append(arg.toString())
                }
            }
        )
        'h'(
            conversionNotNull { _, arg ->
                arg.hashCode().toString(16)
            }
        )
        'c'(
            conversionNotNull(precisionAction = PartAction.FORBIDDEN) { to, str, arg ->
                when (arg) {
                    is Char -> to.append(arg)
                    is Int, is Short, is Byte -> {
                        val i = (arg as Number).toInt()

                        if (i ushr 16 < (0X10FFFF + 1).ushr(16)) {
                            if (i ushr 16 == 0) {
                                to.append(i.toChar())
                            } else {
                                to.append(((i ushr 10) + (Char.MIN_HIGH_SURROGATE.code - (0x010000 ushr 10))).toChar())
                                to.append(((i and 0x3ff) + Char.MIN_LOW_SURROGATE.code).toChar())
                            }
                        } else {
                            throw IllegalFormatCodePointException(str, i)
                        }
                    }
                    else -> throw IllegalFormatArgumentException(str, arg)
                }
            }
        )
    }
    flags {
        +FLAG_ALTERNATE
        +FLAG_INCLUDE_SIGN
        +FLAG_POSITIVE_LEADING_SPACE
        +FLAG_ZERO_PADDED
        +FLAG_LOCALE_SPECIFIC_GROUPING_SEPARATORS
        +FLAG_NEGATIVE_PARENTHESES
    }
}