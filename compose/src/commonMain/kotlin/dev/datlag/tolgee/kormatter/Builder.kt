package dev.datlag.tolgee.kormatter

import dev.datlag.tolgee.kormatter.conversions.Conversion
import dev.datlag.tolgee.kormatter.conversions.UppercaseConversion
import dev.datlag.tolgee.kormatter.utils.ConversionKey
import dev.datlag.tolgee.kormatter.utils.MutableConversionMap
import dev.datlag.tolgee.kormatter.utils.MutableFlagSet

@DslMarker
annotation class FormatterDsl

@FormatterDsl
class FormatterBuilder {
    private val conversions: MutableConversionMap = hashMapOf()
    private val flags: MutableFlagSet = hashSetOf(FLAG_REUSE_ARGUMENT, FLAG_LEFT_JUSTIFIED)

    fun takeFrom(formatter: Formatter) {
        flags.takeFrom(formatter)
        conversions.takeFrom(formatter)
    }

    fun conversions(init: ConversionScope.() -> Unit) {
        val scope = ConversionScope(conversions)
        scope.init()
    }

    fun flags(init: FlagsScope.() -> Unit) {
        val scope = FlagsScope(flags)
        scope.init()
    }

    fun createFormatter() = Formatter(conversions, flags)

    @FormatterDsl
    class ConversionScope internal constructor(
        internal val conversions: MutableConversionMap,
    ) {
        fun takeFrom(formatter: Formatter) {
            conversions.takeFrom(formatter)
        }

        operator fun Char.invoke(conversion: Conversion, uppercaseVariant: Boolean = true) {
            put(ConversionKey(this), conversion, uppercaseVariant)
        }

        operator fun String.invoke(conversion: Conversion, uppercaseVariant: Boolean = true) {
            val key = when (this.length) {
                1 -> ConversionKey(this[0])
                2 -> ConversionKey(this[0], this[1])
                else -> throw IllegalArgumentException("$this: The conversion key string should be 1 or 2 characters long.")
            }
            put(key, conversion, uppercaseVariant)
        }

        private fun put(key: ConversionKey, conversion: Conversion, uppercaseVariant: Boolean) {
            val prev = conversions[key]
            if (prev != null) {
                throw ConversionAlreadyExistsException(key, prev)
            }
            conversions[key] = conversion
            if (uppercaseVariant) {
                put(key.withConversion(key.conversion.uppercaseChar()), UppercaseConversion(conversion), false)
            }
        }
    }

    @FormatterDsl
    class FlagsScope internal constructor(
        val flags: MutableFlagSet,
    ) {
        fun takeFrom(formatter: Formatter) {
            flags.takeFrom(formatter)
        }

        operator fun Char.unaryPlus() {
            flags.add(this)
        }
    }
}

fun buildFormatter(init: FormatterBuilder.() -> Unit): Formatter {
    val builder = FormatterBuilder()
    builder.init()
    return builder.createFormatter()
}

private fun MutableConversionMap.takeFrom(formatter: Formatter) = putAll(formatter.conversions)
private fun MutableFlagSet.takeFrom(formatter: Formatter) = addAll(formatter.flags)