package io.tolgee.model.pull

/**
 * Represents the state of a translation in Tolgee.
 *
 * This sealed class is used to specify the required state of translations when pulling strings
 * from Tolgee. It supports predefined states (e.g., `UNTRANSLATED`, `TRANSLATED`, etc.) and allows
 * for custom states through the [Custom] data class.
 *
 * @property value The string representation of the state.
 */
sealed class State(open val value: String) : CharSequence {

    /**
     * The length of the state's string representation.
     */
    override val length: Int
        get() = value.length

    /**
     * Returns the character at the specified [index] in the state's string representation.
     *
     * @param index The position of the character to retrieve.
     * @return The character at the given index.
     * @throws IndexOutOfBoundsException If [index] is out of bounds.
     */
    override operator fun get(index: Int): Char {
        return value[index]
    }

    /**
     * Returns a subsequence of the state's string representation from [startIndex] (inclusive)
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
     * Returns the string representation of the state.
     *
     * @return The state's value as a [String].
     */
    override fun toString(): String {
        return value
    }

    /**
     * State representing untranslated strings.
     */
    object Untranslated : State("UNTRANSLATED")

    /**
     * State representing strings that have been translated.
     */
    object Translated : State("TRANSLATED")

    /**
     * State representing strings that have been reviewed.
     */
    object Reviewed : State("REVIEWED")

    /**
     * State representing strings that are disabled.
     */
    object Disabled : State("DISABLED")

    /**
     * Custom state for translations.
     *
     * This data class allows specifying a custom string value for a translation state.
     *
     * @property value The custom string representation of the state.
     */
    data class Custom(override val value: String) : State(value)

    companion object {
        fun from(value: String): State = when {
            value.equals(Untranslated.value, ignoreCase = true) -> Untranslated
            value.equals(Translated.value, ignoreCase = true) -> Translated
            value.equals(Reviewed.value, ignoreCase = true) -> Reviewed
            value.equals(Disabled.value, ignoreCase = true) -> Disabled
            else -> Custom(value)
        }
    }
}