package dev.datlag.tolgee.kormatter

import dev.datlag.tolgee.kormatter.utils.ArgumentTaker
import dev.datlag.tolgee.kormatter.utils.ConversionMap
import dev.datlag.tolgee.kormatter.utils.FlagSet
import dev.datlag.tolgee.kormatter.utils.PartAction
import dev.datlag.tolgee.kormatter.utils.internal.ArgumentIndexHolder
import dev.datlag.tolgee.kormatter.utils.internal.createFormatStringRegex
import dev.datlag.tolgee.kormatter.utils.internal.lengthSequence
import dev.datlag.tolgee.kormatter.utils.internal.parseFormatString

class Formatter internal constructor(
    internal val conversions: ConversionMap,
    val flags: FlagSet,
) {
    private val regex: Regex by lazy {
        createFormatStringRegex(flags, conversions)
    }

    fun <T : Appendable> formatTo(to: T, format: String, args: Array<out Any?>): T {
        return to.apply {
            val taker = ArgumentTaker(ArgumentIndexHolder(-1, -1), args)
            var textStart = 0

            for (str in parseFormatString(format, regex)) {
                append(format, textStart, str.start)
                textStart = str.endInclusive + 1

                taker.formatString = str
                val conversion = conversions[str.conversion] ?: throw UnknownConversionException(str)
                conversion.check(str)

                val fWidth = conversion.widthAction is PartAction.STANDARD && str.width != null
                val fPrecision = conversion.precisionAction is PartAction.STANDARD && str.precision != null

                if (fWidth || fPrecision) {
                    var formatted: CharSequence = StringBuilder().apply {
                        conversion.formatTo(this, str, taker)
                    }

                    if (fPrecision) {
                        if (formatted.length - str.precision!! > 0) {
                            formatted = formatted.lengthSequence(str.precision)
                        }
                    }

                    if (fWidth) {
                        val len = str.width!! - formatted.length
                        if (len > 0) {
                            val leftJustified = FLAG_LEFT_JUSTIFIED in str.flags

                            if (leftJustified) {
                                append(formatted)
                            }
                            for (n in 1..len) {
                                append(' ')
                            }
                            if (!leftJustified) {
                                append(formatted)
                            }

                            continue
                        }
                    }
                } else {
                    conversion.formatTo(this, str, taker)
                }
            }
            append(format, textStart, format.length)
        }
    }

    fun format(str: String, vararg args: Any): String = formatTo(StringBuilder(), str, args).toString()

    companion object
}