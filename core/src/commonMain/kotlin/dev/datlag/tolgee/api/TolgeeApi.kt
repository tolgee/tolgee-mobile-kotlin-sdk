package dev.datlag.tolgee.api

import de.jensklingenberg.ktorfit.ktorfit
import dev.datlag.tolgee.Tolgee
import dev.datlag.tolgee.model.TolgeePagedResponse
import dev.datlag.tolgee.model.TolgeeKey
import dev.datlag.tolgee.model.TolgeeProjectLanguage
import dev.datlag.tolgee.model.TolgeeTranslation
import dev.datlag.tooling.async.suspendCatching
import io.ktor.client.*
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
        val api = requestsInstance(client, config)
        val response = requestByIdOrApiKey(
            config = config,
            projectIdRequest = { api.allProjectLanguages(config.apiKey, it) },
            fallbackRequest = { api.allProjectLanguages(config.apiKey) }
        ) ?: return persistentSetOf()

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

        val api = requestsInstance(client, config)
        val allTranslations = mutableListOf<TolgeeKey>()
        var currentPage = 0
        var totalPages = 1

        while (currentPage < totalPages) {
            val response = requestByIdOrApiKey(
                config = config,
                projectIdRequest = { api.translations(
                    apiKey = config.apiKey,
                    projectId = it,
                    page = currentPage,
                    size = 20,
                    sort = "keyId,asc",
                    languages = currentLanguage,
                ) },
                fallbackRequest = { api.translations(
                    apiKey = config.apiKey,
                    page = currentPage,
                    size = 20,
                    sort = "keyId,asc",
                    languages = currentLanguage,
                ) }
            ) ?: break

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

    private suspend fun requestByIdOrApiKey(
        config: Tolgee.Config,
        projectIdRequest: suspend (String) -> HttpResponse,
        fallbackRequest: suspend () -> HttpResponse
    ): HttpResponse? {
        return suspendCatching {
            config.projectId?.let {
                projectIdRequest(it).takeIf { res -> res.status.isSuccess() }
            }
        }.getOrNull() ?: suspendCatching {
            fallbackRequest().takeIf { res -> res.status.isSuccess() }
        }.getOrNull()
    }

    private fun requestsInstance(client: HttpClient, config: Tolgee.Config): TolgeeRequests {
        return ktorfit {
            baseUrl(config.apiUrl)

            httpClient(client)
        }.createTolgeeRequests()
    }
}