package io.tolgee.api

import de.comahe.i18n4k.forLocaleTag
import de.comahe.i18n4k.language
import dev.datlag.tooling.async.suspendCatching
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.tolgee.Tolgee
import io.tolgee.common.keyData
import io.tolgee.common.stringValue
import io.tolgee.model.TolgeeKey
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
     * Retrieves translations using the Tolgee API or falls back to a CDN if necessary.
     *
     * This method fetches translations from the Tolgee server with pagination support.
     * If the API key is not provided or translations are not available, it defaults to
     * fetching translations from the CDN.
     *
     * @param client The HTTP client used to make requests to the Tolgee API.
     * @param config The configuration object containing API URL, project ID, and API key.
     * @param currentLanguage The language to fetch translations for. This is optional.
     * @return A [TolgeeTranslation] object containing the retrieved translations.
     */
    suspend fun getTranslations(
        client: HttpClient,
        config: Tolgee.Config,
        currentLanguage: String?
    ): TolgeeTranslation {
        return getTranslationFromCDN(client, config, currentLanguage)
    }

    /**
     * Retrieves translations from a Content Delivery Network (CDN) based on the specified configuration and language.
     *
     * This method fetches the translation file corresponding to a given language from the CDN URL provided in the configuration.
     * If no valid language or URL is specified, a fallback empty translation object is returned.
     *
     * @param client The HTTP client used to perform the network request.
     * @param config The Tolgee configuration object containing CDN-related settings and fallback locale.
     * @param currentLanguage The current language code for which translations are being retrieved. If null, fallback mechanisms are applied.
     * @return A [TolgeeTranslation] object containing the retrieved translations, or a fallback empty translation object if no valid translations are available.
     */
    suspend fun getTranslationFromCDN(
        client: HttpClient,
        config: Tolgee.Config,
        currentLanguage: String?
    ): TolgeeTranslation {
        val baseUrl = config.contentDelivery.url?.ifBlank { null } ?: return TranslationEmpty
        val language = currentLanguage?.ifBlank { null }
            ?: config.locale?.language?.ifBlank { null }
            ?: Tolgee.systemLocale.language.ifBlank { null }
            ?: return TranslationEmpty

        val start = if (baseUrl.endsWith('/')) baseUrl else "$baseUrl/"
        val response = client.get("$start$language.json") {
            headers {
                append("sdkType", Tolgee.TYPE_HEADER)
                append("sdkVersion", Tolgee.VERSION_HEADER)
            }
        }.takeIf {
            it.status.isSuccess()
        } ?: return TranslationEmpty

        val decoded = suspendCatching {
            json.decodeFromString<Map<String, JsonElement>>(response.readRawBytes().decodeToString())
        }.getOrNull() ?: return TranslationEmpty

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
}
