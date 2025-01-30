package dev.datlag.tolgee.model

import de.comahe.i18n4k.Locale
import de.comahe.i18n4k.createLocale
import de.comahe.i18n4k.messages.MessageBundle
import de.comahe.i18n4k.messages.NameToIndexMapperNumbersFrom0
import de.comahe.i18n4k.messages.formatter.MessageParametersList
import de.comahe.i18n4k.messages.providers.MessagesProvider
import kotlinx.collections.immutable.ImmutableList
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

data class TolgeeTranslation internal constructor(
    private val keys: ImmutableList<TolgeeKey>,
    private val currentLocale: Locale?,
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
            override val locale: Locale = createLocale(locale)

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

    fun localized(key: String, vararg args: Any): String? {
        return groupedProviders.firstNotNullOfOrNull { it.indexOfKey(key) }?.let { index ->
            if (args.isEmpty()) {
                getLocalizedString0(key, index)
            } else {
                getLocalizedStringN(
                    key = key,
                    index = index,
                    parameters = MessageParametersList(
                        parameters = args.toList(),
                        nameMapper = NameToIndexMapperNumbersFrom0
                    )
                )
            }
        }?.toString(currentLocale)
    }

    @Serializable
    internal data class MappedTranslation(
        val name: String,
        val description: String?,
        val text: String?
    )
}