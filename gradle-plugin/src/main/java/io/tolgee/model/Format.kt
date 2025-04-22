package io.tolgee.model

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
     * Predefined format for JSON localization files.
     */
    sealed class JSON(override val value: String) : Format(value) {

        /**
         * Predefined format for JSON Tolgee localization files.
         */
        object Tolgee : JSON("JSON_TOLGEE")

        /**
         * Predefined format for JSON ICU localization files.
         */
        object ICU : JSON("JSON_ICU")

        /**
         * Predefined format for JSON i18next localization files.
         */
        object I18Next : JSON("JSON_I18NEXT")

        /**
         * Predefined format for JSON Java localization files.
         */
        object Java : JSON("JSON_JAVA")

        /**
         * Predefined format for JSON PHP localization files.
         */
        object PHP : JSON("JSON_PHP")

        /**
         * Predefined format for JSON Ruby localization files.
         */
        object Ruby : JSON("JSON_RUBY")

        /**
         * Predefined format for JSON C localization files.
         */
        object C : JSON("JSON_C")

    }

    /**
     * Predefined format for PO (Portable Object) localization files.
     */
    sealed class PO(override val value: String) : Format(value) {

        /**
         * Predefined format for PO (Portable Object) PHP localization files.
         */
        object PHP : PO("PO_PHP")

        /**
         * Predefined format for PO (Portable Object) C localization files.
         */
        object C : PO("PO_C")

        /**
         * Predefined format for PO (Portable Object) Java localization files.
         */
        object Java : PO("PO_JAVA")

        /**
         * Predefined format for PO (Portable Object) ICU localization files.
         */
        object ICU : PO("PO_ICU")

        /**
         * Predefined format for PO (Portable Object) Ruby localization files.
         */
        object Ruby : PO("PO_RUBY")

        /**
         * Predefined format for PO (Portable Object) Python localization files.
         */
        object Python : PO("PO_PYTHON")

    }

    /**
     * Predefined format for apple localization files.
     */
    sealed class Apple(override val value: String) : Format(value) {

        /**
         * Predefined format for apple strings localization files.
         */
        object Strings : Apple("APPLE_STRINGS")

        /**
         * Predefined format for apple xliff localization files.
         */
        object XLIFF : Apple("APPLE_XLIFF")

    }

    /**
     * Predefined format for properties localization files.
     */
    sealed class Properties(override val value: String) : Format(value) {

        /**
         * Predefined format for properties ICU localization files.
         */
        object ICU : Properties("PROPERTIES_ICU")

        /**
         * Predefined format for properties Java localization files.
         */
        object Java : Properties("PROPERTIES_JAVA")

    }

    /**
     * Custom format for Tolgee localization.
     *
     * This data class allows specifying a custom string value for a format.
     *
     * @property value The custom string representation of the format.
     */
    data class Custom(override val value: String) : Format(value)

    companion object {
        fun from(value: String): Format = when {
            value.equals(ComposeXML.value, ignoreCase = true) -> ComposeXML
            value.equals(AndroidXML.value, ignoreCase = true) -> AndroidXML
            value.equals(JSON.Tolgee.value, ignoreCase = true) -> JSON.Tolgee
            value.equals(JSON.ICU.value, ignoreCase = true) -> JSON.ICU
            value.equals(JSON.I18Next.value, ignoreCase = true) -> JSON.I18Next
            value.equals(JSON.Java.value, ignoreCase = true) -> JSON.Java
            value.equals(JSON.PHP.value, ignoreCase = true) -> JSON.PHP
            value.equals(JSON.Ruby.value, ignoreCase = true) -> JSON.Ruby
            value.equals(JSON.C.value, ignoreCase = true) -> JSON.C
            value.equals(PO.PHP.value, ignoreCase = true) -> PO.PHP
            value.equals(PO.C.value, ignoreCase = true) -> PO.C
            value.equals(PO.Java.value, ignoreCase = true) -> PO.Java
            value.equals(PO.ICU.value, ignoreCase = true) -> PO.ICU
            value.equals(PO.Ruby.value, ignoreCase = true) -> PO.Ruby
            value.equals(PO.Python.value, ignoreCase = true) -> PO.Python
            value.equals(Apple.Strings.value, ignoreCase = true) -> Apple.Strings
            value.equals(Apple.XLIFF.value, ignoreCase = true) -> Apple.XLIFF
            value.equals(Properties.ICU.value, ignoreCase = true) -> Properties.ICU
            value.equals(Properties.Java.value, ignoreCase = true) -> Properties.Java
            else -> Custom(value)
        }
    }
}