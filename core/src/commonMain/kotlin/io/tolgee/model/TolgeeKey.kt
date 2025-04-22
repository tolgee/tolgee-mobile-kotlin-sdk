package io.tolgee.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a translation key, including its associated information and translations.
 *
 * This data class is used within the Tolgee platform to store and manage keys, which can be
 * localized into multiple languages. Each key is identified by an ID and has a name, an optional
 * description, and a set of translations mapped by language.
 *
 * @property keyId The unique identifier for the translation key.
 * @property keyName The name of the key.
 * @property keyDescription An optional description for the key.
 * @property translations A map containing translations for the key, where the key is the language
 *                        and the value is a `Translation`.
 */
@Serializable
internal data class TolgeeKey(
    @SerialName("keyId") val keyId: Int,
    @SerialName("keyName") val keyName: String,
    @SerialName("keyDescription") val keyDescription: String? = null,
    @SerialName("translations") val translations: Map<String, Translation>
) {

    /**
     * Retrieves a translation for a given language code or the first available non-null translation.
     *
     * @param language The language code for which the translation is requested.
     *                 If null or blank, the method will attempt to return the first available non-null translation.
     * @return The corresponding translation for the provided language code, or the first non-null translation if the language is not available.
     */
    internal fun translationForOrFirst(language: String?) = language?.ifBlank { null }?.let(translations::get)
        ?: translations.firstNotNullOfOrNull { it.value }

    /**
     * Represents a single translation entry for a specific language.
     *
     * This data class is a part of the [TolgeeKey] structure, where each key can have multiple translations
     * associated with different languages. Each translation contains the translated text or can be null
     * if the text for the associated language is not defined.
     *
     * @property text The translated text for the specific language, or null if not available.
     */
    @Serializable
    internal data class Translation(
        @SerialName("text") val text: String?,
    )

    /**
     * Represents a wrapper for paginated Tolgee keys.
     * This class is used to encapsulate a list of Tolgee keys
     * in a paged response structure.
     *
     * @property keys A list of `TolgeeKey` objects, each representing a key with its associated metadata
     * and translations.
     */
    @Serializable
    data class PagedWrapper(
        @SerialName("keys") val keys: List<TolgeeKey>
    )
}
