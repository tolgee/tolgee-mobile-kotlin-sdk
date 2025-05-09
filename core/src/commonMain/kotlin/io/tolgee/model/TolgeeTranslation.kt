package io.tolgee.model

import de.comahe.i18n4k.Locale
import io.tolgee.Tolgee
import io.tolgee.model.translation.TranslationEmpty
import io.tolgee.model.translation.TranslationICU
import io.tolgee.model.translation.TranslationSprintf
import kotlinx.collections.immutable.ImmutableList

/**
 * Interface representing a translation mechanism for the Tolgee platform.
 * Defines operations for managing and retrieving localized keys and translations.
 */
internal interface TolgeeTranslation {
    /**
     * Holds a list of translation keys represented as an immutable list of [TolgeeKey].
     * Each key within the list contains information about its ID, name, optional description,
     * and translations mapped by language.
     */
    val keys: ImmutableList<TolgeeKey>

    /**
     * Retrieves the localized translation of a given key, utilizing the provided parameters and locale.
     *
     * @param key The key identifying the translation to retrieve.
     * @param params The parameters to format the translation with, encapsulating indexed or mapped arguments.
     * @param locale The target locale for which the translation should be retrieved. Can be null for default behavior.
     * @return The localized translation as a string if found, or null otherwise.
     */
    fun localized(key: String, params: TolgeeMessageParams, locale: Locale?): String?
    /**
     * Checks if the given locale exists within the translation context.
     *
     * @param locale The locale to be checked.
     * @return `true` if the locale exists, `false` otherwise.
     */
    fun hasLocale(locale: Locale): Boolean

    fun stringArray(key: String, locale: Locale?): List<String>

    /**
     * The companion object provides factory functionality for creating instances of `TolgeeTranslation`
     * based on the given formatter. It determines the appropriate implementation to instantiate
     * (e.g., `TranslationICU`, `TranslationSprintf`, or `TranslationEmpty`) according to the
     * provided `formatter` type.
     */
    companion object {
        /**
         * Invokes the creation of a `TolgeeTranslation` implementation based on the provided formatter type.
         *
         * @param keys The list of `TolgeeKey` objects to be used for translation.
         * @param formatter The formatter defining the translation logic, such as ICU or Sprintf.
         * @param usedLocale The optional locale to be used if required for the formatter.
         * @return An instance of `TolgeeTranslation` corresponding to the formatter type.
         *         Returns `TranslationICU` for ICU formatter, `TranslationSprintf` for Sprintf formatter,
         *         or `TranslationEmpty` if no specific formatter logic applies.
         */
        internal operator fun invoke(
            keys: ImmutableList<TolgeeKey>,
            formatter: Tolgee.Formatter,
            usedLocale: Locale?,
        ): TolgeeTranslation = when (formatter) {
            is Tolgee.Formatter.ICU -> TranslationICU(keys)
            is Tolgee.Formatter.Sprintf -> TranslationSprintf(keys, usedLocale)
            else -> TranslationEmpty
        }
    }
}