package dev.datlag.tolgee.model

sealed class Format(open val value: String) : CharSequence {

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

    object ComposeXML : Format("COMPOSE_XML")
    object AndroidXML : Format("ANDROID_XML")
    object Po : Format("PO")
    data class Custom(override val value: String) : Format(value)
}