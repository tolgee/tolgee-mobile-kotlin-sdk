package dev.datlag.tolgee.kormatter.conversions

import dev.datlag.tolgee.kormatter.*
import dev.datlag.tolgee.kormatter.FLAG_LEFT_JUSTIFIED
import dev.datlag.tolgee.kormatter.FLAG_REUSE_ARGUMENT
import dev.datlag.tolgee.kormatter.PrecisionMismatchException
import dev.datlag.tolgee.kormatter.WidthMismatchException
import dev.datlag.tolgee.kormatter.utils.FormatString
import dev.datlag.tolgee.kormatter.utils.PartAction

interface ConversionChecking : Conversion {

    override fun check(str: FormatString) {
        if (widthAction is PartAction.FORBIDDEN && str.width != null) {
            throw WidthMismatchException(str)
        }
        if (precisionAction is PartAction.FORBIDDEN && str.precision != null) {
            throw PrecisionMismatchException(str)
        }

        for (flag in str.flags) {
            if (flag == FLAG_LEFT_JUSTIFIED && widthAction !is PartAction.FORBIDDEN) {
                continue
            }
            if (flag == FLAG_REUSE_ARGUMENT && canTakeArguments) {
                continue
            }
            if (checkFlag(str, flag)) {
                continue
            }

            throw FlagMismatchException(str, flag)
        }
    }

    fun checkFlag(str: FormatString, flag: Char): Boolean = false

    companion object
}