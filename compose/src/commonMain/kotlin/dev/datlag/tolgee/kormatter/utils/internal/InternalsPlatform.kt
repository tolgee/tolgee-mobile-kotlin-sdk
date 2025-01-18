package dev.datlag.tolgee.kormatter.utils.internal

internal expect val lineSeparator: String

internal fun CharSequence.lengthSequence(length: Int): CharSequence {
    if (this is StringBuilder) {
        setLength(length)
        return this
    }
    return subSequence(0, length)
}