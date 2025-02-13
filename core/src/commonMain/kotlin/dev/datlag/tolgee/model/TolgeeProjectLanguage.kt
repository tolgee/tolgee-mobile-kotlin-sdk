package dev.datlag.tolgee.model

import de.comahe.i18n4k.forLocaleTag
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a language configuration within a Tolgee project.
 *
 * This data class defines properties and functionality for managing a language associated
 * with a specific Tolgee project. Each instance includes details such as the language's name,
 * tag, original name, flag emoji, and whether it serves as the base language.
 *
 * @property name The name of the language (e.g., "English").
 * @property tag The language's unique tag identifier, typically in the format of a locale (e.g., "en-US").
 * @property originalName The original name of the language in its native form, if available.
 * @property flagEmoji The emoji representing the country's or language's flag, if available.
 * @property base Indicates whether this language is the project's base language.
 */
@Serializable
@ConsistentCopyVisibility
data class TolgeeProjectLanguage internal constructor(
    @SerialName("name") val name: String,
    @SerialName("tag") val tag: String,
    @SerialName("originalName") val originalName: String? = null,
    @SerialName("flagEmoji") val flagEmoji: String? = null,
    @SerialName("base") val base: Boolean
) {

    /**
     * Converts the associated language tag of this `TolgeeProjectLanguage` instance into a `Locale`.
     *
     * This function utilizes the `tag` property of the `TolgeeProjectLanguage` to generate a `Locale`
     * object that corresponds to the specified tag. This is useful for working with localization and
     * internationalization features where locale-specific data is required.
     *
     * @return A `Locale` object corresponding to the language tag of this instance.
     */
    fun asLocale() = forLocaleTag(tag)

    /**
     * Represents a paginated wrapper for a list of Tolgee project languages.
     * This class is used to encapsulate a list of languages in a paged response structure.
     *
     * @property languages A list of `TolgeeProjectLanguage` objects, each representing a language
     * used in a Tolgee project, including its metadata and related attributes.
     */
    @Serializable
    internal data class PagedWrapper(
        @SerialName("languages") val languages: List<TolgeeProjectLanguage>
    )
}
