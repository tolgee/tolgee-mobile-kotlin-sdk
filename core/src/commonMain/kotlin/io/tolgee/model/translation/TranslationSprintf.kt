package io.tolgee.model.translation

import de.comahe.i18n4k.Locale
import de.comahe.i18n4k.language
import io.tolgee.common.sprintf
import io.tolgee.model.TolgeeKey
import io.tolgee.model.TolgeeMessageParams
import io.tolgee.model.TolgeeTranslation
import kotlinx.collections.immutable.ImmutableList

/**
 * A data class implementing the [TolgeeTranslation] interface, providing translation functionality
 * based on the sprintf formatting convention.
 *
 * The `TranslationSprintf` class is responsible for retrieving and formatting localized translations
 * using sprintf-style arguments. It supports indexed parameters and enforces the use of valid indexed
 * format arguments during translation.
 *
 * @property keys A list of immutable [TolgeeKey] objects, each containing the key definitions and their translations.
 * @property usedLocale An optional locale to be used as the default for resolving translations.
 */
internal data class TranslationSprintf(
    override val keys: ImmutableList<TolgeeKey>,
    private var usedLocale: Locale?,
) : TolgeeTranslation {

    /**
     * Retrieves a localized string for the specified key and formatting parameters, considering a given locale.
     *
     * @param key The key identifying the string to be localized.
     * @param params The parameters used to format the localized string, represented by [TolgeeMessageParams].
     * @param locale The locale in which the string should be localized. Defaults to the previously used locale if `null`.
     * @return The localized and formatted string if found; `null` otherwise.
     */
    override fun localized(key: String, params: TolgeeMessageParams, locale: Locale?): String? {
        val requestedKey = keys.firstOrNull { it.keyName == key } ?: return null

        if (usedLocale == null) {
            usedLocale = locale
        }

        val args = when (params) {
            is TolgeeMessageParams.None -> arrayOf<Any>()
            is TolgeeMessageParams.Indexed -> params.argList.toTypedArray()
            else -> throw IllegalArgumentException("Only indexed or none parameters are supported when using sprintf format.")
        }

        return requestedKey.translationForOrFirst(
                  locale?.language?.ifBlank { null }
                      ?: this.usedLocale?.language?.ifBlank { null }
              )?.sprintf(*args)
    }

    /**
     * Checks if the specified locale matches the currently used locale or has the same language as the used locale.
     *
     * @param locale The locale to check.
     * @return `true` if the given locale matches the currently used locale or shares the same language; `false` otherwise.
     */
    override fun hasLocale(locale: Locale): Boolean {
        return locale == this.usedLocale || locale.language == this.usedLocale?.language
    }
}
