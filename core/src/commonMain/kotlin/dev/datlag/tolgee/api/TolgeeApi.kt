package dev.datlag.tolgee.api

import dev.datlag.tolgee.Tolgee
import dev.datlag.tolgee.model.TolgeePagedResponse
import dev.datlag.tolgee.model.TolgeeKey
import dev.datlag.tolgee.model.TolgeeProjectLanguage
import dev.datlag.tolgee.model.TolgeeTranslation
import dev.datlag.tooling.async.suspendCatching
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.serialization.json.Json

internal data object TolgeeApi {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    suspend fun getAllProjectLanguages(client: HttpClient, config: Tolgee.Config): ImmutableSet<TolgeeProjectLanguage> {
        val response = client.get(buildUrl(config.apiUrl, config.projectId, "projects/languages")) {
            headers {
                append("X-Api-Key", config.apiKey)
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
        val baseUrl = buildUrl(config.apiUrl, config.projectId, "projects/translations")
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
                    append("X-Api-Key", config.apiKey)
                }
            }.takeIf {
                println(it.request.url.toString())
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

        return TolgeeTranslation(
            keys = allTranslations.toImmutableList()
        )
    }

    private fun buildUrl(base: String, projectId: String?, path: String): String {
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