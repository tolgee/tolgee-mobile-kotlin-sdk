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

internal data object TolgeeApi {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    suspend fun getAllProjectLanguages(client: HttpClient, config: Tolgee.Config): ImmutableSet<TolgeeProjectLanguage> {
        val apiKey = config.apiKey ?: return persistentSetOf()
        val response = client.get(buildProjectUrl(config.apiUrl, config.projectId, "projects/languages")) {
            headers {
                append("X-Api-Key", apiKey)
            }
        }.takeIf { it.status.isSuccess() } ?: return persistentSetOf()

        return suspendCatching {
            json.decodeFromString<TolgeePagedResponse<TolgeeProjectLanguage.PagedWrapper>>(
                response.bodyAsText()
            )
        }.getOrNull()?.embedded?.languages?.toImmutableSet() ?: persistentSetOf()
    }

    suspend fun getTranslations(
        client: HttpClient,
        config: Tolgee.Config,
        currentLanguage: String?
    ): TolgeeTranslation {
        if (config.cdn.use) {
            return getTranslationFromCDN(client, config, currentLanguage)
        }

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

            suspendCatching {
                json.decodeFromString<TolgeePagedResponse<TolgeeKey.PagedWrapper>>(response.bodyAsText())
            }.getOrNull()?.let {
                totalPages = it.page?.totalPages ?: totalPages

                it.embedded.keys.let(allTranslations::addAll)
            }
            currentPage++
        }

        return TranslationICU(
            keys = allTranslations.toImmutableList()
        )
    }

    suspend fun getTranslationFromCDN(
        client: HttpClient,
        config: Tolgee.Config,
        currentLanguage: String?
    ): TolgeeTranslation {
        if (!config.cdn.use) {
            return TranslationEmpty
        }

        val baseUrl = config.cdn.url?.ifBlank { null } ?: return TranslationEmpty
        val language = currentLanguage?.ifBlank { null }
            ?: config.locale?.language?.ifBlank { null }
            ?: Tolgee.systemLocale.language.ifBlank { null }
            ?: return TranslationEmpty

        val start = if (baseUrl.endsWith('/')) baseUrl else "$baseUrl/"
        val response = client.get("$start$language.json".also { println("Full URL: $it") }).takeIf {
            it.status.isSuccess()
        } ?: return TranslationEmpty

        val decoded = suspendCatching {
            response.body<Map<String, JsonElement>>()
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
            formatter = config.cdn.formatter,
            usedLocale = forLocaleTag(language),
        )
    }

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