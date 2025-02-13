package dev.datlag.tolgee.api

import de.comahe.i18n4k.forLocaleTag
import de.comahe.i18n4k.language
import dev.datlag.tolgee.Tolgee
import dev.datlag.tolgee.common.stringValue
import dev.datlag.tolgee.model.*
import dev.datlag.tolgee.model.TolgeeKey
import dev.datlag.tolgee.model.TolgeePagedResponse
import dev.datlag.tolgee.model.TolgeeTranslation
import dev.datlag.tolgee.model.translation.TranslationEmpty
import dev.datlag.tolgee.model.translation.TranslationICU
import dev.datlag.tooling.async.suspendCatching
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.collections.immutable.*
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
     * Retrieves all languages associated with a specific project using the provided HTTP client and configuration.
     *
     * @param client The HttpClient used to send the request to the Tolgee API.
     * @param config The configuration object containing project-specific details such as API key, URL, and project ID.
     * @return An immutable set of project languages, or an empty set if the API key is missing or the request fails.
     */
    suspend fun getAllProjectLanguages(client: HttpClient, config: Tolgee.Config): ImmutableSet<TolgeeProjectLanguage> {
        val apiKey = config.apiKey ?: return persistentSetOf()
        val response = client.get(buildProjectUrl(config.apiUrl, config.projectId, "projects/languages")) {
            headers {
                append("X-Api-Key", apiKey)
            }
        }.takeIf { it.status.isSuccess() } ?: return persistentSetOf()

        val decoded = suspendCatching {
            response.body<TolgeePagedResponse<TolgeeProjectLanguage.PagedWrapper>>()
        }.getOrNull() ?: suspendCatching {
            json.decodeFromString<TolgeePagedResponse<TolgeeProjectLanguage.PagedWrapper>>(
                response.bodyAsText()
            )
        }.getOrNull()

        return decoded?.embedded?.languages?.toImmutableSet() ?: persistentSetOf()
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
        val apiKey = config.apiKey ?: return getTranslationFromCDN(client, config, currentLanguage)
        val baseUrl = buildProjectUrl(config.apiUrl, config.projectId, "projects/translations")
        val allTranslations = mutableListOf<TolgeeKey>()
        var currentPage = 0
        var totalPages = 1

        while (currentPage < totalPages) {
            val response = client.get(baseUrl) {
                url {
                    parameter("page", currentPage)
                    parameter("size", 20)
                    parameter("sort", "keyId,asc")
                    currentLanguage?.let { parameter("languages", it) }
                }
                headers {
                    append("X-Api-Key", apiKey)
                }
            }.takeIf {
                it.status.isSuccess()
            } ?: break

            val decoded = suspendCatching {
                response.body<TolgeePagedResponse<TolgeeKey.PagedWrapper>>()
            }.getOrNull() ?: suspendCatching {
                json.decodeFromString<TolgeePagedResponse<TolgeeKey.PagedWrapper>>(response.bodyAsText())
            }.getOrNull()

            decoded?.let {
                totalPages = it.page?.totalPages ?: totalPages

                it.embedded.keys.let(allTranslations::addAll)
            }
            currentPage++
        }

        return if (allTranslations.isEmpty()) {
            getTranslationFromCDN(client, config, currentLanguage)
        } else {
            TranslationICU(keys = allTranslations.toImmutableList())
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
        val response = client.get("$start$language.json").takeIf {
            it.status.isSuccess()
        } ?: return TranslationEmpty

        val decoded = suspendCatching {
            json.decodeFromString<Map<String, JsonElement>>(response.bodyAsText())
        }.getOrNull() ?: suspendCatching {
            json.decodeFromString<Map<String, JsonElement>>(response.readRawBytes().decodeToString())
        }.getOrNull() ?: return TranslationEmpty

        return TolgeeTranslation(
            keys = decoded.map { (key, value) ->
                TolgeeKey(
                    keyId = key.hashCode(),
                    keyName = key,
                    translations = mapOf(language to TolgeeKey.Translation(value.stringValue()))
                )
            }.toImmutableList(),
            formatter = config.contentDelivery.formatter,
            usedLocale = forLocaleTag(language),
        )
    }

    /**
     * Constructs a complete URL for a project by combining the base URL, project ID (if provided),
     * and a specific path. Ensures the proper formatting of slashes between components of the URL.
     *
     * @param base The base URL of the project API.
     * @param projectId The optional ID of the project. If null or blank, it will not be included in the URL.
     * @param path The specific path to append to the base and project components.
     * @return The constructed URL as a string.
     */
    private fun buildProjectUrl(base: String, projectId: String?, path: String): String {
        val start = if (base.endsWith('/')) {
            base
        } else {
            "$base/"
        }

        val project = if (projectId.isNullOrBlank()) {
            start
        } else {
            "$start${projectId.encodeURLPath()}/"
        }

        return if (path.startsWith('/')) {
            "$project${path.substring(1)}"
        } else {
            "$project$path"
        }
    }
}