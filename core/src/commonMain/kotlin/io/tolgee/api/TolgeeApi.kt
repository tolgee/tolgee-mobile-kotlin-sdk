package io.tolgee.api

import de.comahe.i18n4k.forLocaleTag
import de.comahe.i18n4k.toTag
import dev.datlag.tooling.async.suspendCatching
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.core.toByteArray
import io.tolgee.Tolgee
import io.tolgee.common.keyData
import io.tolgee.model.TolgeeKey
import io.tolgee.model.TolgeeManifest
import io.tolgee.model.TolgeeTranslation
import io.tolgee.model.translation.TranslationEmpty
import kotlinx.collections.immutable.toImmutableList
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

/**
 * Singleton object for handling API interactions with the Tolgee service.
 * Provides functionality to fetch configured project languages, manage translations,
 * and handle fallback mechanisms such as utilizing the content delivery network (CDN) for translations.
 */
internal data object TolgeeApi {

    /**
     * A configured instance of the `Json` class from Kotlinx.serialization.
     *
     * This instance is used to parse JSON responses while interacting with the Tolgee API.
     *
     * The following configurations are applied:
     * - `ignoreUnknownKeys`: Allows parsing of JSON even when there are unknown fields in the data, ensuring backward compatibility.
     * - `isLenient`: Enables lenient parsing, allowing more flexible handling of the input JSON, such as unquoted strings or relaxed syntax.
     */
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    /**
     * Retrieves translations from the CDN.
     *
     * @param client The HTTP client used to make requests to the CDN.
     * @param config The configuration object containing API URL, project ID, and API key.
     * @param currentLanguage The language to fetch translations for. This is optional.
     * @return A [TolgeeTranslation] object containing the retrieved translations.
     */
    suspend fun getTranslations(
        client: HttpClient,
        config: Tolgee.Config,
        currentLanguage: String?
    ): TolgeeTranslation {
        val storage = config.contentDelivery.storage
        val language = currentLanguage?.ifBlank { null }
            ?: config.locale?.toTag("-")?.ifBlank { null }
            ?: Tolgee.systemLocale.toTag("-").ifBlank { null }
            ?: return TranslationEmpty
        val path = config.contentDelivery.path(language)

        val fresh = getTranslationFromCDN(client, config, path)
        val decoded = fresh?.decodeTranslation(config, language)

        if (decoded != null) {
            storage?.put(path, fresh.toByteArray())
            return decoded
        }

        val cached = storage?.get(path)?.decodeToString()?.decodeTranslation(config, language)
        return cached ?: TranslationEmpty
    }

    /**
     * Retrieves project manifest from the Content Delivery Network (CDN).
     *
     * @param client The HTTP client used to perform the network request.
     * @param config The Tolgee configuration object containing CDN-related settings.
     * @return A [TolgeeManifest] object containing available locales, or null if fetch fails.
     */
    suspend fun getManifest(
        client: HttpClient,
        config: Tolgee.Config,
    ): TolgeeManifest {
        val storage = config.contentDelivery.storage
        val path = config.contentDelivery.manifestPath

        // Try to fetch fresh manifest from CDN
        val fresh = getTranslationFromCDN(client, config, path)
        val decoded = fresh?.decodeManifest()

        if (decoded != null) {
            // Cache the fresh manifest to storage
            storage?.put(path, fresh.toByteArray())
            return decoded
        }

        val cached = storage?.get(path)?.decodeToString()?.decodeManifest()
        return cached ?: getFallbackManifest()
    }

    /**
     * Retrieves a fallback instance of [TolgeeManifest]. The fallback instance disables locale
     * fallback mechanism.
     *
     * This method is used when manifest fetching fails, no further attempts to fetch manifest
     * are made during the application's lifecycle. By setting the `locales` to `null`, it disables
     * the locale fallback mechanism, where translations fallback from a regional variant to the base
     * language (e.g., "en-US" to "en").
     *
     * @return A [TolgeeManifest] instance with `locales` set to `null`, effectively disabling
     *         all locale fallback logic.
     */
    private fun getFallbackManifest(): TolgeeManifest {
        return TolgeeManifest(locales = null)
    }

    /**
     * Decodes a localization file's content in JSON format into a `TolgeeTranslation` object.
     *
     * @param config The configuration object used for content delivery and formatting.
     * @param language The language code for the language being processed.
     * @return A `TolgeeTranslation` object containing parsed keys and translations, or `null`
     *         if the decoding process fails or the input is invalid.
     */
    private suspend fun String.decodeTranslation(
        config: Tolgee.Config,
        language: String,
    ): TolgeeTranslation? {
        val decoded = suspendCatching {
            json.decodeFromString<Map<String, JsonElement>>(this@decodeTranslation)
        }.getOrNull() ?: return null

        return TolgeeTranslation(
            keys = decoded.map { (key, value) ->
                TolgeeKey(
                    keyName = key,
                    translations = mapOf(language to value.keyData())
                )
            }.toImmutableList(),
            formatter = config.contentDelivery.formatter,
            usedLocale = forLocaleTag(language),
        )
    }

    /**
     * Decodes a JSON string into a `TolgeeManifest` object.
     *
     * @return The decoded manifest, or null if parsing fails.
     */
    private fun String.decodeManifest(): TolgeeManifest? {
        return try {
            json.decodeFromString<TolgeeManifest>(this@decodeManifest)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Retrieves translations from a Content Delivery Network (CDN) based on the specified configuration and language.
     *
     * This method fetches the translation file corresponding to a given language from the CDN URL provided in the configuration.
     * If no valid language or URL is specified, a fallback empty translation object is returned.
     *
     * @param client The HTTP client used to perform the network request.
     * @param config The Tolgee configuration object containing CDN-related settings and fallback locale.
     * @param path The path to the translation file within the CDN url.
     * @return A [TolgeeTranslation] object containing the retrieved translations, or a fallback empty translation object if no valid translations are available.
     */
    suspend fun getTranslationFromCDN(
        client: HttpClient,
        config: Tolgee.Config,
        path: String
    ): String? {
        val baseUrl = config.contentDelivery.url?.ifBlank { null } ?: return null

        val start = if (baseUrl.endsWith('/')) baseUrl else "$baseUrl/"
        val response = client.get("$start$path") {
            headers {
                append("sdkType", Tolgee.TYPE_HEADER)
                append("sdkVersion", Tolgee.VERSION_HEADER)
            }
        }.takeIf {
            it.status.isSuccess()
        } ?: return null

        return response.readRawBytes().decodeToString()
    }
}
