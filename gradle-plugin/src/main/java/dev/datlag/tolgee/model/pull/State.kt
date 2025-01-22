package dev.datlag.tolgee.model.pull

sealed class State(open val value: String) : CharSequence {

    override val length: Int
        get() = value.length

    override operator fun get(index: Int): Char {
        return value[index]
    }

    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
        return value.subSequence(startIndex, endIndex)
    }

    override fun toString(): String {
        return value
    }

    object Untranslated : State("UNTRANSLATED")
    object Translated : State("TRANSLATED")
    object Reviewed : State("REVIEWED")
    object Disabled : State("DISABLED")
    data class Custom(override val value: String) : State(value)
}