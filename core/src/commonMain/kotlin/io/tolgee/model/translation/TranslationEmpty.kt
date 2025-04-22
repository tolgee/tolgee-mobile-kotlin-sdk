package io.tolgee.model.translation

import de.comahe.i18n4k.Locale
import io.tolgee.model.TolgeeKey
import io.tolgee.model.TolgeeMessageParams
import io.tolgee.model.TolgeeTranslation
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * A singleton implementation of the [TolgeeTranslation] interface that represents an empty translation context.
 * This implementation provides no localized content and serves as a fallback or default when no other
 * specific translation logic is applied.
 */
internal object TranslationEmpty : TolgeeTranslation {
    /**
     * Contains a list of translation keys in an immutable structure.
     *
     * Each key is represented by a [TolgeeKey], which includes its ID, name, optional description,
     * and a mapping of translations by language.
     */
    override val keys: ImmutableList<TolgeeKey> = persistentListOf()

    /**
     * Retrieves the localized string for the given key and parameters in the specified locale.
     *
     * @param key The key identifying the string to be localized.
     * @param params The parameters used for formatting the localized string, represented by `TolgeeMessageParams`.
     * @param locale The locale in which the string should be localized. If null, the default locale is used.
     * @return The localized string if it exists; otherwise, null.
     */
    override fun localized(key: String, params: TolgeeMessageParams, locale: Locale?): String? {
        return null
    }

    /**
     * Checks if a specific locale exists in the collection of supported locales.
     *
     * @param locale The locale to check for existence.
     * @return `true` if the locale is supported, `false` otherwise.
     */
    override fun hasLocale(locale: Locale): Boolean {
        return false
    }
}
