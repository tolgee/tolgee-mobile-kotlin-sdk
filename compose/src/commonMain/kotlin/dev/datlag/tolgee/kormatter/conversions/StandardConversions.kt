package dev.datlag.tolgee.kormatter.conversions

import dev.datlag.tolgee.kormatter.utils.ArgumentTaker
import dev.datlag.tolgee.kormatter.utils.FormatString
import dev.datlag.tolgee.kormatter.utils.PartAction

internal class ConversionConstant(
    private val replacement: String,
    override val widthAction: PartAction,
    override val precisionAction: PartAction
) : ConversionChecking {
    override val canTakeArguments: Boolean
        get() = false

    override fun formatTo(to: Appendable, str: FormatString, taker: ArgumentTaker) {
        to.append(replacement)
    }
}

internal abstract class ConversionExecuting(
    private val supportedFlags: CharArray,
    override val widthAction: PartAction,
    override val precisionAction: PartAction
) : ConversionChecking {
    override val canTakeArguments: Boolean
        get() = true

    override fun checkFlag(str: FormatString, flag: Char): Boolean {
        return flag in supportedFlags
    }
}

internal abstract class ConversionExecutingNotNull(
    private val supportedFlags: CharArray,
    override val widthAction: PartAction,
    override val precisionAction: PartAction
) : ConversionChecking {
    override val canTakeArguments: Boolean
        get() = true

    override fun checkFlag(str: FormatString, flag: Char): Boolean {
        return flag in supportedFlags
    }

    override fun formatTo(to: Appendable, str: FormatString, taker: ArgumentTaker) {
        val arg = taker.take()
        if (arg == null) {
            to.append("null")
        } else {
            formatTo(to, str, arg)
        }
    }

    abstract fun formatTo(to: Appendable, str: FormatString, arg: Any)
}