package io.tolgee.model.translation

import de.comahe.i18n4k.Locale
import de.comahe.i18n4k.forLocaleTag
import de.comahe.i18n4k.i18n4kInitCldrPluralRules
import de.comahe.i18n4k.language
import de.comahe.i18n4k.messages.MessageBundle
import de.comahe.i18n4k.messages.formatter.MessageParameters
import de.comahe.i18n4k.messages.providers.MessagesProvider
import de.comahe.i18n4k.strings.LocalizedString
import io.tolgee.model.TolgeeKey
import io.tolgee.model.TolgeeMessageParams
import io.tolgee.model.TolgeeTranslation
import kotlinx.collections.immutable.ImmutableList
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Represents a translation mechanism built on the ICU MessageFormat style.
 * This class serves as an implementation of the [TolgeeTranslation] interface,
 * leveraging a list of translation keys and locales to support localization and parameterized translations.
 *
 * @property keys A list of immutable translation keys ([TolgeeKey]), containing information
 * such as key names, descriptions, and their corresponding translations for various locales.
 */
internal data class TranslationICU(
    override val keys: ImmutableList<TolgeeKey>
) : MessageBundle(), TolgeeTranslation {

    /**
     * A grouping map that organizes translation keys and their corresponding mapped translations by locale.
     *
     * Each locale is mapped to a list of [MappedTranslation], where [MappedTranslation] contains the name,
     * description, and translated text associated with a key. The grouping is constructed by iterating over
     * all keys and their translations, then associating them with their respective locales.
     *
     * This property is transient and excluded from serialization.
     */
    @Transient
    private val groupedKeysByLocale = keys.filter { it.isText }.flatMap { key ->
        key.translations.map { (locale, translation) ->
            locale to MappedTranslation(
                name = key.keyName,
                text = (translation as? TolgeeKey.Data.Text)?.text,
            )
        }
    }.groupBy({ it.first }, { it.second })

    @Transient
    private val stringArrayKeys = keys.filter { it.isArray }

    /**
     * A collection of message providers grouped by locale. Each provider implements the [MessagesProvider] interface.
     * Each provider represents translations for a specific locale retrieved from [groupedKeysByLocale].
     *
     * This property is lazily initialized by mapping through [groupedKeysByLocale] to create
     * providers that:
     * - Define the locale they correspond to using `forLocaleTag`.
     * - Provide access to the number of translations available.
     * - Allow retrieving translations by index.
     * - Enable looking up the index of a key within the translation set.
     */
    @Transient
    private val groupedProviders = groupedKeysByLocale.map { (locale, translation) ->
        object : MessagesProvider {
            /**
             * The locale used for resolving translations in the current translation context. This property holds
             * an instance of [Locale], which represents a specific linguistic, regional, or cultural setting.
             * It is used to determine the appropriate translations to be applied for the given configuration.
             */
            override val locale: Locale = forLocaleTag(locale)

            /**
             * The size of the translations contained within this instance.
             *
             * This property returns the total count of translations. It provides information
             * about the number of available localized entries stored in the `translation` collection.
             */
            override val size: Int
                get() = translation.size

            /**
             * Retrieves the translated text at the specified index in the translation list.
             *
             * @param index The position in the translation list to retrieve the text from.
             * @return The translated text as a string at the given index, or `null` if the index is out of bounds or no text exists.
             */
            override operator fun get(index: Int): String? {
                return translation.getOrNull(index)?.text
            }

            /**
             * Finds the index of the first translation key with the specified name.
             *
             * @param key The name of the key to search for in the list of translations.
             * @return The index of the key if found, or `null` if the key does not exist.
             */
            fun indexOfKey(key: String): Int? {
                return translation.indexOfFirst { it.name == key }.takeIf { it > -1 }
            }
        }
    }

    init {
        groupedProviders.forEach { provider ->
            registerTranslation(provider)
        }
        i18n4kInitCldrPluralRules()
    }

    /**
     * Retrieves a localized string for the specified key and parameters, using the first provider
     * that successfully matches the key. The matched key index is used to fetch the localized string.
     *
     * @param key The key identifying the string to be localized.
     * @param parameters The parameters used to format the localized string.
     * @return The localized string as a `LocalizedString` if found using any provider; otherwise, `null`.
     */
    private fun localized(key: String, parameters: MessageParameters): LocalizedString? {
        return groupedProviders.firstNotNullOfOrNull { it.indexOfKey(key) }?.let { index ->
            getLocalizedStringN(
                key = key,
                index = index,
                parameters = parameters
            )
        }
    }

    /**
     * Retrieves the localized string for a given key, parameters, and locale.
     *
     * @param key The key identifying the string to be localized.
     * @param params The parameters used for formatting the localized string, represented by `TolgeeMessageParams`.
     * @param locale The locale in which the string should be localized. If null, the default locale is considered.
     * @return The localized string formatted for the specified locale if found; otherwise, null.
     */
    override fun localized(key: String, params: TolgeeMessageParams, locale: Locale?): String? {
        return localized(key, params)?.toString(locale)
    }

    /**
     * Checks if the specified locale is contained in the collection of supported locales.
     *
     * @param locale The locale to verify for presence in the supported locales.
     * @return `true` if the locale is present in the supported locales, `false` otherwise.
     */
    override fun hasLocale(locale: Locale): Boolean {
        return locales.contains(locale) || locales.any {
            it.language == locale.language
        }
    }

    override fun stringArray(key: String, locale: Locale?): List<String> {
        val foundTolgeeKey = stringArrayKeys.firstOrNull { it.keyName == key } ?: return emptyList()
        return when (val data = foundTolgeeKey.translationForOrFirst(locale?.language)) {
            is TolgeeKey.Data.Array -> data.array
            is TolgeeKey.Data.Text -> listOf(data.text)
            else -> emptyList()
        }
    }

    /**
     * A serializable data class representing a mapped translation entity within the Tolgee platform.
     *
     * This class is used to store information about a specific translation, including its name,
     * an optional description, and the actual translated text. It serves as an intermediary structure
     * in processing and organizing translation content.
     *
     * @property name The name or key identifier for the translation.
     * @property text The translated content, which may be `null` if no translation is available.
     */
    @Serializable
    internal data class MappedTranslation(
        val name: String,
        val text: String?
    )
}
