package dev.datlag.tolgee.kormatter.utils

@ConsistentCopyVisibility
data class FormatString internal constructor(
    val argumentIndex: Int?,
    val flags: String,
    val width: Int?,
    val precision: Int?,
    val conversion: ConversionKey,
    val start: Int,
    val endInclusive: Int,
) {
    override fun toString(): String {
        return buildString {
            append("%")
            if (argumentIndex != null) {
                append(argumentIndex).append("$")
            }
            if (width != null) {
                append(width)
            }
            if (precision != null) {
                append(".").append(precision)
            }
            append(conversion)
        }
    }

    companion object
}
