package dev.datlag.tolgee.model

/**
 * Represents a format for Tolgee localization files.
 *
 * This sealed class encapsulates predefined formats and allows for custom formats by using [Custom].
 * It implements [CharSequence], enabling the format value to be used as a string-like object.
 *
 * @property value The string representation of the format.
 */
sealed class Format(open val value: String) : CharSequence {

    /**
     * The length of the format's string representation.
     */
    override val length: Int
        get() = value.length

    /**
     * Returns the character at the specified [index] in the format's string representation.
     *
     * @param index The position of the character to retrieve.
     * @return The character at the given index.
     * @throws IndexOutOfBoundsException If [index] is out of bounds.
     */
    override operator fun get(index: Int): Char {
        return value[index]
    }

    /**
     * Returns a subsequence of the format's string representation from [startIndex] (inclusive)
     * to [endIndex] (exclusive).
     *
     * @param startIndex The start index of the subsequence, inclusive.
     * @param endIndex The end index of the subsequence, exclusive.
     * @return A subsequence of the string representation.
     * @throws IndexOutOfBoundsException If [startIndex] or [endIndex] is out of bounds.
     */
    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
        return value.subSequence(startIndex, endIndex)
    }

    /**
     * Returns the string representation of the format.
     *
     * @return The format's value as a [String].
     */
    override fun toString(): String {
        return value
    }

    /**
     * Predefined format for Compose Multiplatform XML localization files.
     */
    object ComposeXML : Format("COMPOSE_XML")

    /**
     * Predefined format for Android XML localization files.
     */
    object AndroidXML : Format("ANDROID_XML")

    /**
     * Predefined format for PO (Portable Object) localization files.
     */
    object Po : Format("PO")

    /**
     * Custom format for Tolgee localization.
     *
     * This data class allows specifying a custom string value for a format.
     *
     * @property value The custom string representation of the format.
     */
    data class Custom(override val value: String) : Format(value)
}