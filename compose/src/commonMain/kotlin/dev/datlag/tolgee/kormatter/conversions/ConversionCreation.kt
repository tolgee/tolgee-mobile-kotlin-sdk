package dev.datlag.tolgee.kormatter.conversions

import dev.datlag.tolgee.kormatter.utils.ArgumentTaker
import dev.datlag.tolgee.kormatter.utils.FormatString
import dev.datlag.tolgee.kormatter.utils.PartAction

fun conversion(
    replacement: String,
    widthAction: PartAction = PartAction.STANDARD,
    precisionAction: PartAction = PartAction.STANDARD
): Conversion {
    return ConversionConstant(replacement, widthAction, precisionAction)
}

fun conversion(
    supportedFlags: CharArray = charArrayOf(),
    widthAction: PartAction = PartAction.STANDARD,
    precisionAction: PartAction = PartAction.STANDARD,
    executor: (to: Appendable, str: FormatString, arg: Any?) -> Unit
): Conversion {
    return object : ConversionExecuting(supportedFlags, widthAction, precisionAction) {
        override fun formatTo(to: Appendable, str: FormatString, taker: ArgumentTaker) {
            executor(to, str, taker.take())
        }
    }
}

fun conversion(
    supportedFlags: CharArray = charArrayOf(),
    widthAction: PartAction = PartAction.STANDARD,
    precisionAction: PartAction = PartAction.STANDARD,
    executor: (str: FormatString, arg: Any?) -> String
): Conversion {
    return object : ConversionExecuting(supportedFlags, widthAction, precisionAction) {
        override fun formatTo(to: Appendable, str: FormatString, taker: ArgumentTaker) {
            to.append(executor(str, taker.take()))
        }
    }
}

fun conversionNotNull(
    supportedFlags: CharArray = charArrayOf(),
    widthAction: PartAction = PartAction.STANDARD,
    precisionAction: PartAction = PartAction.STANDARD,
    executor: (to: Appendable, str: FormatString, arg: Any) -> Unit
): Conversion {
    return object : ConversionExecutingNotNull(supportedFlags, widthAction, precisionAction) {
        override fun formatTo(to: Appendable, str: FormatString, arg: Any) {
            executor(to, str, arg)
        }
    }
}

fun conversionNotNull(
    supportedFlags: CharArray = charArrayOf(),
    widthAction: PartAction = PartAction.STANDARD,
    precisionAction: PartAction = PartAction.STANDARD,
    executor: (str: FormatString, arg: Any) -> String
): Conversion {
    return object : ConversionExecutingNotNull(supportedFlags, widthAction, precisionAction) {
        override fun formatTo(to: Appendable, str: FormatString, arg: Any) {
            to.append(executor(str, arg))
        }
    }
}