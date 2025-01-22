package dev.datlag.tolgee.model.push

sealed class Mode(open val value: String): CharSequence {

    override val length: Int
        get() = value.length

    override operator fun get(index: Int): Char {
        return value[index]
    }

    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
        return subSequence(startIndex, endIndex)
    }

    override fun toString(): String {
        return value
    }

    object Override : Mode("OVERRIDE")
    object Keep : Mode("KEEP")
    object NoForce : Mode("NO_FORCE")
    data class Custom(override val value: String) : Mode(value)
}