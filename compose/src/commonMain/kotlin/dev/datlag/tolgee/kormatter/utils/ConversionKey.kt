package dev.datlag.tolgee.kormatter.utils

@ConsistentCopyVisibility
data class ConversionKey internal constructor(
    val prefix: Char?,
    val conversion: Char
) {
    constructor(conversion: Char) : this(null, conversion)

    fun withConversion(conversion: Char) = ConversionKey(this.prefix, conversion)

    override fun toString(): String {
        return "${prefix?.toString().orEmpty()}$conversion"
    }

    companion object
}
