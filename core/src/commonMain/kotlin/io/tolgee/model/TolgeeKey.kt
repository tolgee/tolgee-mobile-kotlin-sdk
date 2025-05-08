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
 * @property keyName The name of the key.
 * @property translations A map containing translations for the key, where the key is the language
 *                        and the value is a `Translation`.
 */
@Serializable
internal data class TolgeeKey(
    @SerialName("keyName") val keyName: String,
    @SerialName("translations") val translations: Map<String, String>
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
}
