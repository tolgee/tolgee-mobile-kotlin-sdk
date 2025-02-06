package dev.datlag.tolgee.model

import de.comahe.i18n4k.Locale
import de.comahe.i18n4k.forLocaleTag
import de.comahe.i18n4k.messages.MessageBundle
import de.comahe.i18n4k.messages.NameToIndexMapperNumbersFrom0
import de.comahe.i18n4k.messages.formatter.MessageParameters
import de.comahe.i18n4k.messages.formatter.MessageParametersList
import de.comahe.i18n4k.messages.providers.MessagesProvider
import de.comahe.i18n4k.strings.LocalizedString
import kotlinx.collections.immutable.ImmutableList
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@ConsistentCopyVisibility
data class TolgeeTranslation internal constructor(
    private val keys: ImmutableList<TolgeeKey>
) : MessageBundle() {

    @Transient
    private val groupedKeysByLocale = keys.flatMap { key ->
        key.translations.map { (locale, translation) ->
            locale to MappedTranslation(
                name = key.keyName,
                description = key.keyDescription,
                text = translation.text,
            )
        }
    }.groupBy({ it.first }, { it.second })

    @Transient
    private val groupedProviders = groupedKeysByLocale.map { (locale, translation) ->
        object : MessagesProvider {
            override val locale: Locale = forLocaleTag(locale)

            override val size: Int
                get() = translation.size

            override operator fun get(index: Int): String? {
                return translation.getOrNull(index)?.text
            }

            fun indexOfKey(key: String): Int? {
                return translation.indexOfFirst { it.name == key }.takeIf { it > -1 }
            }
        }
    }

    init {
        groupedProviders.forEach { provider ->
            registerTranslation(provider)
        }
    }

    fun localized(key: String, parameters: MessageParameters): LocalizedString? {
        return groupedProviders.firstNotNullOfOrNull { it.indexOfKey(key) }?.let { index ->
            getLocalizedStringN(
                key = key,
                index = index,
                parameters = parameters
            )
        }
    }

    fun hasLocale(locale: Locale): Boolean {
        return locales.contains(locale)
    }

    @Serializable
    internal data class MappedTranslation(
        val name: String,
        val description: String?,
        val text: String?
    )
}