package dev.datlag.tolgee.kormatter

import dev.datlag.tolgee.kormatter.conversions.Conversion
import dev.datlag.tolgee.kormatter.utils.ConversionKey
import dev.datlag.tolgee.kormatter.utils.FormatString

internal abstract class FormatStringException : RuntimeException {
    private val localMessage: String
    private val formatString: FormatString

    internal constructor(formatString: FormatString, localMessage: String) : super() {
        this.formatString = formatString
        this.localMessage = localMessage
    }

    internal constructor(formatString: FormatString, localMessage: String, cause: Throwable?) : super(cause) {
        this.formatString = formatString
        this.localMessage = localMessage
    }

    override val message: String?
        get() = "$formatString: $localMessage"
}

internal open class PartMismatchException(
    formatString: FormatString,
    name: String
) : FormatStringException(formatString, "The format string shouldn't have the $name.")

internal class PrecisionMismatchException(formatString: FormatString) : PartMismatchException(formatString, "precision")

internal class WidthMismatchException(formatString: FormatString) : PartMismatchException(formatString, "width")

internal class UnknownConversionException(formatString: FormatString) : PartMismatchException(formatString, "Cannot find a conversion '${formatString.conversion}'")

internal class ConversionAlreadyExistsException(key: ConversionKey, existing: Conversion) : RuntimeException("The conversion '$key' already exists: $existing!")

internal open class IllegalFormatArgumentException : FormatStringException {
    internal constructor(formatString: FormatString, argument: Any?) : super(formatString, "'$argument' of type '${argument?.let { it::class } ?: "NULL"}' is not a valid argument for this conversion.")
    internal constructor(formatString: FormatString, message: String) : super(formatString, message)
}

internal class IllegalFormatCodePointException(
    formatString: FormatString,
    codePoint: Int
) : IllegalFormatArgumentException(formatString, "Illegal UTF-16 code point: ${codePoint.hashCode().toString(16)}!")

internal class FlagMismatchException(
    formatString: FormatString,
    flag: Char
) : FormatStringException(formatString, "The '$flag' flag isn't an allowed flag for this conversion.")

internal open class NoSuchArgumentException(
    formatString: FormatString,
    message: String,
    cause: Throwable?
) : FormatStringException(formatString, message, cause)