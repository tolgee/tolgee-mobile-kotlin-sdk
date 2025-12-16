package io.tolgee.model.translation

import de.comahe.i18n4k.Locale
import de.comahe.i18n4k.cldr.plurals.PluralCategory
import de.comahe.i18n4k.cldr.plurals.PluralRule
import de.comahe.i18n4k.cldr.plurals.PluralRuleType
import de.comahe.i18n4k.toTag
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

    private val stringArrayKeys = keys.filter { it.isArray }

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

        return when (val data = requestedKey.translationForOrFirst(
            locale?.toTag("-")?.ifBlank { null }
                ?: this.usedLocale?.toTag("-")?.ifBlank { null }
        )) {
            is TolgeeKey.Data.Text -> return data.text.sprintf(*args)
            is TolgeeKey.Data.Plural -> {
                val selected = data.plurals[getPluralName(args.getOrNull(0))]
                return selected?.sprintf(*args)
            }
            else -> null
        }
    }

    fun getPluralName(number: Any?): String {
        if (number == null) return PluralCategory.OTHER.id
        val locale = usedLocale ?: return PluralCategory.OTHER.id

        val pluralRule = PluralRule.create(locale, PluralRuleType.CARDINAL)

        val pluralCategory = when (number) {
            is Number -> pluralRule?.select(number)
            is String -> pluralRule?.select(number)
            else -> PluralCategory.OTHER
        } ?: PluralCategory.OTHER

        return pluralCategory.id
    }

    /**
     * Checks if the specified locale matches the currently used locale or has the same language as the used locale.
     *
     * @param locale The locale to check.
     * @return `true` if the given locale matches the currently used locale or shares the same language; `false` otherwise.
     */
    override fun hasLocale(locale: Locale): Boolean {
        return locale == this.usedLocale || locale.toTag("-") == this.usedLocale?.toTag("-")
    }

    override fun stringArray(key: String, locale: Locale?): List<String> {
        val foundTolgeeKey = stringArrayKeys.firstOrNull { it.keyName == key } ?: return emptyList()
        return when (val data = foundTolgeeKey.translationForOrFirst(locale?.toTag("-"))) {
            is TolgeeKey.Data.Array -> data.array
            is TolgeeKey.Data.Plural -> data.plurals.map { it.value }
            is TolgeeKey.Data.Text -> listOf(data.text)
            else -> emptyList()
        }
    }
}
