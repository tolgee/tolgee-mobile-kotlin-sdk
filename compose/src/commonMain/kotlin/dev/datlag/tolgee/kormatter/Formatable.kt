package dev.datlag.tolgee.kormatter

import dev.datlag.tolgee.kormatter.utils.FormatString

interface Formatable {
    fun formatTo(to: Appendable, str: FormatString)

    companion object
}