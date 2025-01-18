package dev.datlag.tolgee.kormatter.conversions

import dev.datlag.tolgee.kormatter.utils.ArgumentTaker
import dev.datlag.tolgee.kormatter.utils.FormatString
import dev.datlag.tolgee.kormatter.utils.PartAction

interface Conversion {
    val widthAction: PartAction
    val precisionAction: PartAction
    val canTakeArguments: Boolean

    fun formatTo(to: Appendable, str: FormatString, taker: ArgumentTaker)
    fun check(str: FormatString)
}